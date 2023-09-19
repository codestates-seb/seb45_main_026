package com.server.domain.video.service.dto.response;

import com.server.domain.video.repository.dto.response.VideoReportData;
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
    private Long reportCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastReportedDate;

    public static VideoReportResponse of(VideoReportData data) {
        return VideoReportResponse.builder()
                .videoId(data.getVideoId())
                .videoName(data.getVideoName())
                .reportCount(data.getReportCount())
                .createdDate(data.getCreatedDate())
                .lastReportedDate(data.getLastReportedDate())
                .build();
    }
}
