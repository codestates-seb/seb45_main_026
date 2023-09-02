package com.server.domain.channel.service.dto;

import com.server.domain.channel.entity.Channel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChannelUpdate {
    @Size(min = 1, max = 20, message = "{validation.size}")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣 ]*$", message = "{validation.channel.channelName}")
    private String channelName;
    private String description;

    public static ChannelUpdate of(String channelName, String description) {
        return ChannelUpdate.builder()
                .channelName(channelName)
                .description(description)
                .build();
    }

}