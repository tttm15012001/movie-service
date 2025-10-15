package com.ryanmovie.controller;

import com.ryanmovie.api.CastApi;
import com.ryanmovie.dto.request.CastRequest;
import com.ryanmovie.dto.response.CastResponseDto;
import com.ryanmovie.service.CastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CastController implements CastApi {

    private final CastService castService;

    @Autowired
    public CastController(CastService castService) {
        this.castService = castService;
    }

    @Override
    public CastResponseDto getCast(@PathVariable("cast-id") Long castId) {
        return castService.getCastById(castId);
    }

    @Override
    public CastResponseDto createCast(@RequestBody CastRequest movieRequest) {
        return castService.createCast(movieRequest);
    }
}
