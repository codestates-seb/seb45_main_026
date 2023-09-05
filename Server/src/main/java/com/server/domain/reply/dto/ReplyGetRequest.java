package com.server.domain.reply.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class ReplyGetRequest {

    private Long replyId;
    private int page;
    private int size;
    private String content;
    private Integer star;
    private MemberInfo member;
    private LocalDateTime createdDate;
    private String sort;

    public static ReplyGetRequest of(Long replyId, int page, int size, String content, Integer star, MemberInfo member, LocalDateTime createdDate, String sort) {
        return ReplyGetRequest.builder()
                .replyId(replyId)
                .page(page)
                .size(size)
                .content(content)
                .star(star)
                .member(member)
                .createdDate(createdDate)
                .sort(sort)
                .build();
    }
}
