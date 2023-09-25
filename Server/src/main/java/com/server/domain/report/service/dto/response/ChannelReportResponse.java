package com.server.domain.report.service.dto.response;

import com.server.domain.member.entity.MemberStatus;
import com.server.domain.report.repository.dto.response.AnnouncementReportData;
import com.server.domain.report.repository.dto.response.ChannelReportData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class ChannelReportResponse {

    private Long memberId;
    private String channelName;
    private MemberStatus memberStatus;
    private String blockReason;
    private LocalDateTime blockEndDate;
    private Long reportCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastReportedDate;

    public static ChannelReportResponse of(ChannelReportData data,
                                           MemberStatus memberStatus,
                                           String blockReason,
                                           LocalDateTime blockEndDate) {
        return ChannelReportResponse.builder()
                .memberId(data.getMemberId())
                .channelName(data.getChannelName())
                .memberStatus(memberStatus)
                .blockReason(blockReason)
                .blockEndDate(blockEndDate)
                .reportCount(data.getReportCount())
                .createdDate(data.getCreatedDate())
                .lastReportedDate(data.getLastReportedDate())
                .build();
    }
}
