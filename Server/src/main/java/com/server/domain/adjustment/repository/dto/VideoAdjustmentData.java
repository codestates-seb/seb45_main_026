package com.server.domain.adjustment.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VideoAdjustmentData {

    private Long videoId;
    private String videoName;
    private Integer amount;

    @QueryProjection
    public VideoAdjustmentData(Long videoId, String videoName, Integer amount) {
        this.videoId = videoId;
        this.videoName = videoName;
        this.amount = amount;
    }
}
