package com.movieservice.service;

import com.movieservice.dto.request.MovieRequest;
import com.movieservice.dto.response.CategoryWithMoviesResponseDto;
import com.movieservice.dto.response.ManifestResponseDto;
import com.movieservice.dto.response.MovieResponseDto;
import com.movieservice.model.entity.CategoryModel;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;

import java.util.List;

public interface MovieService {

    MovieResponseDto getMovieById(Long movieId);

    MovieResponseDto createMovie(MovieRequest movieRequest);

    ResponseEntity<ManifestResponseDto> getManifestFromMovieName(String movieName) throws Exception;

    Flux<CategoryWithMoviesResponseDto> getTopMoviesFromCateList(List<CategoryModel> categories);
}
