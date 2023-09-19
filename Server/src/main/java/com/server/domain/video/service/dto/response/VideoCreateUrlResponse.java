package com.server.domain.video.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class VideoCreateUrlResponse {

    private String thumbnailUrl;
    private String videoUrl;
}
