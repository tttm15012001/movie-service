package com.ryanmovie.service.impl;

import com.ryanmovie.dto.request.MovieRequest;
import com.ryanmovie.dto.response.MovieResponseDto;
import com.ryanmovie.model.entity.CategoryModel;
import com.ryanmovie.model.entity.MovieModel;
import com.ryanmovie.repository.CategoryRepository;
import com.ryanmovie.repository.MovieRepository;
import com.ryanmovie.service.MovieService;
import com.ryanmovie.utils.StringUtil;
import com.ryanmovie.validation.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.ryanmovie.common.constant.DatabaseConstants.TABLE_MOVIE;

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
                .map(name -> categoryRepository.findByName(name)
                        .orElseThrow(() -> new NotFoundException("category", name)))
                .toList();

        MovieModel movieModel = MovieModel.builder()
                .title(movieRequest.getTitle())
                .originalTitle(movieRequest.getOriginalTitle())
                .description(movieRequest.getDescription())
                .rateScore(movieRequest.getRateScore())
                .reviewQuantity(movieRequest.getReviewQuantity())
                .duration(movieRequest.getDuration())
                .genre(movieRequest.getGenre())
                .categories(categoryModels)
                .country(movieRequest.getCountry())
                .director(movieRequest.getDirector())
                .casts(StringUtil.convertListToString(movieRequest.getCasts()))
                .episodes(movieRequest.getEpisodes())
                .status(movieRequest.getStatus())
                .releaseDate(movieRequest.getReleaseDate())
                .manufacturingDate(movieRequest.getManufacturingDate())
                .build();

        return this.movieRepository.save(movieModel).toMovieResponseDto();
    }

}
