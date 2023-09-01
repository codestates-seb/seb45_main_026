package com.server.domain.channel.service.dto;

import com.server.domain.channel.entity.Channel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ChannelUpdate {
    private String channelName;
    private String description;

    public static ChannelUpdate of(String channelName, String description) {
        return ChannelUpdate.builder()
                .channelName(channelName)
                .description(description)
                .build();
    }
}