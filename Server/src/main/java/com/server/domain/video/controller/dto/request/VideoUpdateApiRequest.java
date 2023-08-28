package com.server.domain.video.controller.dto.request;

import com.server.domain.video.service.dto.request.VideoCreateServiceRequest;
import com.server.domain.video.service.dto.request.VideoUpdateServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class VideoUpdateApiRequest {

    private String videoName;
    private Integer price;
    private String description;
    private List<String> categories;

    public VideoUpdateServiceRequest toServiceRequest(Long videoId) {
        return VideoUpdateServiceRequest.builder()
                .videoId(videoId)
                .videoName(videoName)
                .price(price)
                .description(description)
                .categories(categories)
                .build();
    }

}
