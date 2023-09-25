package com.server.domain.report.repository;

import com.server.domain.announcement.entity.Announcement;
import com.server.domain.member.entity.Member;
import com.server.domain.reply.entity.Reply;
import com.server.domain.report.entity.ChannelReport;
import com.server.domain.report.entity.Report;
import com.server.domain.report.entity.VideoReport;
import com.server.domain.report.repository.dto.response.AnnouncementReportData;
import com.server.domain.report.repository.dto.response.ChannelReportData;
import com.server.domain.report.repository.dto.response.ReplyReportData;
import com.server.domain.report.repository.dto.response.VideoReportData;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

class ReportRepositoryTest extends RepositoryTest {

    @Autowired ReportRepository reportRepository;

    @Test
    @DisplayName("회원이 비디오를 신고했는지 확인한다. 신고하지 않으면 false 를 반환한다.")
    void existsByMemberAndVideoFalse() {
        // given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());

        Member reporter = createMemberWithChannel();

        // when
        boolean result = reportRepository.existsByMemberIdAndVideoId(reporter.getMemberId(), video.getVideoId());

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("회원이 비디오를 신고했는지 확인한다. 신고하지 않으면 false 를 반환한다.")
    void existsByMemberAndVideoTrue() {
        // given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());

        Member reporter = createMemberWithChannel();
        Report report = Report.createVideoReport(reporter, video, "신고사유");
        em.persist(report);

        // when
        boolean result = reportRepository.existsByMemberIdAndVideoId(reporter.getMemberId(), video.getVideoId());

        // then
        assertThat(result).isTrue();
    }

    @TestFactory
    @DisplayName("신고된 목록을 조회한다.")
    Collection<DynamicTest> findReportDataByCond() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());

        Reply reply = createAndSaveReplies(owner, video);
        Announcement announcement = createAnnouncement(owner);

        Member reporter = createMemberWithChannel();

        Report videoReport = Report.createVideoReport(reporter, video, "신고사유");
        Report replyReport = Report.createReplyReport(reporter, reply, "신고사유");
        Report announcementReport = Report.createAnnouncementReport(reporter, announcement, "신고사유");
        Report channelReport = Report.createChannelReport(reporter, owner.getChannel(), "신고사유");

        em.persist(videoReport);
        em.persist(replyReport);
        em.persist(announcementReport);
        em.persist(channelReport);

        Pageable pageable = Pageable.ofSize(10).withPage(0);

        return List.of(
            dynamicTest("비디오 신고 목록 조회", () -> {
                //when
                Page<VideoReportData> reports = reportRepository.findVideoReportDataByCond(pageable, null);

                //then
                assertThat(reports.getContent()).hasSize(1);

                VideoReportData report = reports.getContent().get(0);
                assertThat(report.getVideoId()).isEqualTo(video.getVideoId());
                assertThat(report.getVideoName()).isEqualTo(video.getVideoName());
                assertThat(report.getReportCount()).isEqualTo(1);
                assertThat(report.getVideoStatus()).isEqualTo(video.getVideoStatus());

            }),
            dynamicTest("댓글 신고 목록 조회", ()-> {
                //when
                Page<ReplyReportData> reports = reportRepository.findReplyReportDataByCond(pageable, null);

                //then
                assertThat(reports.getContent()).hasSize(1);

                ReplyReportData report = reports.getContent().get(0);
                assertThat(report.getReplyId()).isEqualTo(reply.getReplyId());
                assertThat(report.getContent()).isEqualTo(reply.getContent());
                assertThat(report.getVideoId()).isEqualTo(video.getVideoId());
                assertThat(report.getVideoName()).isEqualTo(video.getVideoName());
                assertThat(report.getReportCount()).isEqualTo(1);
            }),
            dynamicTest("공지사항 신고 목록 조회", ()-> {
                //when
                Page<AnnouncementReportData> reports = reportRepository.findAnnouncementReportDataByCond(pageable, null);

                //then
                assertThat(reports.getContent()).hasSize(1);

                AnnouncementReportData report = reports.getContent().get(0);
                assertThat(report.getAnnouncementId()).isEqualTo(announcement.getAnnouncementId());
                assertThat(report.getContent()).isEqualTo(announcement.getContent());
                assertThat(report.getReportCount()).isEqualTo(1);

            }),
            dynamicTest("채널 신고 목록 조회", ()-> {
                //when
                Page<ChannelReportData> reports = reportRepository.findChannelReportDataByCond(pageable, null);

                //then
                assertThat(reports.getContent()).hasSize(1);

                ChannelReportData report = reports.getContent().get(0);
                assertThat(report.getMemberId()).isEqualTo(owner.getChannel().getChannelId());
                assertThat(report.getChannelName()).isEqualTo(owner.getChannel().getChannelName());
                assertThat(report.getReportCount()).isEqualTo(1);
            })






        );
    }

    private Announcement createAnnouncement(Member owner) {
        Announcement announcement = Announcement.createAnnouncement(owner.getChannel(), "content");
        em.persist(announcement);

        return announcement;
    }
}