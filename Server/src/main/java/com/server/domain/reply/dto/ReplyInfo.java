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
    private String content;
    private Integer star;
    private Long memberId;
    private String nickname;
    private String imageUrl;
    private LocalDateTime createdDate;

    // 생성자, Getter 메서드 업데이트

    public static ReplyInfo of(Reply reply) {
        return ReplyInfo.builder()
                .replyId(reply.getReplyId())
                .content(reply.getContent())
                .star(reply.getStar())
                .memberId(reply.getMember().getMemberId())
                .nickname(reply.getMember().getNickname())
                .imageUrl(reply.getMember().getImageFile())
                .createdDate(reply.getCreatedDate())
                .build();
    }

    public static Page<ReplyInfo> of(Page<Reply> replies) {
        return replies.map(ReplyInfo::of);
    }
}
