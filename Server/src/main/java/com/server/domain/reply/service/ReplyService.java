package com.server.domain.reply.service;

import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.reply.controller.convert.ReplySort;
import com.server.domain.reply.dto.ReplyCreateResponse;
import com.server.domain.reply.dto.ReplyCreateServiceApi;
import com.server.domain.reply.dto.ReplyInfo;
import com.server.domain.reply.dto.ReplyUpdateServiceApi;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.repository.ReplyRepository;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.replyException.ReplyDuplicateException;
import com.server.global.exception.businessexception.replyException.ReplyNotFoundException;
import com.server.global.exception.businessexception.replyException.ReplyNotValidException;
import com.server.global.exception.businessexception.videoexception.VideoNotFoundException;
import com.server.module.s3.service.AwsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;
    private final VideoRepository videoRepository;




    public ReplyService(ReplyRepository replyRepository, MemberRepository memberRepository, VideoRepository videoRepository) {
        this.replyRepository = replyRepository;
        this.memberRepository = memberRepository;
        this.videoRepository = videoRepository;
    }

    @Transactional(readOnly = true)
    public Page<ReplyInfo> getReplies(Long videoId, int page, int size, ReplySort replySort, Integer star) {
        Sort sort = Sort.by(Sort.Direction.DESC, replySort.getSort());
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        if (star != null) { //(별점 필터링 o)
            return replyRepository.findAllByVideoIdAndStarOrStarIsNull(videoId, star, pageRequest);
        } else { //(별점 필터링 x)
            return replyRepository.findAllByVideoIdPaging(videoId, pageRequest);
        }
    }

    public Long createReply(Long loginMemberId, Long videoId, ReplyCreateServiceApi request) {
        Member member = memberRepository.findById(loginMemberId).orElseThrow(() -> new MemberAccessDeniedException());
        Integer star = request.getStar();

        if (star < 1 || star > 10) {
            throw new ReplyNotValidException();
        }

        Video video = videoRepository.findById(videoId).orElseThrow(() -> new VideoNotFoundException());

        Long loginMember = member.getMemberId();
        memberRepository.findById(loginMember);

        Reply reply = Reply.builder()
                .content(request.getContent())
                .star(request.getStar())
                .member(member)
                .video(video)
                .build();

        return replyRepository.save(reply).getReplyId();


    }


    public void updateReply(Long loginMemberId, Long replyId, ReplyUpdateServiceApi request) {

        Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new ReplyNotFoundException());

            if (!reply.getMember().getMemberId().equals(loginMemberId)) {
                throw new MemberAccessDeniedException();
            }

        reply.updateReply(request.getContent(), request.getStar());
    }

    @Transactional(readOnly = true)
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

