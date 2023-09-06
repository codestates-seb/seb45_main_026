package com.server.domain.channel.service.dto;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.module.s3.service.AwsService;
import com.server.module.s3.service.dto.FileType;
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
    private LocalDateTime createdDate;

    public static ChannelInfo of(Channel channel, Boolean isSubscribed, AwsService awsService, Member member) {

            String imageUrl = awsService.getFileUrl(channel.getMember().getMemberId(), member.getImageFile(), FileType.PROFILE_IMAGE);

                 return ChannelInfo.builder()
                            .memberId(channel.getMember().getMemberId())
                            .channelName(channel.getChannelName())
                            .subscribers(channel.getSubscribers())
                            .isSubscribed(isSubscribed)
                            .description(channel.getDescription())
                            .imageUrl(imageUrl)
                            .createdDate(channel.getCreatedDate())
                            .build();



        }
    }
