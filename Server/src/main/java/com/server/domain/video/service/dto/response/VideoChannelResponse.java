package com.server.domain.video.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class VideoChannelResponse {

    private Long memberId;
    private String channelName;
    private Integer subscribes;
    private Boolean isSubscribed;
    private String imageUrl;
}
