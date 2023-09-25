package com.server.domain.report.service;

import com.server.domain.announcement.entity.Announcement;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.reply.entity.Reply;
import com.server.domain.report.entity.Report;
import com.server.domain.report.entity.ReportType;
import com.server.domain.report.repository.ReportRepository;
import com.server.domain.report.service.dto.response.*;
import com.server.domain.video.entity.Video;
import com.server.global.exception.businessexception.videoexception.VideoNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
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
        return null;
    }

    public Page<ReplyReportResponse> getReportReplies(int page, int size, String sort) {
        return null;
    }


    public Page<ChannelReportResponse> getReportChannels(int page, int size, String sort) {

        return null;
    }

    public Page<AnnouncementReportResponse> getReportAnnouncements(int page, int size, String sort) {
        return null;
    }

    public Page<ReportDetailResponse> getReportDetails(Long entityId, int page, int size, ReportType reportType) {
        return null;
    }

    public boolean blockMember(Long memberId) {
        return false;
    }
}
