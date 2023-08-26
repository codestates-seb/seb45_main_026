package com.server.domain.videoCategory.entity;


import com.server.domain.category.entity.Category;
import com.server.domain.video.entity.Video;
import com.server.global.entity.BaseEntity;
import lombok.Getter;

import javax.persistence.*;
@Getter
@Entity
public class VideoCategory extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long videoCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;


}
