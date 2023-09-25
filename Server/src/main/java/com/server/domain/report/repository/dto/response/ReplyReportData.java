package com.server.domain.report.repository.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.server.domain.video.entity.VideoStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReplyReportData {

    private Long videoId;
    private String videoName;
    private Long replyId;
    private String content;
    private Long reportCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastReportedDate;

    @QueryProjection
    public ReplyReportData(Long videoId, String videoName, Long replyId, String content, Long reportCount, LocalDateTime createdDate, LocalDateTime lastReportedDate) {
        this.videoId = videoId;
        this.videoName = videoName;
        this.replyId = replyId;
        this.content = content;
        this.reportCount = reportCount;
        this.createdDate = createdDate;
        this.lastReportedDate = lastReportedDate;
    }
}
