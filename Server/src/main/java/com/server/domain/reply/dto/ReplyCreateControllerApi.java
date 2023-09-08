package com.server.domain.reply.dto;

import com.server.domain.reply.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Builder
@Getter
@AllArgsConstructor
public class ReplyCreateControllerApi {
    @NotBlank(message = "{validation.reply.content}")
    @Size(min = 1, max = 100, message = "{validation.size}")
    private String content;
    @NotNull(message = "{validation.reply.star}")
    @Positive(message = "{validation.positive}")
    private Integer star;

    public static ReplyUpdateServiceApi of(Reply reply) {
        return ReplyUpdateServiceApi.builder()
                .content(reply.getContent())
                .star(reply.getStar())
                .build();
    }


    public CreateReply toService() {
        return CreateReply.builder()
                .content(content)
                .star(star)
                .build();
    }
}

