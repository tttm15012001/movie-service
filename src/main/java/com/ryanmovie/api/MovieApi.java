package com.ryanmovie.api;

import com.ryanmovie.dto.request.MovieRequest;
import com.ryanmovie.dto.response.MovieResponseDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

import static com.ryanmovie.common.constant.ApiConstant.MOVIE_API_URL;

@RequestMapping(MOVIE_API_URL)
public interface MovieApi {
    @GetMapping(value = "/{movie-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    MovieResponseDto getMovie(@PathVariable("movie-id") Long movieId);

    @PostMapping(value = "/new", consumes = MediaType.APPLICATION_JSON_VALUE)
    MovieResponseDto createMovie(@RequestBody MovieRequest movieRequest);

    @GetMapping(value = "/{movie-name}/manifest", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getManifestByMovieName(@PathVariable("movie-name") String movieName) throws Exception;
}
