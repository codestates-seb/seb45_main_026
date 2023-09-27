package com.server.domain.report.service.dto.response;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.member.entity.MemberStatus;
import com.server.domain.video.entity.Video;
import com.server.domain.video.entity.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Getter
@Builder
public class AdminVideoResponse {

    private Long videoId;
    private String videoName;
    private VideoStatus videoStatus;
    private Long memberId;
    private String email;
    private String channelName;
    private LocalDateTime createdDate;

    public static AdminVideoResponse of(Video video) {

        Channel channel = video.getChannel();

        if(channel == null) {
            return AdminVideoResponse.builder()
                    .videoId(video.getVideoId())
                    .videoName(video.getVideoName())
                    .videoStatus(video.getVideoStatus())
                    .memberId(null)
                    .email("탈퇴한 회원의 비디오입니다.")
                    .channelName("탈퇴한 회원의 비디오입니다.")
                    .createdDate(video.getCreatedDate())
                    .build();

        }else {
            Member member = channel.getMember();

            return AdminVideoResponse.builder()
                    .videoId(video.getVideoId())
                    .videoName(video.getVideoName())
                    .videoStatus(video.getVideoStatus())
                    .memberId(member.getMemberId())
                    .email(member.getEmail())
                    .channelName(video.getChannel().getChannelName())
                    .createdDate(video.getCreatedDate())
                    .build();
        }


    }
}
