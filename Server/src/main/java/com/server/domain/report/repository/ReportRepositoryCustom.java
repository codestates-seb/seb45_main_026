package com.server.domain.report.repository;

import com.server.domain.member.entity.Member;
import com.server.domain.report.entity.Report;
import com.server.domain.report.entity.ReportType;
import com.server.domain.report.repository.dto.response.AnnouncementReportData;
import com.server.domain.report.repository.dto.response.ChannelReportData;
import com.server.domain.report.repository.dto.response.ReplyReportData;
import com.server.domain.report.repository.dto.response.VideoReportData;
import com.server.domain.report.service.dto.response.ReportDetailResponse;
import com.server.domain.video.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportRepositoryCustom {

    boolean existsByMemberIdAndVideoId(Long memberId, Long videoId);

    boolean existsByMemberIdAndReplyId(Long memberId, Long replyId);

    boolean existsByMemberIdAndChannelId(Long memberId, Long channelId);

    boolean existsByMemberIdAndAnnouncementId(Long memberId, Long announcementId);

    Page<VideoReportData> findVideoReportDataByCond(Pageable pageable, String sort);

    Page<ReplyReportData> findReplyReportDataByCond(Pageable pageable, String sort);

    Page<ChannelReportData> findChannelReportDataByCond(Pageable pageable, String sort);

    Page<AnnouncementReportData> findAnnouncementReportDataByCond(Pageable pageable, String sort);

    Page<? extends Report> findReportDetailByCond(Long entityId, Pageable pageable, ReportType reportType);

    Page<Member> findMemberByKeyword(String keyword, Pageable pageable);

    Page<Video> findVideoByKeyword(String email, String keyword, Pageable pageable);
}
