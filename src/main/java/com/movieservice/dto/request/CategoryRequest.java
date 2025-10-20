package com.movieservice.dto.request;

import com.movieservice.model.entity.CategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    private String name;

    private CategoryEnum code;

    private Float score;
}
