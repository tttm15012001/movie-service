package com.movieservice.controller;

import com.movieservice.api.MovieApi;
import com.movieservice.dto.request.MovieRequest;
import com.movieservice.dto.response.CategoryWithMoviesResponseDto;
import com.movieservice.dto.response.ManifestResponseDto;
import com.movieservice.dto.response.MovieResponseDto;
import com.movieservice.model.entity.CategoryModel;
import com.movieservice.service.CategoryService;
import com.movieservice.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import java.util.List;

@RestController
public class MovieController implements MovieApi {

    @Value("${config.category.top}")
    private int TOP_CATEGORIES;

    private final MovieService movieService;

    private final CategoryService categoryService;

    @Autowired
    public MovieController(
            MovieService movieService,
            CategoryService categoryService
    ) {
        this.movieService = movieService;
        this.categoryService = categoryService;
    }

    @Override
    public MovieResponseDto createMovie(@RequestBody MovieRequest movieRequest) {
        return this.movieService.createMovie(movieRequest);
    }

    @Override
    public MovieResponseDto getMovie(@PathVariable(value = "movie-id") Long movieId) {
        return this.movieService.getMovieById(movieId);
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

}