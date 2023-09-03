package com.server.domain.reply.dto;

import com.server.domain.reply.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
public class ReplyRequestApi {
    private Long replyId;
    private String content;
    private Integer star;
    private Long memberId;
    private String nickname;
    private String imageUrl;
    private LocalDateTime createdDate;



    public static ReplyRequestApi of(Reply reply) {
        return ReplyRequestApi.builder()
                .replyId(reply.getReplyId())
                .content(reply.getContent())
                .star(reply.getStar())
                .memberId(reply.getMember().getMemberId())
                .nickname(reply.getMember().getNickname())
                .imageUrl(reply.getMember().getImageFile())
                .createdDate(reply.getCreatedDate())
                .build();
    }
}