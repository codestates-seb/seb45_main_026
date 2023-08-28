package com.server.domain.video.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.aspectj.lang.annotation.Aspect;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class VideoPageResponse {

    private Long videoId;
    private String videoName;
    private String thumbnailUrl;
    private Integer views;
    private Integer price;
    private Float star;
    private List<VideoCategoryResponse> categories;
    private VideoChannelResponse channel;
    private LocalDateTime createdDate;
}
