package com.server.domain.reply.service;

import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.dto.ReplyDto;
import com.server.domain.reply.repository.ReplyRepository;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.replyException.ReplyNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ReplyService {

    private ReplyRepository replyRepository;
    private MemberRepository memberRepository;

    public ReplyService(ReplyRepository replyRepository, MemberRepository memberRepository) {
        this.replyRepository = replyRepository;
        this.memberRepository = memberRepository;
    }

    public List<Reply> getReply(Long videoId, int page, String sort, int star) {
        int replyListPage = 10;
        return replyRepository.findByVideo_VideoId(videoId, star, PageRequest.of(page - 1, replyListPage));
    }

    public Reply createReply(Long videoId, ReplyDto replyDto) {
        Member member = memberRepository.findById(replyDto.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException());

        Reply reply = new Reply();
        reply.setContent(replyDto.getContent());
        reply.setStar(replyDto.getStar());
        reply.setMember(member);

        return replyRepository.save(reply);
    }

    public Reply updateReply(Long replyId, ReplyDto replyDto) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new ReplyNotFoundException());

        reply.setContent(replyDto.getContent());
        reply.setStar(replyDto.getStar());

        return replyRepository.save(reply);
    }

    public void deleteReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new ReplyNotFoundException());

        replyRepository.delete(reply);
    }
}
