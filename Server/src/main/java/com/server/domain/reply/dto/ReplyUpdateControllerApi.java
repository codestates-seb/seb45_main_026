package com.server.domain.reply.dto;

import com.server.domain.reply.entity.Reply;
import com.server.global.validation.OnlyNotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Builder
@Getter
@AllArgsConstructor
public class ReplyUpdateControllerApi {
    @OnlyNotBlank(message = "{validation.reply.content}")
    @Size(min = 1, max = 100, message = "{validation.size}")
    private String content;
    @Positive(message = "{validation.positive}")
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
