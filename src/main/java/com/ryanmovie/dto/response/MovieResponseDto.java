package com.ryanmovie.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ryanmovie.common.converter.CustomDateFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieResponseDto {

    private Long id;

    private String title;

    private String originalTitle;

    private String description;

    @JsonSerialize(using = CustomDateFormat.class)
    private Date manufacturingDate;

    private Float rateScore;

    private Integer reviewQuantity;

    private Integer duration;

    private List<String> genre;

    private String country;

    private String director;

    private List<String> cast;

    private String episodes;

    private String status;

    @JsonSerialize(using = CustomDateFormat.class)
    private Date releaseDate;
}
