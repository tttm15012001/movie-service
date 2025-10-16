package com.ryanmovie.service;

import com.ryanmovie.dto.request.CategoryRequest;
import com.ryanmovie.dto.response.CategoryResponseDto;

import java.util.List;

public interface CategoryService {

    CategoryResponseDto getCategoryById(Long categoryId);

    List<CategoryResponseDto> findTopScoreCategory(int top);

    CategoryResponseDto createCategory(CategoryRequest categoryRequest);
}
