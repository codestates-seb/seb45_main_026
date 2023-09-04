package com.server.domain.reply.dto;

import com.server.domain.reply.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ReplyCreateServiceApi {
    private String content;
    private Integer star;

    public static ReplyUpdateServiceApi of(Reply reply) {
        return ReplyUpdateServiceApi.builder()
                .content(reply.getContent())
                .star(reply.getStar())
                .build();
    }
}
