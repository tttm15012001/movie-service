package com.movieservice.service.impl;

import static com.movieservice.common.constant.DatabaseConstants.TABLE_CATEGORY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieservice.dto.GenreDto;
import com.movieservice.dto.response.CategoryResponseDto;
import com.movieservice.model.entity.CategoryModel;
import com.movieservice.model.entity.Genre;
import com.movieservice.repository.CategoryRepository;
import com.movieservice.service.CategoryService;
import com.movieservice.validation.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryResponseDto getCategoryById(Long categoryId) {
        return this.categoryRepository.findById(categoryId)
                .map(CategoryModel::toCategoryResponseDto)
                .orElseThrow(() -> new NotFoundException(TABLE_CATEGORY, categoryId));
    }

    @Override
    public List<CategoryModel> handleCategory(String categoriesJson) {
        if (categoriesJson == null || categoriesJson.isBlank()) {
            return List.of();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        List<GenreDto> genreDtos;

        try {
            genreDtos = objectMapper.readValue(categoriesJson, new TypeReference<List<GenreDto>>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to parse categories JSON: {}", categoriesJson, e);
            return List.of();
        }

        return genreDtos.stream()
            .map(dto -> {
                int id = dto.getId();
                String genreName = dto.getName();
                String genreCode = Genre.getGenreCodeFromId(id);

                if (genreCode == null) {
                    log.warn("Unknown genre ID: {}", id);
                    return null;
                }

                return categoryRepository.findByCodeIgnoreCase(genreCode)
                    .orElseGet(() -> {
                        CategoryModel newCategory = CategoryModel.builder()
                            .name(genreName != null ? genreName : genreCode)
                            .code(genreCode)
                            .score(0f)
                            .build();
                        categoryRepository.save(newCategory);
                        log.info("Created new category: [{} - {}]", genreCode, genreName);
                        return newCategory;
                    });
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }



    @Override
    public List<CategoryModel> findTopScoreCategory(int top) {
        Pageable pageable = PageRequest.of(0, top);
        return this.categoryRepository.findAllByOrderByScoreDesc(pageable);
    }
}
