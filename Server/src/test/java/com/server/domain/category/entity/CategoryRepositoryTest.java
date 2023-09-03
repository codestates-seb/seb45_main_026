package com.server.domain.category.entity;

import com.server.domain.category.repository.CategoryRepository;
import com.server.global.testhelper.RepositoryTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

class CategoryRepositoryTest extends RepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리 이름으로 카테고리를 조회한다.")
    void findByCategoryNameIn() {
        //given
        Category category = createAndSaveCategory("categoryName1");
        Category category2 = createAndSaveCategory("categoryName2");
        Category category3 = createAndSaveCategory("categoryName3");

        List<String> categoryNames = List.of(category.getCategoryName(), category2.getCategoryName()); //1, 2 를 찾음

        em.flush();
        em.clear();

        //when
        List<Category> categories = categoryRepository.findByCategoryNameIn(categoryNames);

        //then
        Assertions.assertThat(categories).hasSize(2)
                .extracting("categoryName")
                .containsExactlyInAnyOrder(category.getCategoryName(), category2.getCategoryName());


    }
}