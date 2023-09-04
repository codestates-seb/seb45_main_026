package com.server.domain.category.entity;


import com.server.domain.videoCategory.entity.VideoCategory;
import com.server.global.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Category extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long categoryId;

    @Column(nullable = false)
    private String categoryName;

    @OneToMany(mappedBy = "category")
    private List<VideoCategory> videoCategories = new ArrayList<>();
}
