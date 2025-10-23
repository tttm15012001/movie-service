package com.movieservice.controller;

import com.movieservice.api.CategoryApi;
import com.movieservice.dto.request.CategoryRequest;
import com.movieservice.dto.response.CategoryResponseDto;
import com.movieservice.model.entity.CategoryModel;
import com.movieservice.service.CategoryService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController implements CategoryApi {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public CategoryResponseDto getCategory(@PathVariable("category-id") Long categoryId) {
        return this.categoryService.getCategoryById(categoryId);
    }

    @Override
    public List<CategoryResponseDto> getTopScoreCategory(int top) {
        return this.categoryService.findTopScoreCategory(top)
                .stream()
                .map(CategoryModel::toCategoryResponseDto)
                .toList();
    }

    @Override
    public CategoryResponseDto createCategory(CategoryRequest categoryRequest) {
        return this.categoryService.createCategory(categoryRequest);
    }

    @Override
    public ResponseEntity<List<CategoryResponseDto>> createCategories(@RequestBody List<CategoryRequest> categoryRequests) {
        List<CategoryResponseDto> response = categoryService.createCategories(categoryRequests);
        return ResponseEntity.ok(response);
    }
}
