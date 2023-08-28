package com.server.domain.video.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class VideoDetailResponse {

    private Long videoId;
    private String videoName;
    private String description;
    private String thumbnailUrl;
    private String videoUrl;
    private Integer views;
    private Float star;
    private Integer price;
    private Integer reward;
    private Boolean isReplied;
    private List<VideoCategoryResponse> categories;
    private VideoChannelResponse channel;
    private LocalDateTime createdDate;
}
