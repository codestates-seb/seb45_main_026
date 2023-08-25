package com.server.domain.videoCategory.entity;


import com.server.domain.entity.BaseEntity;
import com.server.domain.category.entity.Category;
import com.server.domain.video.entity.Video;
import lombok.Getter;

import javax.persistence.*;
@Getter
@Entity
public class VideoCategory extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long videoCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_Id")
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;


}
