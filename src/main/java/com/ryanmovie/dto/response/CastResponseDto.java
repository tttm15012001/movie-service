package com.ryanmovie.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ryanmovie.common.converter.CustomDateFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CastResponseDto {

    private Long id;

    private String name;

    private String nationality;

    @JsonSerialize(using = CustomDateFormat.class)
    private Date birthday;
}
