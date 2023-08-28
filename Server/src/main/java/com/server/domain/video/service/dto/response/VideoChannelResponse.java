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

    public static VideoChannelResponse of(Member member, boolean isSubscribed) {
        return VideoChannelResponse.builder()
                .memberId(member.getMemberId())
                .channelName(member.getChannel().getChannelName())
                .subscribes(member.getChannel().getSubscribers())
                .isSubscribed(isSubscribed)
                .imageUrl(member.getImageFile())
                .build();
    }
}
