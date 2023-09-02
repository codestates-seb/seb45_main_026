package com.server.domain.video.controller.dto.request;

import com.server.domain.video.service.dto.request.VideoCreateServiceRequest;
import com.server.domain.video.service.dto.request.VideoUpdateServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class VideoUpdateApiRequest {

    private String description;

    public VideoUpdateServiceRequest toServiceRequest(Long videoId) {
        return VideoUpdateServiceRequest.builder()
                .videoId(videoId)
                .description(description)
                .build();
    }

}
