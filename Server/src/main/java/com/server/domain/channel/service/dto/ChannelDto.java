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
        private boolean isSubscribed; //구독여부 값 채워넣기
        private String description;
        private String imageUrl;
        private LocalDateTime createdDate;

        public static ChannelInfo of(Channel channel,
                                     String channelName,
                                     int subscribers,
                                     boolean isSubscribed,
                                     String description,
                                     String imageUrl,
                                     LocalDateTime createdDate) {

            return ChannelInfo.builder()
                    .channelName(channel.getChannelName())
                    .subscribers(subscribers)
                    .isSubscribed(isSubscribed)
                    .description(channel.getDescription())
                    .createdDate(channel.getCreatedDate())
                    .build();
        }
    }


    @Builder
    @Getter
    public static class UpdateInfo {
        private String channelName;
        private String description;
    }
}

