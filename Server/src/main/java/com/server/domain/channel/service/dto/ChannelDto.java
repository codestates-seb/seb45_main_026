package com.server.domain.channel.service.dto;

import com.server.domain.channel.entity.Channel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
public class ChannelDto {
    @Builder
    @Getter
    public static class ChannelInfo {
        private Long memberId;
        private String channelName;
        private int subscribers;
        private boolean isSubscribed;
        private String description;
        private String imageUrl;
        private LocalDateTime createdDate;
    }

    @Builder
    @Getter
    public static class UpdateInfo {
        private String channelName;
        private String description;
    }
}

