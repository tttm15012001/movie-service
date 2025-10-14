package com.ryanmovie.service;

import com.ryanmovie.dto.request.CategoryRequest;
import com.ryanmovie.dto.response.CategoryResponseDto;
import com.ryanmovie.model.entity.CategoryModel;

import java.util.List;

public interface CategoryService {

    List<CategoryModel> findTopScoreCategory(int top);

    CategoryResponseDto createCategory(CategoryRequest movieRequest);
}
