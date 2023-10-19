package com.server.domain.report.service;

import com.server.domain.announcement.entity.Announcement;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.member.entity.MemberStatus;
import com.server.domain.reply.entity.Reply;
import com.server.domain.report.entity.Report;
import com.server.domain.report.entity.ReportType;
import com.server.domain.report.repository.ReportRepository;
import com.server.domain.report.service.dto.request.MemberBlockServiceRequest;
import com.server.domain.report.service.dto.response.*;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import com.server.module.redis.service.RedisService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final RedisService redisService;
    private final VideoRepository videoRepository;

    public ReportService(ReportRepository reportRepository, RedisService redisService, VideoRepository videoRepository) {
        this.reportRepository = reportRepository;
        this.redisService = redisService;
        this.videoRepository = videoRepository;
    }

    @Transactional
    public boolean reportVideo(Member member, Video video, String reportContent) {

        if(isAlreadyReport(member.getMemberId(), video.getVideoId(), ReportType.VIDEO)) {
            return false;
        }

        reportRepository.save(Report.createVideoReport(member, video, reportContent));

        return true;
    }

    @Transactional
    public boolean reportReply(Member member, Reply reply, String reportContent) {

        if(isAlreadyReport(member.getMemberId(), reply.getReplyId(), ReportType.REPLY)) {
            return false;
        }

        reportRepository.save(Report.createReplyReport(member, reply, reportContent));

        return true;
    }

    @Transactional
    public boolean reportChannel(Member member, Channel channel, String reportContent) {

        if(isAlreadyReport(member.getMemberId(), channel.getChannelId(), ReportType.CHANNEL)) {
            return false;
        }

        reportRepository.save(Report.createChannelReport(member, channel, reportContent));

        return true;
    }

    @Transactional
    public boolean reportAnnouncement(Member member, Announcement announcement, String reportContent) {

        if(isAlreadyReport(member.getMemberId(), announcement.getAnnouncementId(), ReportType.ANNOUNCEMENT)) {
            return false;
        }

        reportRepository.save(Report.createAnnouncementReport(member, announcement, reportContent));

        return true;
    }

    private boolean isAlreadyReport(Long loginMemberId, Long entityId, ReportType reportType) {

        switch (reportType) {
            case VIDEO:
                return reportRepository.existsByMemberIdAndVideoId(loginMemberId, entityId);
            case REPLY:
                return reportRepository.existsByMemberIdAndReplyId(loginMemberId, entityId);
            case CHANNEL:
                return reportRepository.existsByMemberIdAndChannelId(loginMemberId, entityId);
            case ANNOUNCEMENT:
                return reportRepository.existsByMemberIdAndAnnouncementId(loginMemberId, entityId);
            default:
                return false;
        }
    }

    public Page<VideoReportResponse> getReportVideos(int page, int size, String sort) {

        Pageable pageable = PageRequest.of(page, size);

        return reportRepository.findVideoReportDataByCond(pageable, sort).map(VideoReportResponse::of);
    }

    public Page<ReplyReportResponse> getReportReplies(int page, int size, String sort) {

        Pageable pageable = PageRequest.of(page, size);

        return reportRepository.findReplyReportDataByCond(pageable, sort).map(ReplyReportResponse::of);
    }


    public Page<ChannelReportResponse> getReportChannels(int page, int size, String sort) {

        Pageable pageable = PageRequest.of(page, size);

        return reportRepository.findChannelReportDataByCond(pageable, sort)
                .map(data -> {
                    MemberStatus memberStatus = MemberStatus.ACTIVE;
                    String blockReason = null;
                    LocalDateTime blockEndDate = null;

                    if(redisService.isExist(data.getMemberId().toString())) {
                        memberStatus = MemberStatus.BLOCKED;
                        blockReason = redisService.getData(data.getMemberId().toString());
                        long expireDuration = redisService.getExpire(data.getMemberId().toString());
                        blockEndDate = LocalDateTime.now().plusSeconds(expireDuration);
                    }

                    return ChannelReportResponse.of(data, memberStatus, blockReason, blockEndDate);
                });
    }

    public Page<AnnouncementReportResponse> getReportAnnouncements(int page, int size, String sort) {

        Pageable pageable = PageRequest.of(page, size);

        return reportRepository.findAnnouncementReportDataByCond(pageable, sort).map(AnnouncementReportResponse::of);
    }

    public Page<ReportDetailResponse> getReportDetails(Long entityId, int page, int size, ReportType reportType) {

        Pageable pageable = PageRequest.of(page, size);

        return reportRepository.findReportDetailByCond(entityId, pageable, reportType).map(ReportDetailResponse::of);
    }

    @Transactional
    public boolean blockMember(Long memberId, MemberBlockServiceRequest request) {

        if(redisService.isExist(memberId.toString())) {
            redisService.deleteData(memberId.toString());
            return false;
        }

        long duration = request.getDays() * 24 * 60 * 60;

        redisService.setExpire(memberId.toString(), request.getBlockReason(), duration);

        List<Video> videos = videoRepository.findByMemberId(memberId);

        videos.forEach(Video::adminClose);

        return true;
    }

    public Page<AdminMemberResponse> getMembers(String keyword, int page, int size) {

        Page<Member> members = reportRepository.findMemberByKeyword(keyword, PageRequest.of(page, size));

        return members.map(member -> {
                    String blockReason = null;
                    LocalDateTime blockEndDate = null;

                    if(redisService.isExist(member.getMemberId().toString())) {
                        blockReason = redisService.getData(member.getMemberId().toString());
                        long expireDuration = redisService.getExpire(member.getMemberId().toString());
                        blockEndDate = LocalDateTime.now().plusSeconds(expireDuration);
                    }

                    return AdminMemberResponse.of(member, blockReason, blockEndDate);
                });
    }

    public Page<AdminVideoResponse> getVideos(String email, String keyword, int page, int size) {

        Page<Video> videos = reportRepository.findVideoByKeyword(email, keyword, PageRequest.of(page, size));

        return videos.map(AdminVideoResponse::of);
    }
}
