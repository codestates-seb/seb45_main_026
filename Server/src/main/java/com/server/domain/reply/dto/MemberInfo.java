package com.server.domain.reply.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberInfo {
    private Long memberId;
    private String nickname;
    private String imageUrl;
}