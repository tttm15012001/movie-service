package com.movieservice.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.movieservice.common.converter.CustomDateFormat;
import com.movieservice.model.entity.MovieStatus;
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

    private String searchTitle;

    private Integer releaseYear;

    private Double voteAverage;

    private Long metadataId;

    private Integer numberOfEpisodes;

    private List<String> categories;
}
