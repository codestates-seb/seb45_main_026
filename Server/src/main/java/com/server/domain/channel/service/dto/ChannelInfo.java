package com.server.domain.channel.service.dto;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Grade;
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
    private Boolean isSubscribed;
    private String description;
    private String imageUrl;
    private Grade grade;
    private LocalDateTime createdDate;
    public static ChannelInfo of(Channel channel, Boolean isSubscribed, String imageUrl) {

                 return ChannelInfo.builder()
                            .memberId(channel.getMember().getMemberId())
                            .channelName(channel.getChannelName())
                            .subscribers(channel.getSubscribers())
                            .isSubscribed(isSubscribed)
                            .description(channel.getDescription())
                            .imageUrl(imageUrl)
                            .grade(channel.getMember().getGrade())
                            .createdDate(channel.getCreatedDate())
                            .build();
        }
    }
