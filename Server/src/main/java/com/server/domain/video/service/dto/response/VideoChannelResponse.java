package com.server.domain.video.service.dto.response;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
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

    public static VideoChannelResponse of(Channel channel, boolean isSubscribed, String imageUrl) {

        if(channel == null)
            return VideoChannelResponse.builder()
                    .memberId(null)
                    .channelName("삭제된 채널")
                    .subscribes(0)
                    .isSubscribed(null)
                    .imageUrl(null)
                    .build();

        return VideoChannelResponse.builder()
                .memberId(channel.getMember().getMemberId())
                .channelName(channel.getChannelName())
                .subscribes(channel.getSubscribers())
                .isSubscribed(isSubscribed)
                .imageUrl(imageUrl)
                .build();
    }
}
