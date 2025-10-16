package com.ryanmovie.dto.request;

import com.ryanmovie.model.entity.Genre;
import com.ryanmovie.model.entity.MovieStatus;
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

    private String title;

    private String originalTitle;

    private String description;

    private Date manufacturingDate;

    private Float rateScore;

    private Integer reviewQuantity;

    private Integer duration;

    private List<Genre> genre;

    private List<String> categories;

    private String country;

    private String director;

    private List<String> casts;

    private Integer episodes;

    private MovieStatus status;

    private Date releaseDate;
}
