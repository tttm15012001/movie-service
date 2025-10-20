package com.movieservice.service;

import com.movieservice.dto.request.CategoryRequest;
import com.movieservice.dto.response.CategoryResponseDto;

import java.util.List;

public interface CategoryService {

    CategoryResponseDto getCategoryById(Long categoryId);

    List<CategoryResponseDto> findTopScoreCategory(int top);

    CategoryResponseDto createCategory(CategoryRequest categoryRequest);

    List<CategoryResponseDto> createCategories(List<CategoryRequest> categoryRequests);
}
