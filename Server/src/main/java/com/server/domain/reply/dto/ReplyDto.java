package com.server.domain.reply.dto;

import com.server.domain.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReplyDto {

    private Long replyId;
    private Long memberId;
    private String content;
    private int star;
    private Member member;
    private LocalDateTime createdDate;

    public ReplyDto(Long replyId, Long memberId, String content, int star, Member member, LocalDateTime createdDate) {
        this.replyId = replyId;
        this.memberId = memberId;
        this.content = content;
        this.star = star;
        this.member = member;
        this.createdDate = createdDate;
    }

}

