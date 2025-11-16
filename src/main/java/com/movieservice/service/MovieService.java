package com.movieservice.service;

import com.movieservice.dto.response.CategoryWithMoviesResponseDto;
import com.movieservice.dto.response.ManifestResponseDto;
import com.movieservice.dto.response.MovieResponseDto;
import com.movieservice.model.entity.CategoryModel;
import com.movieservice.model.entity.MovieModel;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;

public interface MovieService {

    MovieModel saveMovie(MovieModel movieModel);

    Optional<MovieModel> getMovieById(Long movieId);

    Optional<MovieResponseDto> getMovieDetailById(Long movieId);

    void fetchMetadata(MovieModel movie);

    ResponseEntity<ManifestResponseDto> getManifestFromMovieName(String movieName) throws Exception;

    Flux<CategoryWithMoviesResponseDto> getTopMoviesFromCateList(List<CategoryModel> categories);
}
