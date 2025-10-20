package com.movieservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.movieservice.model.entity.CategoryEnum;
import com.movieservice.model.entity.Genre;
import com.movieservice.model.entity.MovieStatus;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequest {

    private Integer tdmbId;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date releaseDate;

    private String country;

    private String originalLanguage;

    private List<Genre> genre;

    private List<CategoryEnum> categories;

    private String director;

    private List<String> casts;

    private MovieStatus status;
}
