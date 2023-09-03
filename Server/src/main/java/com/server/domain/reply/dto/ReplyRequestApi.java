//package com.server.domain.reply.dto;
//
//import com.server.domain.member.entity.Member;
//import com.server.domain.reply.entity.Reply;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//
//import java.time.LocalDateTime;
//
//@Builder
//@Getter
//@AllArgsConstructor
//public class ReplyRequestApi {
//    private Long replyId;
//    private String content;
//    private Integer star;
//    private Long memberId;
//    private String nickname;
//    private String imageUrl;
//    private LocalDateTime createdDate;
//
//
//
//    public static ReplyRequestApi of(Reply reply) {
//        Member member = reply.getMember();
//        return ReplyRequestApi.builder()
//                .replyId(reply.getReplyId())
//                .content(reply.getContent())
//                .star(reply.getStar())
//                .memberId(member.getMemberId())
//                .nickname(member.getNickname())
//                .imageUrl(member.getImageFile())
//                .createdDate(reply.getCreatedDate())
//                .build();
//    }
//}