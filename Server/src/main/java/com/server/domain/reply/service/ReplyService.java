package com.server.domain.reply.service;

import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.reply.controller.convert.ReplySort;
import com.server.domain.reply.dto.*;
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
import com.server.global.exception.businessexception.videoexception.VideoNotPurchasedException;
import com.server.module.s3.service.AwsService;
import com.server.module.s3.service.dto.FileType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;
    private final VideoRepository videoRepository;
    private final AwsService awsService;


    public ReplyService(ReplyRepository replyRepository,
                        MemberRepository memberRepository,
                        VideoRepository videoRepository,
                        AwsService awsService) {

        this.replyRepository = replyRepository;
        this.memberRepository = memberRepository;
        this.videoRepository = videoRepository;
        this.awsService = awsService;
    }

    @Transactional(readOnly = true)
    public Page<ReplyInfo> getReplies(Long videoId, int page, int size, ReplySort replySort, Integer star) {

       Sort sort = (replySort == ReplySort.STAR)
               ? Sort.by(Sort.Direction.DESC, "star", "createdDate")
               : Sort.by(Sort.Direction.DESC, "createdDate");

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<Reply> replies = (star != null)
                ? replyRepository.findAllByVideoIdAndStarOrStarIsNull(videoId, star, pageRequest)
                : replyRepository.findAllByVideoIdPaging(videoId, pageRequest);

        Page<ReplyInfo> replyInfoPage = replies.map(reply -> {
            String imageUrl = awsService.getFileUrl(reply.getMember().getMemberId(), reply.getMember().getImageFile(), FileType.PROFILE_IMAGE);
            return ReplyInfo.of(reply, imageUrl);
        });

        return replyInfoPage;
    }

    public Long createReply(Long loginMemberId, Long videoId, CreateReply reply) {

        Member findLoginMember = findMember(loginMemberId);
        Video video = findVideo(videoId);

        validateReply(loginMemberId, video, reply);
        existReplies(loginMemberId, videoId);
        evaluateStar(reply.getStar());

        Reply newReply = Reply.createReply(findLoginMember, video, reply);

        replyRepository.save(newReply);

        video.calculateStar();
        videoRepository.save(video);

        return newReply.getReplyId();
    }

    public void updateReply(Long loginMemberId, Long replyId, ReplyUpdateServiceApi request) {

        Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyNotFoundException::new);

            if (!reply.getMember().getMemberId().equals(loginMemberId)) {
                throw new MemberAccessDeniedException();
            }

        reply.updateReply(request.getContent(), request.getStar());

        replyRepository.save(reply);

        reply.getVideo().calculateStar();
        videoRepository.save(reply.getVideo());
    }

    @Transactional(readOnly = true)
    public ReplyInfo getReply(Long replyId) {

        Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyNotFoundException::new);

        return ReplyInfo.builder()
                .replyId(reply.getReplyId())
                .content(reply.getContent())
                .star(reply.getStar())
                .member(MemberInfo.of(reply.getMember().getMemberId(),
                                      awsService.getFileUrl(reply.getMember().getMemberId(), reply.getMember().getImageFile(), FileType.PROFILE_IMAGE),
                                      reply.getMember().getNickname()))
                .createdDate(reply.getCreatedDate())
                .build();
    }

    public void deleteReply(Long replyId, Long loginMemberId) {

        Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyNotFoundException::new);

        if (!reply.getMember().getMemberId().equals(loginMemberId)) {
            throw new MemberAccessDeniedException();
        }

        Video video = reply.getVideo();
        replyRepository.deleteById(replyId);
        videoRepository.save(video);

        video.calculateStar();
    }

    private void existReplies(Long loginMemberId, Long videoId) {
        if (!replyRepository.findAllByMemberIdAndVideoId(loginMemberId, videoId).isEmpty()) {
            throw new ReplyDuplicateException();
        }
        replyRepository.findAllByMemberIdAndVideoId(loginMemberId, videoId);
    }

    private void validateReply(Long loginMemberId, Video video, CreateReply reply) {
        Boolean isPurchased = memberRepository.checkMemberPurchaseVideo(loginMemberId, video.getVideoId());
        if (!isPurchased) {
            throw new VideoNotPurchasedException();
        }
    }

    private void evaluateStar(Integer star) {
        if (star < 1 || star > 10) {
            throw new ReplyNotValidException();
        }
    }

   private Video findVideo(Long videoId) {
        return videoRepository.findById(videoId).orElseThrow(VideoNotFoundException::new);
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }

}

