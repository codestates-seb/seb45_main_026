package com.server.domain.category.entity;



import com.server.domain.video.entity.Video;
import com.server.global.entity.BaseEntity;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Category extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long categoryId;

    @Column(nullable = false)
    private String categoryName;

    @ManyToMany
    @JoinColumn(name = "videoCategory")
    private List<Video> videoCategories = new ArrayList<>();

    public String getCategoryName(){
        return categoryName;
    }

}
