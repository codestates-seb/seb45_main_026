package com.server.domain.report.service.dto.response;

import com.server.domain.video.entity.VideoStatus;
import com.server.domain.report.repository.dto.response.VideoReportData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class VideoReportResponse {

    private Long videoId;
    private String videoName;
    private VideoStatus videoStatus;
    private Long reportCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastReportedDate;

    public static VideoReportResponse of(VideoReportData data) {
        return VideoReportResponse.builder()
                .videoId(data.getVideoId())
                .videoName(data.getVideoName())
                .videoStatus(data.getVideoStatus())
                .reportCount(data.getReportCount())
                .createdDate(data.getCreatedDate())
                .lastReportedDate(data.getLastReportedDate())
                .build();
    }
}
