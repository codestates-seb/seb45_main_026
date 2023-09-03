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
    private Long memberId;
    private String nickname;
    private String imageUrl;
    private LocalDateTime createdDate;

    public static ReplyInfo of(Reply reply, AwsService awsService, Long memberId) {
        Member member = reply.getMember();
        String imageFile = awsService.getFileUrl(memberId, member.getImageFile(), FileType.PROFILE_IMAGE);

        return ReplyInfo.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .imageUrl(imageFile)
                .replyId(reply.getReplyId())
                .content(reply.getContent())
                .star(reply.getStar())
                .createdDate(reply.getCreatedDate())
                .build();
    }
//    public static ReplyInfo of(Reply reply) {
//        Member member = reply.getMember();
//
//        return ReplyInfo.builder()
//                .replyId(reply.getReplyId())
//                .content(reply.getContent())
//                .star(reply.getStar())
//                .memberId(member.getMemberId())
//                .nickname(member.getNickname())
//                .imageUrl(member.getImageFile())
//                .createdDate(reply.getCreatedDate())
//                .build();
//
//    }


    public static Page<ReplyInfo> of(Page<Reply> replies, AwsService awsService, Long memberId) {
        return replies.map(reply -> ReplyInfo.of(reply, awsService, memberId));
    }
}
