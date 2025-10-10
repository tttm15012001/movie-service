package com.ryanmovie.service.impl;

import com.ryanmovie.dto.request.MovieRequest;
import com.ryanmovie.dto.response.MovieResponseDto;
import com.ryanmovie.model.entity.MovieModel;
import com.ryanmovie.repository.MovieRepository;
import com.ryanmovie.service.MovieService;
import com.ryanmovie.validation.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ryanmovie.model.common.DatabaseConstants.TABLE_MOVIE;

@Service
@Slf4j
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;

    public MovieServiceImpl(
            MovieRepository movieRepository
    ) {
        this.movieRepository = movieRepository;
    }

    @Override
    public MovieResponseDto getMovieById(Long movieId) {
        return this.movieRepository.findById(movieId)
                .map(MovieModel::toMovieResponseDto)
                .orElseThrow(() -> new NotFoundException(TABLE_MOVIE, movieId));
    }

    @Override
    public MovieResponseDto createMovie(MovieRequest movieRequest) {
        MovieModel movieModel = MovieModel.builder()
                .title(movieRequest.getTitle())
                .description(movieRequest.getDescription())
                .build();

        return this.movieRepository.save(movieModel).toMovieResponseDto();
    }

}
