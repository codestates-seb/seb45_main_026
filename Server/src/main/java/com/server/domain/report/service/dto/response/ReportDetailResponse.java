package com.server.domain.report.service.dto.response;

import com.server.domain.report.entity.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
public class ReportDetailResponse {

    private Long reportId;
    private String reportContent;
    private LocalDateTime createdDate;
    private Long memberId;
    private String email;
    private String nickname;

    public static ReportDetailResponse of(Report report) {
        return ReportDetailResponse.builder()
                .reportId(report.getReportId())
                .reportContent(report.getReportContent())
                .createdDate(report.getCreatedDate())
                .memberId(report.getMember().getMemberId())
                .email(report.getMember().getEmail())
                .nickname(report.getMember().getNickname())
                .build();
    }
}
