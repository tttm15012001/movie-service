package com.movieservice.service.impl;

import com.movieservice.dto.request.MovieRequest;
import com.movieservice.dto.response.MovieResponseDto;
import com.movieservice.model.entity.CategoryModel;
import com.movieservice.model.entity.MovieModel;
import com.movieservice.repository.CategoryRepository;
import com.movieservice.repository.MovieRepository;
import com.movieservice.service.MovieService;
import com.movieservice.utils.StringUtil;
import com.movieservice.validation.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static com.movieservice.common.constant.DatabaseConstants.TABLE_MOVIE;

@Service
@Slf4j
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;

    private final CategoryRepository categoryRepository;

    public MovieServiceImpl(
            MovieRepository movieRepository,
            CategoryRepository categoryRepository
    ) {
        this.movieRepository = movieRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public MovieResponseDto getMovieById(Long movieId) {
        return this.movieRepository.findMovieByIdWithCategories(movieId)
                .map(MovieModel::toMovieResponseDto)
                .orElseThrow(() -> new NotFoundException(TABLE_MOVIE, movieId));
    }

    @Override
    public MovieResponseDto createMovie(MovieRequest movieRequest) {
        List<CategoryModel> categoryModels = movieRequest.getCategories().stream()
                .map(code -> categoryRepository.findByCode(code)
                        .orElseThrow(() -> new NotFoundException("category", code.toString())))
                .toList();

        MovieModel movieModel = MovieModel.builder()
                .tmdbId(movieRequest.getTdmbId())
                .forAdult(movieRequest.getForAdult())
                .title(movieRequest.getTitle())
                .originalTitle(movieRequest.getOriginalTitle())
                .description(movieRequest.getDescription())
                .numberOfEpisodes(movieRequest.getNumberOfEpisodes())
                .voteAverage(movieRequest.getVoteAverage())
                .voteCount(movieRequest.getVoteCount())
                .popularity(movieRequest.getPopularity())
                .posterPath(movieRequest.getPosterPath())
                .backdropPath(movieRequest.getBackdropPath())
                .releaseDate(movieRequest.getReleaseDate())
                .country(movieRequest.getCountry())
                .originalLanguage(movieRequest.getOriginalLanguage())
                .genre(movieRequest.getGenre())
                .categories(categoryModels)
                .director(movieRequest.getDirector())
                .casts(StringUtil.convertListToString(movieRequest.getCasts()))
                .status(movieRequest.getStatus())
                .build();

        return this.movieRepository.save(movieModel).toMovieResponseDto();
    }

    @Override
    public Flux<List<MovieResponseDto>> getMoviesFlux(Long categoryId, int limit) {
        return Mono.fromCallable(() ->
            movieRepository.findTopByCategoryOrderByRateScoreDesc(categoryId, PageRequest.of(0, limit))
                .stream()
                .map(MovieModel::toMovieResponseDto)
                .toList()
        )
        .subscribeOn(Schedulers.boundedElastic())
        .flux();
    }

}
