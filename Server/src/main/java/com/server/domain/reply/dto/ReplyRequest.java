package com.server.domain.reply.dto;

import com.server.domain.reply.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Builder
@Getter
@AllArgsConstructor
public class ReplyRequest {
    @NotNull(message = "{validation.reply.content}")
    private String content;
    @NotNull(message = "{validation.reply.star}")
    private Integer star;

    public static ReplyRequest of(Reply reply) {
        return ReplyRequest.builder()
                .content(reply.getContent())
                .star(reply.getStar())
                .build();
    }
}