package com.server.domain.category.controller;

import com.server.domain.category.service.CategoryService;
import com.server.domain.category.service.dto.response.CategoryResponse;
import com.server.global.reponse.ApiSingleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiSingleResponse<List<CategoryResponse>>> getCategories() {

        List<CategoryResponse> categories = categoryService.getCategories();

        return ResponseEntity.ok(ApiSingleResponse.ok(categories, "카테고리 목록 조회 성공"));
    }
}
