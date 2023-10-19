package com.server.domain.video.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class PreviewUrlResponse {

    private String previewUrl;

    public static PreviewUrlResponse of(String url) {
        return PreviewUrlResponse.builder()
                .previewUrl(url)
                .build();
    }
}
