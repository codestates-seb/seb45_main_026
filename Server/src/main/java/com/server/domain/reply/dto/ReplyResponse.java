package com.server.domain.reply.dto;


import com.server.domain.reply.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
public class ReplyResponse {
    private Long replyId;
    private String content;
    private int star;
    private MemberResponse memberResponse;
    private LocalDateTime createdDate;


    public static ReplyResponse of(Long replyId, String content, int star, MemberResponse memberResponse, LocalDateTime createdDate) {
        return ReplyResponse.builder()
                .replyId(replyId)
                .content(content)
                .star(star)
                .memberResponse(memberResponse)
                .createdDate(createdDate)
                .build();
    }

    public static Page<ReplyResponse> of(Page<Reply> replies) { //reply -> replyResponse Page
        return replies.map(reply -> (ReplyResponse) of((Page<Reply>) reply));
    }



}
