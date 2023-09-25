package com.server.domain.report.service.dto.response;

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
    private Long reportCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastReportedDate;
}
