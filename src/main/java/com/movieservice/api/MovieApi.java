package com.movieservice.api;

import com.movieservice.dto.request.CrawlMovieRequest;
import com.movieservice.dto.request.MovieRequest;
import com.movieservice.dto.response.CategoryWithMoviesResponseDto;
import com.movieservice.dto.response.CrawlMovieResponse;
import com.movieservice.dto.response.ManifestResponseDto;
import com.movieservice.dto.response.MovieResponseDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


import java.util.List;
import java.util.Optional;

import static com.movieservice.common.constant.ApiConstant.MOVIE_API_URL;

@RequestMapping(MOVIE_API_URL)
public interface MovieApi {
    @GetMapping(value = "/{movie-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    Optional<MovieResponseDto> getMovie(@PathVariable("movie-id") Long movieId);

    @PostMapping(value = "/new", consumes = MediaType.APPLICATION_JSON_VALUE)
    MovieResponseDto createMovie(@RequestBody MovieRequest movieRequest);

    @GetMapping(value = "/{movie-name}/manifest", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ManifestResponseDto> getManifestByMovieName(@PathVariable("movie-name") String movieName) throws Exception;

    @GetMapping(value = "/top-list", produces = MediaType.APPLICATION_JSON_VALUE)
    Flux<CategoryWithMoviesResponseDto> getTopMoviesFromTopCategories();

    @PostMapping(value = "/crawl", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<CrawlMovieResponse> crawlMovie(
            @RequestHeader(value = "x-api-key") String apiKey,
            @RequestBody List<CrawlMovieRequest> movieRequest
    );
}
