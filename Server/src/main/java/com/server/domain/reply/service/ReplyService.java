package com.server.domain.reply.service;

import com.server.domain.member.repository.MemberRepository;
import com.server.domain.reply.dto.ReplyInfo;
import com.server.domain.reply.dto.ReplyRequest;
import com.server.domain.reply.dto.ReplyRequestApi;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.repository.ReplyRepository;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.replyException.ReplyNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;


    public ReplyService(ReplyRepository replyRepository, MemberRepository memberRepository) {
        this.replyRepository = replyRepository;
        this.memberRepository = memberRepository;
    }


    public void updateReply(Long loginMemberId, Long replyId, ReplyRequest response) {

        memberRepository.findById(loginMemberId).orElseThrow(() -> new MemberAccessDeniedException());

       existReply(replyId);

       ReplyRequest.builder()
               .content(response.getContent())
               .star(response.getStar())
               .build();
    }

    public ReplyRequestApi getReply(Long replyId, Long loginMemberId) {

            memberRepository.findById(loginMemberId).orElseThrow(() -> new MemberNotFoundException());

            Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new ReplyNotFoundException());

            return ReplyRequestApi.of(reply);
    }

    public void deleteReply(Long replyId, Long loginMemberId) {

        memberRepository.findById(loginMemberId).orElseThrow(() -> new MemberNotFoundException());

        replyRepository.deleteById(replyId);
    }


    public void existReply(Long replyId) {

        Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new ReplyNotFoundException());

        ReplyInfo.of(reply);

    }
}
