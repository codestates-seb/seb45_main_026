package com.server.domain.order.service.dto.response;

import com.server.domain.order.repository.dto.AdjustmentData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class AdjustmentResponse {

    private Long videoId;
    private String videoName;
    private Integer totalSaleAmount;
    private Integer refundAmount;

    public static AdjustmentResponse of(AdjustmentData data) {
        return AdjustmentResponse.builder()
                .videoId(data.getVideoId())
                .videoName(data.getVideoName())
                .totalSaleAmount(data.getTotalSaleAmount())
                .refundAmount(data.getRefundAmount())
                .build();
    }
}
