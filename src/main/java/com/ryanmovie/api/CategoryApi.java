package com.ryanmovie.api;

import static com.ryanmovie.common.constant.ApiConstant.CATEGORY_API_URL;

import com.ryanmovie.dto.request.CategoryRequest;
import com.ryanmovie.dto.response.CategoryResponseDto;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RequestMapping(CATEGORY_API_URL)
public interface CategoryApi {
    @GetMapping(value = "/{category-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    CategoryResponseDto getCategory(@PathVariable("category-id") Long categoryId);

    @GetMapping(value = "/top/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    List<CategoryResponseDto> getTopScoreCategory(int top);

    @PostMapping(value = "/new", produces = MediaType.APPLICATION_JSON_VALUE)
    CategoryResponseDto createCategory(@RequestBody CategoryRequest categoryRequest);
}
