package com.server.domain.reply.service;

import com.server.auth.util.SecurityUtil;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.reply.controller.convert.ReplySort;
import com.server.domain.reply.dto.*;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.repository.ReplyRepository;
import com.server.domain.report.service.ReportService;
import com.server.domain.reward.service.RewardService;
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
    private final RewardService rewardService;
    private final ReportService reportService;


    public ReplyService(ReplyRepository replyRepository,
                        MemberRepository memberRepository,
                        VideoRepository videoRepository,
                        AwsService awsService,
                        RewardService rewardService, ReportService reportService) {

        this.replyRepository = replyRepository;
        this.memberRepository = memberRepository;
        this.videoRepository = videoRepository;
        this.awsService = awsService;
        this.rewardService = rewardService;
        this.reportService = reportService;
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
            String imageUrl = awsService.getFileUrl(reply.getMember().getImageFile(), FileType.PROFILE_IMAGE);
            return ReplyInfo.of(reply, imageUrl);
        });

        return replyInfoPage;
    }

    public Long createReply(Long loginMemberId, Long videoId, CreateReply createReply) {

        Member findLoginMember = findMember(loginMemberId);
        Video video = findVideo(videoId);

        validateReply(loginMemberId, video);
        existReplies(loginMemberId, videoId);
        evaluateStar(createReply.getStar());

        Reply reply = Reply.newReply(findLoginMember, video, createReply);
        rewardService.createRewardIfNotPresent(reply, findLoginMember);

        replyRepository.save(reply);

        video.calculateStar();

        return reply.getReplyId();
    }

    public void updateReply(Long loginMemberId, Long replyId, ReplyUpdateServiceApi request) {

        Reply reply = verifiedReply(replyId);

            if (!reply.getMember().getMemberId().equals(loginMemberId)) {
                throw new MemberAccessDeniedException();
            }

        reply.updateReply(request.getContent(), request.getStar());

        reply.getVideo().calculateStar();
    }

    @Transactional(readOnly = true)
    public ReplyInfo getReply(Long replyId) {

        Reply reply = verifiedReply(replyId);

        return ReplyInfo.builder()
                .replyId(reply.getReplyId())
                .content(reply.getContent())
                .star(reply.getStar())
                .member(MemberInfo.of(reply.getMember().getMemberId(),
                                      awsService.getFileUrl(reply.getMember().getImageFile(), FileType.PROFILE_IMAGE),
                                      reply.getMember().getNickname()))
                .createdDate(reply.getCreatedDate())
                .build();
    }

    public void deleteReply(Long replyId, Long loginMemberId) {
        Reply reply = replyRepository.findByIdWithVideo(replyId).orElseThrow(ReplyNotFoundException::new);

        checkDeleteAuthority(loginMemberId, reply);

        Video video = reply.getVideo();
        video.getReplies().remove(reply);

        replyRepository.delete(reply);

        video.calculateStar();
    }

    private void checkDeleteAuthority(Long loginMemberId, Reply reply) {

        if(SecurityUtil.isAdmin()) return;

        if (!reply.getMember().getMemberId().equals(loginMemberId)) {
            throw new MemberAccessDeniedException();
        }
    }

    public boolean reportReply(Long loginMemberId, Long replyId, String reportContent) {

        Reply reply = verifiedReply(replyId);

        Member reporter = findMember(loginMemberId);

        return reportService.reportReply(reporter, reply, reportContent);
    }

    private Reply verifiedReply(Long replyId) {
        return replyRepository.findById(replyId).orElseThrow(ReplyNotFoundException::new);
    }

    private void existReplies(Long loginMemberId, Long videoId) {
        if (!replyRepository.findAllByMemberIdAndVideoId(loginMemberId, videoId).isEmpty()) {
            throw new ReplyDuplicateException();
        }
        replyRepository.findAllByMemberIdAndVideoId(loginMemberId, videoId);
    }

    private void validateReply(Long loginMemberId, Video video) {
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

