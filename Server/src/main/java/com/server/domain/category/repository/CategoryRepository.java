package com.server.domain.category.repository;

import com.server.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByCategoryNameIn(List<String> categoryNames);
}