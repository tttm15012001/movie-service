package com.ryanmovie.dto.response;

import com.ryanmovie.model.entity.CategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDto {

    private Long id;

    private String name;

    private CategoryEnum code;

    private Float score;
}
