package com.server.domain.video.controller.dto.request;

import com.server.domain.video.service.dto.request.VideoCreateServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class VideoCreateApiRequest {

    private String videoName;
    private Integer price;
    private String description;
    private List<String> categories;

    public VideoCreateServiceRequest toServiceRequest() {
        return VideoCreateServiceRequest.builder()
                .videoName(videoName)
                .price(price)
                .description(description)
                .categories(categories)
                .build();
    }

}
