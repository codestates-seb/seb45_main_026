package com.server.domain.reply.dto;

import com.server.domain.member.entity.Member;
import com.server.domain.reply.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public class ReplyDto {

    @Builder
    @Getter
    @AllArgsConstructor
    public static class ReplyResponse {
        private Long replyId;
        private Long memberId;
        private String content;
        private int star;
        private Member member;
        private LocalDateTime createdAt;

        public static ReplyResponse of(Reply reply) {
            return ReplyResponse.builder()
                    .replyId(reply.getReplyId())
                    .memberId(reply.getMember().getMemberId())
                    .content(reply.getContent())
                    .star(reply.getStar())
                    .member(reply.getMember())
                    .createdAt(reply.getCreatedAt())
                    .build();
        }
    }

    public static List<ReplyResponse> ReplyListOf(List<Reply> replies) {
        List<ReplyResponse> collect = replies.stream()
                .map((Reply reply) -> ReplyResponse.of(reply))
                .collect(Collectors.toList());
        return collect;
    }
}
