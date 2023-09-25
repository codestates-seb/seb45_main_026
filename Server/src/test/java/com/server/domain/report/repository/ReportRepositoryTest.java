package com.server.domain.report.repository;

import com.server.domain.member.entity.Member;
import com.server.domain.report.entity.Report;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

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
}