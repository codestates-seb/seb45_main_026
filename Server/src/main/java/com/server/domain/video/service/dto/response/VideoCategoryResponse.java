package com.server.domain.video.service.dto.response;

import com.server.domain.category.entity.Category;
import com.server.domain.videoCategory.entity.VideoCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Builder
public class VideoCategoryResponse {

    private Long categoryId;
    private String categoryName;

    public static List<VideoCategoryResponse> of(List<VideoCategory> videoCategories) {

        if(videoCategories == null) return new ArrayList<>();

        return videoCategories.stream()
                .map(VideoCategoryResponse::of)
                .collect(Collectors.toList());
    }

    private static VideoCategoryResponse of(VideoCategory videoCategories) {
        Category category = videoCategories.getCategory();
        return VideoCategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .build();
    }
}
