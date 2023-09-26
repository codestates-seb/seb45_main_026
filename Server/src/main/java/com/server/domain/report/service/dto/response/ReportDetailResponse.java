package com.server.domain.report.service.dto.response;

import com.server.domain.member.entity.Member;
import com.server.domain.report.entity.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

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
                .memberId(Optional.ofNullable(report.getMember()).map(Member::getMemberId).orElse(null))
                .email(Optional.ofNullable(report.getMember()).map(Member::getEmail).orElse(null))
                .nickname(Optional.ofNullable(report.getMember()).map(Member::getNickname).orElse(null))
                .build();
    }
}
