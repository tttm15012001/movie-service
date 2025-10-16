package com.ryanmovie.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ryanmovie.model.entity.CategoryEnum;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date manufacturingDate;

    private Float rateScore;

    private Integer reviewQuantity;

    private Integer duration;

    private List<Genre> genre;

    private List<CategoryEnum> categories;

    private String country;

    private String director;

    private List<String> casts;

    private Integer episodes;

    private MovieStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date releaseDate;
}
