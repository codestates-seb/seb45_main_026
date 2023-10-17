package com.server.domain.adjustment.repository.dto;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AdjustmentData {

    private Long videoId;
    private String videoName;
    private Integer totalSaleAmount;
    private Integer refundAmount;

    @QueryProjection
    public AdjustmentData(Long videoId, String videoName, Integer totalSaleAmount, Integer refundAmount) {
        this.videoId = videoId;
        this.videoName = videoName;
        this.totalSaleAmount = totalSaleAmount;
        this.refundAmount = refundAmount;
    }
}
