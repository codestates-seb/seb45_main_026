package com.server.domain.report.service.dto.response;

import com.server.domain.report.repository.dto.response.AnnouncementReportData;
import com.server.domain.report.repository.dto.response.ReplyReportData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class AnnouncementReportResponse {

    private Long announcementId;
    private String content;
    private Long memberId;
    private Long reportCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastReportedDate;

    public static AnnouncementReportResponse of(AnnouncementReportData data) {
        return AnnouncementReportResponse.builder()
                .announcementId(data.getAnnouncementId())
                .content(data.getContent())
                .memberId(data.getMemberId())
                .reportCount(data.getReportCount())
                .createdDate(data.getCreatedDate())
                .lastReportedDate(data.getLastReportedDate())
                .build();
    }
}
