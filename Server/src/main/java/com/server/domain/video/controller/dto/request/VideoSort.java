package com.server.domain.video.controller.dto.request;

import com.server.global.entity.BaseEnum;
import lombok.AllArgsConstructor;
import org.springframework.beans.TypeMismatchException;

@AllArgsConstructor
public enum VideoSort implements BaseEnum {
    CREATED_DATE("최신순", "created-date", "createdDate"),
    VIEWS("조회수 순", "view", "view"),
    STAR("별점 순", "star", "star"),

    ;

    private final String description;
    private final String url;
    private final String sort;

    @Override
    public String getName() {
        return this.url;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public String getSort() {
        return this.sort;
    }

    public static VideoSort fromUrl(String url) {
        for (VideoSort videoSort : values()) {
            if (videoSort.url.equals(url)) {
                return videoSort;
            }
        }
        throw new TypeMismatchException(url, VideoSort.class);
    }
}
