package com.server.domain.report.service.dto.response;

import com.server.domain.member.entity.Member;
import com.server.domain.member.entity.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class AdminMemberResponse {

    private Long memberId;
    private String email;
    private String nickname;
    private MemberStatus memberStatus;
    private String channelName;
    private String blockReason;
    private LocalDateTime blockEndDate;
    private LocalDateTime createdDate;

    public static AdminMemberResponse of(Member member, String blockReason, LocalDateTime blockEndDate) {

        MemberStatus memberStatus = blockReason == null ? MemberStatus.ACTIVE : MemberStatus.BLOCKED;
        String blockReasonResponse = blockReason == null ? "없음" : blockReason;

        return AdminMemberResponse.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .memberStatus(memberStatus)
                .channelName(member.getChannel().getChannelName())
                .blockReason(blockReason)
                .blockEndDate(blockEndDate)
                .createdDate(member.getCreatedDate())
                .build();
    }
}
