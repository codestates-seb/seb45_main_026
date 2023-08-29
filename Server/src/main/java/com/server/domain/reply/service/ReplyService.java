package com.server.domain.reply.service;

import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.dto.ReplyDto;
import com.server.domain.reply.repository.ReplyRepository;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.replyException.ReplyNotFoundException;
import com.server.global.reponse.ApiPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;

    public ReplyService(ReplyRepository replyRepository, MemberRepository memberRepository) {
        this.replyRepository = replyRepository;
        this.memberRepository = memberRepository;
    }

    public ApiPageResponse<Reply> getReply(int page, Sort sort) {
        int replyList = 10;

        Pageable pageable = PageRequest.of(page, replyList, sort);
        Page<Reply> replyPage = replyRepository.findReplyBy(new Reply(), pageable);

        return ApiPageResponse.ok(replyPage);
    }


    public Reply createReply(Long memberId, Long loginMemberId, ReplyDto replyDto) {
        if(memberId.equals(loginMemberId)) {
            Member member = memberRepository.findById(replyDto.getMemberId())
                    .orElseThrow(MemberNotFoundException::new);

            Reply reply = new Reply();
            reply.setContent(replyDto.getContent());
            reply.setStar(replyDto.getStar());
            reply.setMember(member);
            return replyRepository.save(reply);

        } else throw new MemberAccessDeniedException();
    }


    public Reply updateReply(Long replyId, ReplyDto replyDto) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(ReplyNotFoundException::new);

        reply.setContent(replyDto.getContent());
        reply.setStar(replyDto.getStar());

        return replyRepository.save(reply);
    }

    public void deleteReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(ReplyNotFoundException::new);

        replyRepository.delete(reply);
    }
}
