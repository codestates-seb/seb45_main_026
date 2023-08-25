package com.server.domain.category.entity;



import com.server.domain.entity.BaseEntity;
import lombok.Getter;

import javax.persistence.*;
@Getter
@Entity
public class Category extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long categoryId;

    @Column(nullable = false)
    private String categoryName;



}
