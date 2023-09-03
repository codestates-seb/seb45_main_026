package com.server.domain.reply.service;

import com.server.domain.member.repository.MemberRepository;
import com.server.domain.reply.dto.ReplyInfo;
import com.server.domain.reply.dto.ReplyRequest;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.repository.ReplyRepository;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.replyException.ReplyNotFoundException;
import com.server.module.s3.service.AwsService;
import org.springframework.stereotype.Service;

@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;
    private final AwsService awsService;




    public ReplyService(ReplyRepository replyRepository, MemberRepository memberRepository, AwsService awsService) {
        this.replyRepository = replyRepository;
        this.memberRepository = memberRepository;
        this.awsService = awsService;
    }


    public void updateReply(Long loginMemberId, Long replyId, ReplyRequest response) {

        memberRepository.findById(loginMemberId).orElseThrow(() -> new MemberAccessDeniedException());

       existReply(replyId);

       ReplyRequest.builder()
               .content(response.getContent())
               .star(response.getStar())
               .build();
    }

    public ReplyInfo getReply(Long replyId, Long loginMemberId) {

            memberRepository.findById(loginMemberId).orElseThrow(() -> new MemberNotFoundException());

            Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new ReplyNotFoundException());

            return ReplyInfo.of(reply, awsService, reply.getMember().getMemberId());
    }

    public void deleteReply(Long replyId, Long loginMemberId) {

        memberRepository.findById(loginMemberId).orElseThrow(() -> new MemberNotFoundException());

        replyRepository.deleteById(replyId);
    }


    public void existReply(Long replyId) {

        Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new ReplyNotFoundException());

        ReplyInfo.of(reply, awsService, reply.getMember().getMemberId());

    }
}
