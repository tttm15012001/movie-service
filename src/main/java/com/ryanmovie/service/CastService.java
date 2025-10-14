package com.ryanmovie.service;

import com.ryanmovie.dto.request.CastRequest;
import com.ryanmovie.dto.response.CastResponseDto;

public interface CastService {

    CastResponseDto getCastById(Long castId);

    CastResponseDto createCast(CastRequest movieRequest);
}
