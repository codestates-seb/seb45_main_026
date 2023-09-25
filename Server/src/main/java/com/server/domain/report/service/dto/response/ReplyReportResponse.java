package com.server.domain.report.service.dto.response;

import com.server.domain.report.repository.dto.response.ReplyReportData;
import com.server.domain.report.repository.dto.response.VideoReportData;
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

    public static ReplyReportResponse of(ReplyReportData data) {
        return ReplyReportResponse.builder()
                .videoId(data.getVideoId())
                .videoName(data.getVideoName())
                .replyId(data.getReplyId())
                .content(data.getContent())
                .reportCount(data.getReportCount())
                .createdDate(data.getCreatedDate())
                .lastReportedDate(data.getLastReportedDate())
                .build();
    }
}
