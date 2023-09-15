package com.server.domain.video.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class VideoUrlResponse {

    private String videoUrl;

    public static VideoUrlResponse of(String url) {
        return VideoUrlResponse.builder()
                .videoUrl(url)
                .build();
    }
}
