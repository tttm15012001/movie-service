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
public class MovieResponseDto {

    private Long id;

    private String title;

    private String description;

    @JsonSerialize(using = CustomDateFormat.class)
    private Date releaseDate;
}
