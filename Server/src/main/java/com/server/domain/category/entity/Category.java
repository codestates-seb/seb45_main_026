package com.server.domain.category.entity;


import com.server.domain.videoCategory.entity.VideoCategory;
import com.server.global.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "videoCategory")
    private Category videoCategories;

}
