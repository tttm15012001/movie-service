package com.ryanmovie.service.impl;

import com.ryanmovie.dto.request.CastRequest;
import com.ryanmovie.dto.response.CastResponseDto;
import com.ryanmovie.model.entity.CastModel;
import com.ryanmovie.repository.CastRepository;
import com.ryanmovie.service.CastService;
import com.ryanmovie.validation.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.ryanmovie.common.constant.DatabaseConstants.TABLE_CAST;

@Service
@Slf4j
public class CastServiceImpl implements CastService {

    private final CastRepository castRepository;

    @Autowired
    public CastServiceImpl(CastRepository castRepository) {
        this.castRepository = castRepository;
    }

    @Override
    public CastResponseDto getCastById(Long castId) {
        return this.castRepository.findById(castId)
                .map(CastModel::toCastResponseDto)
                .orElseThrow(() -> new NotFoundException(TABLE_CAST, castId));
    }

    @Override
    public CastResponseDto createCast(CastRequest castRequest) {
        CastModel castModel = CastModel.builder()
                .name(castRequest.getName())
                .nationality(castRequest.getNationality())
                .birthday(castRequest.getBirthDay())
                .build();

        return this.castRepository.save(castModel).toCastResponseDto();
    }
}
