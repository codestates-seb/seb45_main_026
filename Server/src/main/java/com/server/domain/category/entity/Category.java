package com.server.domain.category.entity;



import com.server.domain.video.entity.Video;
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

    @ManyToMany
    @JoinColumn(name = "videoCategory")
    private List<Video> videoCategories = new ArrayList<>();

    public String getCategoryName(){
        return categoryName;
    }

}
