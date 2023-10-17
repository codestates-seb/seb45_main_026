package com.server.domain.report.repository.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.server.domain.member.entity.MemberStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AnnouncementReportData {

    private Long announcementId;
    private String content;
    private Long memberId;
    private Long reportCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastReportedDate;

    @QueryProjection
    public AnnouncementReportData(Long announcementId, String content, Long memberId, Long reportCount, LocalDateTime createdDate, LocalDateTime lastReportedDate) {

        this.announcementId = announcementId;
        this.content = content;
        this.memberId = memberId;
        this.reportCount = reportCount;
        this.createdDate = createdDate;
        this.lastReportedDate = lastReportedDate;
    }
}
