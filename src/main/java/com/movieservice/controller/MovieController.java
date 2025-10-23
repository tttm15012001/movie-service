package com.movieservice.controller;

import com.movieservice.api.MovieApi;
import com.movieservice.dto.request.CrawlMovieRequest;
import com.movieservice.dto.request.MovieRequest;
import com.movieservice.dto.response.CategoryWithMoviesResponseDto;
import com.movieservice.dto.response.CrawlMovieResponse;
import com.movieservice.dto.response.ManifestResponseDto;
import com.movieservice.dto.response.MovieResponseDto;
import com.movieservice.messaging.producer.CrawlMovieRequestProducer;
import com.movieservice.model.entity.CategoryModel;
import com.movieservice.model.entity.MovieModel;
import com.movieservice.service.CategoryService;
import com.movieservice.service.MovieService;
import com.movieservice.validation.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;

import static com.movieservice.common.constant.DatabaseConstants.TABLE_MOVIE;

@RestController
@Slf4j
public class MovieController implements MovieApi {

    @Value("${config.category.top}")
    private int TOP_CATEGORIES;

    private final MovieService movieService;

    private final CategoryService categoryService;

    private final CrawlMovieRequestProducer crawlMovieProducer;

    @Autowired
    public MovieController(
            MovieService movieService,
            CategoryService categoryService,
            CrawlMovieRequestProducer crawlMovieProducer
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
        return this.movieService.getMovieById(movieId)
                .map(MovieModel::toMovieResponseDto)
                .orElseThrow(() -> new NotFoundException(TABLE_MOVIE, movieId));
    }

    @Override
    public ResponseEntity<ManifestResponseDto> getManifestByMovieName(@PathVariable("movie-name") String movieName) throws Exception {
        return movieService.getManifestFromMovieName(movieName);
    }

    @Override
    public Flux<CategoryWithMoviesResponseDto> getTopMoviesFromTopCategories() {
        List<CategoryModel> topCategories = categoryService.findTopScoreCategory(TOP_CATEGORIES);

        return movieService.getTopMoviesFromCateList(topCategories);
    }

    @Override
    public ResponseEntity<CrawlMovieResponse> crawlMovie(@RequestBody List<CrawlMovieRequest> movieRequest) {
        log.info("Received {} movies for crawling", movieRequest.size());
        crawlMovieProducer.sendCrawlRequest(movieRequest);

        CrawlMovieResponse response = CrawlMovieResponse.builder()
                .message("Movies are being processed asynchronously")
                .status("FETCHING")
                .requestedAt(LocalDateTime.now())
                .build();

        return ResponseEntity.accepted().body(response);
    }

}