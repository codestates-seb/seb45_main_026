package com.server.domain.report.repository.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.server.domain.member.entity.MemberStatus;
import com.server.domain.video.entity.VideoStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChannelReportData {

    private Long memberId;
    private String channelName;
    private Long reportCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastReportedDate;

    @QueryProjection
    public ChannelReportData(Long memberId, String channelName, Long reportCount, LocalDateTime createdDate, LocalDateTime lastReportedDate) {
        this.memberId = memberId;
        this.channelName = channelName;
        this.reportCount = reportCount;
        this.createdDate = createdDate;
        this.lastReportedDate = lastReportedDate;
    }
}
