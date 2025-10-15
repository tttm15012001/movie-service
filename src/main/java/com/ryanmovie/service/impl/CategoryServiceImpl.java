package com.ryanmovie.service.impl;

import static com.ryanmovie.common.constant.DatabaseConstants.TABLE_CATEGORY;

import com.ryanmovie.dto.request.CategoryRequest;
import com.ryanmovie.dto.response.CategoryResponseDto;
import com.ryanmovie.model.entity.CategoryModel;
import com.ryanmovie.repository.CategoryRepository;
import com.ryanmovie.service.CategoryService;
import com.ryanmovie.validation.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<CategoryResponseDto> findTopScoreCategory(int top) {
        Pageable pageable = PageRequest.of(0, top);
        return this.categoryRepository.findAllByOrderByScoreDesc(pageable)
                .stream()
                .map(CategoryModel::toCategoryResponseDto)
                .toList();
    }

    @Override
    public CategoryResponseDto createCategory(CategoryRequest categoryRequest) {
        CategoryModel categoryModel = CategoryModel.builder()
                .name(categoryRequest.getName())
                .score(categoryRequest.getScore())
                .build();

        return this.categoryRepository.save(categoryModel).toCategoryResponseDto();
    }
}
