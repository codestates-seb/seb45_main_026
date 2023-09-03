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

@Builder
@Getter
@AllArgsConstructor
public class ReplyInfo {
    private Long replyId;
    private String content;
    private Integer star;
    private MemberInfo member;
    private String nickname;
    private String imageUrl;
    private LocalDateTime createdDate;



    public static ReplyInfo of(Reply reply, AwsService awsService, Long memberId) {
        Member member2 = reply.getMember();
        String imageUrl = awsService.getFileUrl(memberId, member2.getImageFile(), FileType.PROFILE_IMAGE);


        MemberInfo member = MemberInfo.builder()
                .memberId(member2.getMemberId())
                .nickname(member2.getNickname())
                .imageUrl(imageUrl)
                .build();


        return ReplyInfo.builder()
                .replyId(reply.getReplyId())
                .content(reply.getContent())
                .star(reply.getStar())
                .nickname(member.getNickname())
                .imageUrl(imageUrl)
                .createdDate(reply.getCreatedDate())
                .build();
    }

    public static Page<ReplyInfo> of(Page<Reply> replies, AwsService awsService, Long memberId) {
        return replies.map(reply -> ReplyInfo.of(reply, awsService, memberId));
    }
}
