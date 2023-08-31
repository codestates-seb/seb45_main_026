package com.server.domain.reply.service;

import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.dto.ReplyDto;
import com.server.domain.reply.repository.ReplyRepository;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.replyException.ReplyNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    public List<ReplyDto.ReplyResponse> getReplies(Long videoId, int page, String sort, int star) {

        Sort replySort; // 정렬 기준

        switch (sort) {
            case "createdAt":
                replySort = Sort.by(Sort.Direction.DESC, "createdAt");
                break;
            case "star":
                replySort = Sort.by(Sort.Direction.DESC, "star");
                break;
            case "starDescending": //별점낮은 순으로 정렬
                replySort = Sort.by(Sort.Direction.ASC, "star");
                break;
            default:
                replySort = Sort.by(Sort.Direction.DESC, "createdAt");
                break;
        }

        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<Reply> replies = replyRepository.findRepliesBy(videoId, star, pageRequest);

        return ReplyDto.ReplyListOf(replies);
    }

    public Reply createReply(Long memberId, ReplyDto.ReplyResponse replyDto) {

        Member member = memberRepository.findById(replyDto.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException());

        Reply reply = new Reply();
        reply.setContent(replyDto.getContent());
        reply.setStar(replyDto.getStar());
        reply.setMember(member);

        return replyRepository.save(reply);
    }

    public Reply updateReply(Long memberId, ReplyDto.ReplyResponse replyDto) {

        Reply reply = replyRepository.findById(memberId)
                .orElseThrow(() -> new ReplyNotFoundException());

        reply = Reply.builder()
                .replyId(reply.getReplyId())
                .member(reply.getMember())
                .content(replyDto.getContent())
                .star(replyDto.getStar())
                .createdAt(reply.getCreatedAt())
                .build();

        return replyRepository.save(reply);
    }

    public void deleteReply(Long replyId, Long memberId) {

        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new ReplyNotFoundException());

        replyRepository.delete(reply);
    }
}
