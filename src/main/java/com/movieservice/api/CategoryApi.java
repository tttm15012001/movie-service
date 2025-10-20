package com.movieservice.api;

import static com.movieservice.common.constant.ApiConstant.CATEGORY_API_URL;

import com.movieservice.dto.request.CategoryRequest;
import com.movieservice.dto.response.CategoryResponseDto;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(CATEGORY_API_URL)
public interface CategoryApi {
    @GetMapping(value = "/{category-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    CategoryResponseDto getCategory(@PathVariable("category-id") Long categoryId);

    @GetMapping(value = "/top/{top}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<CategoryResponseDto> getTopScoreCategory(@PathVariable("top") int top);

    @PostMapping(value = "/new", produces = MediaType.APPLICATION_JSON_VALUE)
    CategoryResponseDto createCategory(@RequestBody CategoryRequest categoryRequest);

    @PostMapping(value = "/multiple/new", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<CategoryResponseDto>> createCategories(@RequestBody List<CategoryRequest> categoryRequest);
}
