package com.ryanmovie.service;

import com.ryanmovie.dto.request.MovieRequest;
import com.ryanmovie.dto.response.MovieResponseDto;

public interface MovieService {

    MovieResponseDto getMovieById(Long movieId);

    MovieResponseDto createMovie(MovieRequest movieRequest);

}
