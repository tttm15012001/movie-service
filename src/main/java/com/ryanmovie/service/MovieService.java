package com.ryanmovie.service;

import com.ryanmovie.dto.request.MovieRequest;
import com.ryanmovie.dto.response.MovieResponseDto;
import reactor.core.publisher.Flux;

import java.util.List;

public interface MovieService {

    MovieResponseDto getMovieById(Long movieId);

    MovieResponseDto createMovie(MovieRequest movieRequest);

    Flux<List<MovieResponseDto>> getMoviesFlux(Long categoryId, int limit);

}
