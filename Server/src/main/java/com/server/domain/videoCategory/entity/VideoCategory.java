package com.server.domain.videoCategory.entity;


import com.server.domain.category.entity.Category;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.global.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
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


    public static VideoCategory createVideoCategory(Video video, Category category) {
        return VideoCategory.builder()
                .video(video)
                .category(category)
                .build();

    }
}
