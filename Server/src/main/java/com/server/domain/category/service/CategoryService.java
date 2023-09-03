package com.server.domain.category.service;

import com.server.domain.category.repository.CategoryRepository;
import com.server.domain.category.service.dto.response.CategoryResponse;
import com.server.domain.video.service.dto.response.VideoCategoryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getCategories() {
        return CategoryResponse.of(categoryRepository.findAll());
    }
}
