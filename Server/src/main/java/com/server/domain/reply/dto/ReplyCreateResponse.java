package com.server.domain.reply.dto;

import com.server.domain.member.entity.Member;
import com.server.domain.reply.entity.Reply;
import com.server.domain.video.entity.Video;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ReplyCreateResponse {
    private String content;
    private Integer star;
    private Member member;
    private Video video;

    public Reply toEntity() {
        return Reply.builder()
                .content(this.content)
                .star(this.star)
                .member(this.member)
                .video(this.video)
                .build();
    }

}

