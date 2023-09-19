package com.server.domain.video.repository.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class VideoReportData {

    private Long videoId;
    private String videoName;
    private Long reportCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastReportedDate;

    @QueryProjection
    public VideoReportData(Long videoId, String videoName, Long reportCount, LocalDateTime createdDate, LocalDateTime lastReportedDate) {
        this.videoId = videoId;
        this.videoName = videoName;
        this.reportCount = reportCount;
        this.createdDate = createdDate;
        this.lastReportedDate = lastReportedDate;
    }
}
