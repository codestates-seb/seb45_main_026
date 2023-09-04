package com.server.domain.reply.dto;

import com.server.domain.reply.entity.Reply;
import com.server.global.validation.OnlyNotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ReplyUpdateControllerApi {
    @OnlyNotBlank(message = "{validation.reply.content}")
    private String content;
    @OnlyNotBlank(message = "{validation.reply.star}")
    private Integer star;

    public static ReplyUpdateServiceApi of(Reply reply) {
        return ReplyUpdateServiceApi.builder()
                .content(reply.getContent())
                .star(reply.getStar())
                .build();
    }

    public ReplyUpdateServiceApi toService() {
        return ReplyUpdateServiceApi.builder()
                .content(content)
                .star(star)
                .build();
    }
}
