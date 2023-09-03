package com.server.domain.reply.service;

import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.reply.dto.ReplyResponse;
import com.server.domain.reply.dto.ReplyUpdate;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.repository.ReplyRepository;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.replyException.ReplyNotFoundException;
import com.server.global.exception.businessexception.replyException.ReplyNotValidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;


    public ReplyService(ReplyRepository replyRepository, MemberRepository memberRepository) {
        this.replyRepository = replyRepository;
        this.memberRepository = memberRepository;
    }

    public Page<ReplyResponse> getReplies(Long replyId, int page, int size, String sort) {

        PageRequest pageRequest = PageRequest.of(page, size , Sort.by("createdDate").descending());

        return ReplyResponse.of(replyRepository.findAllBy(pageRequest, sort));
    }



    public Reply createReply(Long loginMemberId, ReplyUpdate response) {

        Member loginMember = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new MemberNotFoundException());

        Integer star = response.getStar();

        if (star < 1 || star > 5) {
            throw new ReplyNotValidException();
        }

        Reply reply = Reply.builder()
                .member(loginMember)
                .content(response.getContent())
                .star(response.getStar())
                .build();

        return replyRepository.save(reply);
    }


    public Reply updateReply(Long loginMemberId, Long replyId, ReplyUpdate replyUpdate) {

        Member loginMember = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new MemberNotFoundException());

        Reply reply = existReply(replyId);

        reply.updateReply(replyUpdate.getContent(), replyUpdate.getStar());

        return reply;
    }

    public void deleteReply(Long replyId, Long loginMemberId) {

        Member member = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new MemberNotFoundException());

        Reply reply = existReply(replyId);

        replyRepository.delete(reply);
    }


    public Reply existReply(Long replyId) {

        return replyRepository.findById(replyId)
                .orElseThrow(() -> new ReplyNotFoundException());
    }

}
