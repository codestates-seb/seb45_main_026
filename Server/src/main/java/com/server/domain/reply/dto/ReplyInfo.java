package com.server.domain.reply.dto;

import com.server.domain.member.entity.Member;
import com.server.domain.reply.entity.Reply;
import com.server.module.s3.service.AwsService;
import com.server.module.s3.service.dto.FileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ReplyInfo {
    private Long replyId;
    private String content;
    private Integer star;
    private MemberInfo member;
    private LocalDateTime createdDate;
    public static ReplyInfo of(Reply reply, String imageUrl) {
        Member loginMember = reply.getMember();

        MemberInfo member = MemberInfo.builder()
                .memberId(loginMember.getMemberId())
                .nickname(loginMember.getNickname())
                .imageUrl(imageUrl)
                .build();

        return ReplyInfo.builder()
                .replyId(reply.getReplyId())
                .content(reply.getContent())
                .star(reply.getStar())
                .member(member)
                .createdDate(reply.getCreatedDate())
                .build();
    }
}
