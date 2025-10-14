package com.ryanmovie.dto.request;

import com.ryanmovie.model.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;

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

    private String country;

    private String director;

    private Set<Long> castIds;

    private String episodes;

    private String status;

    private Date releaseDate;
}
