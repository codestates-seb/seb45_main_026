package com.server.domain.category.service.dto.response;

import com.server.domain.category.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Getter
public class CategoryResponse {

    private Long categoryId;
    private String categoryName;

    public static List<CategoryResponse> of(List<Category> categories) {
        return categories.stream()
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
    }

    public static CategoryResponse of(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .build();
    }
}
