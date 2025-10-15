package com.ryanmovie.api;

import static com.ryanmovie.common.constant.ApiConstant.CAST_API_URL;

import com.ryanmovie.dto.request.CastRequest;
import com.ryanmovie.dto.response.CastResponseDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(CAST_API_URL)
public interface CastApi {
    @GetMapping(value = "/{cast-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    CastResponseDto getCast(@PathVariable("cast-id") Long castId);

    @PostMapping(value = "/new", consumes = MediaType.APPLICATION_JSON_VALUE)
    CastResponseDto createCast(@RequestBody CastRequest movieRequest);
}
