package com.ryanmovie.model.entity;

import static com.ryanmovie.common.constant.DatabaseConstants.TABLE_CATEGORY;

import com.ryanmovie.dto.response.CategoryResponseDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "code")
    protected CategoryEnum code;

    @Column(name = "score")
    protected Float score;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    private List<MovieModel> movies;

    public CategoryResponseDto toCategoryResponseDto() {
        return CategoryResponseDto.builder()
                .id(this.id)
                .name(this.name)
                .code(this.code)
                .score(this.score)
                .build();
    }
}
