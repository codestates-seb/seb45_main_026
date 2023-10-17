package com.server.domain.video.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class VideoCreateServiceRequest {

    private String videoName;
    private Integer price;
    private String description;
    private boolean hasPreview;
    private List<String> categories;
}
