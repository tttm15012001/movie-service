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

    private Integer releaseYear;

    private String searchTitle;

    private List<CategoryEnum> categories;

}
