package com.server.domain.category.service;

import com.server.domain.category.service.dto.response.CategoryResponse;
import com.server.global.testhelper.ServiceTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CategoryServiceTest extends ServiceTest {

    @Autowired CategoryService categoryService;

    @Test
    @DisplayName("db 에 저장된 카테고리를 모두 가져온다.")
    void getCategories() {
        //given
        createAndSaveCategory("category1");
        createAndSaveCategory("category2");

        //when
        List<CategoryResponse> categories = categoryService.getCategories();

        //then
        assertThat(categories).hasSize(2)
                .extracting("categoryName")
                .containsExactlyInAnyOrder("category1", "category2");
    }
}