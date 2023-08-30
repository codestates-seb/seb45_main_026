package com.server.domain.reply.dto;

import com.server.domain.member.entity.Member;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReplyDto {

    private Long replyId;
    private Long memberId;
    private String content;
    private int star;
    private Member member;
    private LocalDateTime createdAt;

}

