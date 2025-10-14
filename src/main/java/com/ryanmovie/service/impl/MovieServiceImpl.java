package com.ryanmovie.service.impl;

import com.ryanmovie.dto.request.MovieRequest;
import com.ryanmovie.dto.response.MovieResponseDto;
import com.ryanmovie.model.entity.CastModel;
import com.ryanmovie.model.entity.MovieModel;
import com.ryanmovie.repository.CastRepository;
import com.ryanmovie.repository.MovieRepository;
import com.ryanmovie.service.MovieService;
import com.ryanmovie.validation.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static com.ryanmovie.common.constant.DatabaseConstants.TABLE_MOVIE;

@Service
@Slf4j
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;

    private final CastRepository castRepository;

    public MovieServiceImpl(
            MovieRepository movieRepository,
            CastRepository castRepository
    ) {
        this.movieRepository = movieRepository;
        this.castRepository = castRepository;
    }

    @Override
    public MovieResponseDto getMovieById(Long movieId) {
        return this.movieRepository.findById(movieId)
                .map(MovieModel::toMovieResponseDto)
                .orElseThrow(() -> new NotFoundException(TABLE_MOVIE, movieId));
    }

    @Override
    public MovieResponseDto createMovie(MovieRequest movieRequest) {
        Set<CastModel> castModels = new HashSet<>();
        if (movieRequest.getCastIds() != null && !movieRequest.getCastIds().isEmpty()) {
            castModels = new HashSet<>(castRepository.findAllById(movieRequest.getCastIds()));
        }

        MovieModel movieModel = MovieModel.builder()
                .title(movieRequest.getTitle())
                .originalTitle(movieRequest.getOriginalTitle())
                .description(movieRequest.getDescription())
                .rateScore(movieRequest.getRateScore())
                .reviewQuantity(movieRequest.getReviewQuantity())
                .duration(movieRequest.getDuration())
                .genre(movieRequest.getGenre())
                .country(movieRequest.getCountry())
                .director(movieRequest.getDirector())
                .cast(castModels)
                .episodes(movieRequest.getEpisodes())
                .status(movieRequest.getStatus())
                .releaseDate(movieRequest.getReleaseDate())
                .manufacturingDate(movieRequest.getManufacturingDate())
                .build();

        return this.movieRepository.save(movieModel).toMovieResponseDto();
    }

}
