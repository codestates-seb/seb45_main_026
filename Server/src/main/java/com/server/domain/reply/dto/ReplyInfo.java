package com.server.domain.reply.dto;

import com.server.domain.member.entity.Member;
import com.server.domain.reply.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
public class ReplyInfo {
        private Long replyId;
        private Long memberId;
        private Long videoId;
        private String content;
        private int star;
        private Member member;
        private LocalDateTime createdDate;

        public static ReplyInfo of(Reply reply) {
            return ReplyInfo.builder()
                    .replyId(reply.getReplyId())
                    .memberId(reply.getMember().getMemberId())
                    .videoId(reply.getVideo().getVideoId())
                    .content(reply.getContent())
                    .star(reply.getStar())
                    .member(reply.getMember())
                    .createdDate(reply.getCreatedDate())
                    .build();
        }

    public static Page<ReplyInfo> of(Page<Reply> replies) { //reply -> replyResponse Page
        return replies.map(reply -> of(reply));
    }
}
