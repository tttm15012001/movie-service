package com.movieservice.service;

import com.movieservice.dto.response.CategoryResponseDto;
import com.movieservice.model.entity.CategoryModel;

import java.util.List;

public interface CategoryService {

    CategoryResponseDto getCategoryById(Long categoryId);

    List<CategoryModel> handleCategory(String categories);

    List<CategoryModel> findTopScoreCategory(int top);
}
