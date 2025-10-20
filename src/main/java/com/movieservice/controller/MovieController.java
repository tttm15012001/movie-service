package com.movieservice.controller;

import com.movieservice.api.MovieApi;
import com.movieservice.dto.request.CrawlMovieRequest;
import com.movieservice.dto.request.MovieRequest;
import com.movieservice.dto.response.CategoryResponseDto;
import com.movieservice.dto.response.CategoryWithMoviesResponseDto;
import com.movieservice.dto.response.MovieResponseDto;
import com.movieservice.messaging.producer.CrawlMovieProducer;
import com.movieservice.service.CategoryService;
import com.movieservice.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.*;

@RestController
public class MovieController implements MovieApi {

    @Value("${config.category.top}")
    private int TOP_CATEGORIES;

    @Value("${config.category.movies.top}")
    private int TOP_MOVIES_PER_CATEGORY;

    @Value("${aws.s3.presignedurl.expire.time}")
    private int expireTime;

    @Value("${aws.cloudfront.distributiondomain}")
    private String DISTRIBUTION_DOMAIN;

    @Value("${aws.cloudfront.keypairid}")
    private String KEY_PAIR_ID;

    @Value("${aws.cloudfront.privatekeyssmname}")
    private String PRIVATE_KEY_SSM_NAME;

    private final MovieService movieService;

    private final CategoryService categoryService;

    private final CrawlMovieProducer crawlMovieProducer;

    @Autowired
    public MovieController(
            MovieService movieService,
            CategoryService categoryService,
            CrawlMovieProducer crawlMovieProducer
    ) {
        this.movieService = movieService;
        this.categoryService = categoryService;
        this.crawlMovieProducer = crawlMovieProducer;
    }

    @Override
    public MovieResponseDto createMovie(@RequestBody MovieRequest movieRequest) {
        return this.movieService.createMovie(movieRequest);
    }

    @Override
    public MovieResponseDto getMovie(@PathVariable(value = "movie-id") Long movieId) {
        return this.movieService.getMovieById(movieId);
    }

    @Override
    public ResponseEntity<?> getManifestByMovieName(@PathVariable("movie-name") String movieName) throws Exception {
        Instant expireAt = Instant.now().plusSeconds(expireTime);

        String policy = String.format(
                "{\"Statement\":[{\"Resource\":\"https://%s/hls/%s/*\",\"Condition\":{\"DateLessThan\":{\"AWS:EpochTime\":%d}}}]}",
                DISTRIBUTION_DOMAIN,
                movieName,
                expireAt.getEpochSecond()
        );

        PrivateKey privateKey = loadPrivateKeyFromParameterStore(PRIVATE_KEY_SSM_NAME);
        byte[] signatureBytes = signPolicy(policy, privateKey);
        String signature = base64UrlSafe(signatureBytes);
        String encodedPolicy = base64UrlSafe(policy.getBytes());

        ResponseCookie policyCookie = ResponseCookie.from("CloudFront-Policy", encodedPolicy)
                .domain(".ryan-healthcare.com")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(expireTime)
                .build();

        ResponseCookie sigCookie = ResponseCookie.from("CloudFront-Signature", signature)
                .domain(".ryan-healthcare.com")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(expireTime)
                .build();

        ResponseCookie keyPairCookie = ResponseCookie.from("CloudFront-Key-Pair-Id", KEY_PAIR_ID)
                .domain(".ryan-healthcare.com")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(expireTime)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, policyCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, sigCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, keyPairCookie.toString());

        Map<String, String> body = new HashMap<>();
        body.put("manifestUrl", "https://" + DISTRIBUTION_DOMAIN + "/hls/" + movieName + "/" + movieName + ".m3u8");

        return ResponseEntity.ok()
                .headers(headers)
                .body(body);
    }

    @Override
    public Flux<CategoryWithMoviesResponseDto> getTopMoviesFromTopCategories() {
        List<CategoryResponseDto> topCategories = categoryService.findTopScoreCategory(TOP_CATEGORIES);

        return Flux.fromIterable(topCategories)
                // flatMap = concurrent, non-blocking; not ordered
                .flatMap(category ->
                        movieService.getMoviesFlux(category.getId(), TOP_MOVIES_PER_CATEGORY)
                                .map(movies -> CategoryWithMoviesResponseDto.builder()
                                        .category(category)
                                        .movies(movies)
                                        .build())
                                .defaultIfEmpty(CategoryWithMoviesResponseDto.builder()
                                        .category(category)
                                        .movies(Collections.emptyList())
                                        .build())
                                .onErrorResume(e -> Mono.just(
                                        CategoryWithMoviesResponseDto.builder()
                                                .category(category)
                                                .movies(Collections.emptyList())
                                                .build()))
                );
    }

    @Override
    public ResponseEntity<?> crawlMovie(@RequestBody List<CrawlMovieRequest> movieRequest) {
        this.crawlMovieProducer.sendCrawlRequest(movieRequest);

        return ResponseEntity.accepted().body(
            Map.of(
            "message", "Movies is being processed asynchronously",
            "status", "FETCHING"
            )
        );
    }

    private byte[] signPolicy(String policy, PrivateKey privateKey) throws Exception {
        Signature signer = Signature.getInstance("SHA1withRSA"); // CloudFront require SHA1withRSA
        signer.initSign(privateKey);
        signer.update(policy.getBytes());
        return signer.sign();
    }

    private String base64UrlSafe(byte[] data) {
        return Base64.getEncoder().encodeToString(data)
                .replace('+', '-')
                .replace('=', '_')
                .replace('/', '~');
    }

    private PrivateKey loadPrivateKeyFromParameterStore(String parameterName) throws Exception {
        try (SsmClient ssmClient = SsmClient.builder().build()) {
            GetParameterResponse response = ssmClient.getParameter(
                    GetParameterRequest.builder()
                            .name(parameterName)
                            .withDecryption(true)
                            .build()
            );

            String pem = response.parameter().value();

            String key = pem.replaceAll("-----BEGIN (.*)-----", "")
                    .replaceAll("-----END (.*)-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        }
    }

}