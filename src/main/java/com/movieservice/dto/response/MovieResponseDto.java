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

    private Integer tmdbId;

    private Boolean forAdult;

    private String title;

    private String originalTitle;

    private String description;

    private Integer numberOfEpisodes;

    private Double voteAverage;

    private Integer voteCount;

    private Double popularity;

    private String posterPath;

    private String backdropPath;

    @JsonSerialize(using = CustomDateFormat.class)
    private Date releaseDate;

    private String country;

    private String originalLanguage;

    private List<String> genre;

    private List<String> categories;

    private String director;

    private List<String> casts;

    private MovieStatus status;
}
