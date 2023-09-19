package com.server.domain.report.service;

import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.ServiceTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

class ReportServiceTest extends ServiceTest {

    @Autowired ReportService reportService;

    @TestFactory
    @DisplayName("비디오를 신고한다.")
    Collection<DynamicTest> reportVideo() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());

        Member reporter = createMemberWithChannel();

        return List.of(
                dynamicTest("비디오를 최초 신고하면 true 를 반환한다.", () -> {
                    //when
                    boolean result = reportService.reportVideo(reporter, video, "신고사유");

                    //then
                    assertThat(result).isTrue();
                    assertThat(reportRepository.findAll()).hasSize(1);
                }),
                dynamicTest("비디오를 동일 멤버가 중복 신고하면 false 를 반환한다.", ()-> {
                   //when
                    boolean result = reportService.reportVideo(reporter, video, "신고사유");

                    //then
                    assertThat(result).isFalse();
                })


        );
    }
}