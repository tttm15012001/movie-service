package com.ryanmovie.model.entity;

import static com.ryanmovie.common.constant.DatabaseConstants.TABLE_CATEGORY;

import com.ryanmovie.dto.response.CategoryResponseDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TABLE_CATEGORY)
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
