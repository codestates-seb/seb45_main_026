package com.server.domain.report.repository.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.server.domain.video.entity.VideoStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class VideoReportData {

    private Long videoId;
    private String videoName;
    private VideoStatus videoStatus;
    private Long reportCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastReportedDate;

    @QueryProjection
    public VideoReportData(Long videoId, String videoName, VideoStatus videoStatus, Long reportCount, LocalDateTime createdDate, LocalDateTime lastReportedDate) {
        this.videoId = videoId;
        this.videoName = videoName;
        this.videoStatus = videoStatus;
        this.reportCount = reportCount;
        this.createdDate = createdDate;
        this.lastReportedDate = lastReportedDate;
    }
}
