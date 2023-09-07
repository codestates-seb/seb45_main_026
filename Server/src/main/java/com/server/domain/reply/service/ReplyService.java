package com.server.domain.reply.service;

import com.server.domain.member.repository.MemberRepository;
import com.server.domain.reply.dto.ReplyInfo;
import com.server.domain.reply.dto.ReplyUpdateServiceApi;
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




    public ReplyService(ReplyRepository replyRepository) {
        this.replyRepository = replyRepository;
    }


    public void updateReply(Long loginMemberId, Long replyId, ReplyUpdateServiceApi request) {

        Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new ReplyNotFoundException());

            if (!reply.getMember().getMemberId().equals(loginMemberId)) {
                throw new MemberAccessDeniedException();
            }

        reply.updateReply(request.getContent(), request.getStar());
    }

    public ReplyInfo getReply(Long replyId, Long loginMemberId) {

        Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new ReplyNotFoundException());

        return ReplyInfo.of(reply);
    }

    public void deleteReply(Long replyId, Long loginMemberId) {

        Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new ReplyNotFoundException());

        if (!reply.getMember().getMemberId().equals(loginMemberId)) {
            throw new MemberAccessDeniedException();
        }

        replyRepository.deleteById(replyId);
    }
}

