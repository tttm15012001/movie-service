package com.movieservice.service.impl;

import com.movieservice.connector.MetadataServiceConnector;
import com.movieservice.dto.response.CategoryWithMoviesResponseDto;
import com.movieservice.dto.response.ManifestResponseDto;
import com.movieservice.dto.response.MovieResponseDto;
import com.movieservice.model.entity.CategoryModel;
import com.movieservice.model.entity.MovieModel;
import com.movieservice.repository.CategoryRepository;
import com.movieservice.repository.MovieRepository;
import com.movieservice.service.MovieService;
import com.movieservice.validation.exception.NotFoundException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static com.movieservice.common.constant.DatabaseConstants.TABLE_MOVIE;
import static com.movieservice.common.constant.KafkaTopicConstants.TOPIC_MOVIE_CRAWL_REQUEST;

@Service
@Slf4j
public class MovieServiceImpl implements MovieService {

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

    private final MovieRepository movieRepository;

    private final MetadataServiceConnector metadataServiceConnector;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public MovieServiceImpl(
            MovieRepository movieRepository,
            MetadataServiceConnector metadataServiceConnector,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.movieRepository = movieRepository;
        this.metadataServiceConnector = metadataServiceConnector;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public MovieModel saveMovie(MovieModel movieModel) {
        return this.movieRepository.save(movieModel);
    }

    @Override
    public Optional<MovieModel> getMovieById(Long movieId) {
        return this.movieRepository.findMovieByIdWithCategories(movieId);
    }

    @Override
    public Optional<MovieResponseDto> getMovieDetailById(Long movieId) {
        MovieModel movie = this.movieRepository.findMovieByIdWithCategories(movieId)
                .orElseThrow(() -> new NotFoundException(TABLE_MOVIE, movieId));

        MovieResponseDto.MetadataResponseDto metadata = null;
        try {
            if (movie.getMetadataId() != null) {
                metadata = this.metadataServiceConnector.getMetadata(movie.getMetadataId()).getBody();
            }
        } catch (Exception e) {
            log.error("Failed to fetch metadata for movieId={}, metadataId={}: {}",
                    movieId, movie.getMetadataId(), e.getMessage());
            if (e instanceof FeignException fe) {
                log.error("Response: {}", fe.contentUTF8());
            }
        }

        MovieResponseDto response = movie.toMovieResponseDto();
        response.setMetadata(metadata);

        return Optional.of(response);
    }

    @Override
    public void fetchMetadata(MovieModel movie) {
        Map<String, Object> payload = Map.of(
                "movieId", movie.getId(),
                "title", movie.getSearchTitle(),
                "originalLanguage", "",
                "releaseYear", movie.getReleaseYear(),
                "requestedAt", Instant.now().toString()
        );

        kafkaTemplate.send(TOPIC_MOVIE_CRAWL_REQUEST, movie.getSearchTitle(), payload);
    }

    @Override
    public ResponseEntity<ManifestResponseDto> getManifestFromMovieName(String movieName) throws Exception {
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

        return ResponseEntity.ok()
            .headers(headers)
            .body(
                ManifestResponseDto.builder()
                    .manifestUrl("https://" + DISTRIBUTION_DOMAIN + "/hls/" + movieName + "/" + movieName + ".m3u8")
                    .status(200)
                    .requestedAt(LocalDateTime.now())
                    .build()
            );
    }

    @Override
    public Flux<CategoryWithMoviesResponseDto> getTopMoviesFromCateList(List<CategoryModel> categories) {
        return Flux.fromIterable(categories)
            // flatMap = concurrent, non-blocking; not ordered
            .flatMap(category ->
                this.getMoviesFlux(category.getId(), TOP_MOVIES_PER_CATEGORY)
                    .map(movies -> CategoryWithMoviesResponseDto.builder()
                        .category(category.toCategoryResponseDto())
                        .movies(movies)
                        .build())
                    .defaultIfEmpty(CategoryWithMoviesResponseDto.builder()
                        .category(category.toCategoryResponseDto())
                        .movies(Collections.emptyList())
                        .build())
                    .onErrorResume(e -> Mono.just(
                        CategoryWithMoviesResponseDto.builder()
                            .category(category.toCategoryResponseDto())
                            .movies(Collections.emptyList())
                            .build()))
            );
    }

    private Flux<List<MovieResponseDto>> getMoviesFlux(Long categoryId, int limit) {
        return Mono.fromCallable(() ->
            movieRepository.findByPrimaryCategoryIdOrderByVoteAverageDesc(categoryId, PageRequest.of(0, limit))
                .stream()
                .map(MovieModel::toMovieResponseDto)
                .toList()
        )
        .subscribeOn(Schedulers.boundedElastic())
        .flux();
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
