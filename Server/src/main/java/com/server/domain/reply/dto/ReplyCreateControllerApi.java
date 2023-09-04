package com.server.domain.reply.dto;

import com.server.domain.reply.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
@AllArgsConstructor
public class ReplyCreateControllerApi {
    @NotBlank(message = "{validation.reply.content}")
    private String content;
    @NotBlank(message = "{validation.reply.star}")
    private Integer star;

    public static ReplyUpdateServiceApi of(Reply reply) {
        return ReplyUpdateServiceApi.builder()
                .content(reply.getContent())
                .star(reply.getStar())
                .build();
    }


    public ReplyCreateServiceApi toService() {
        return ReplyCreateServiceApi.builder()
                .content(content)
                .star(star)
                .build();
    }
}

