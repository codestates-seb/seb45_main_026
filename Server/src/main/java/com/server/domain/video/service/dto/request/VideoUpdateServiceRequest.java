package com.server.domain.video.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class VideoUpdateServiceRequest {

    private Long videoId;
    private String videoName;
    private Integer price;
    private String description;
    private List<String> categories;
}
