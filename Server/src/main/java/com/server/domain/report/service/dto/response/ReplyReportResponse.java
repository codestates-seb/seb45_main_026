package com.server.domain.report.service.dto.response;

import com.server.domain.video.entity.VideoStatus;
import com.server.domain.video.repository.dto.response.VideoReportData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class ReplyReportResponse {

    private Long videoId;
    private String videoName;
    private Long replyId;
    private String content;
    private Long reportCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastReportedDate;
}
