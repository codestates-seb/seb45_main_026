package com.server.domain.channel.service.dto;

import com.server.domain.channel.entity.Channel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ChannelInfo {
    private Long memberId;
    private String channelName;
    private int subscribers;
    private boolean isSubscribed;
    private String description;
    private String imageUrl;
    private LocalDateTime createdDate;


        public static ChannelInfo of(Channel channel, boolean isSubscribed, String imageUrl) {

            return ChannelInfo.builder()
                    .isSubscribed(isSubscribed)
                    .imageUrl(imageUrl)
                    .build();
        }
    }
