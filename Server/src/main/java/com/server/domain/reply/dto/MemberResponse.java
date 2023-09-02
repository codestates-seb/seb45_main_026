package com.server.domain.reply.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class MemberResponse {
    private Long memberId;
    private String nickname;
    private String profileImageUrl;

    public static MemberResponse of(Long memberId, String nickname, String profileImageUrl) {
        return MemberResponse.builder()
                .memberId(memberId)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .build();
    }



}
