package com.ryanmovie.model.entity;

import com.ryanmovie.dto.response.CategoryResponseDto;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "name")
    protected String name;

    @Column(name = "score")
    protected Float score;

    public CategoryResponseDto toCategoryResponseDto() {
        return CategoryResponseDto.builder()
                .name(this.name)
                .score(this.score)
                .build();
    }
}
