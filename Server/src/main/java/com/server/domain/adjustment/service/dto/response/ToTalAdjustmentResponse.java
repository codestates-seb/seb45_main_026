package com.server.domain.adjustment.service.dto.response;

import com.server.domain.adjustment.domain.AdjustmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class ToTalAdjustmentResponse {

        private Integer amount;
        private AdjustmentStatus adjustmentStatus;
        private String reason;
        private List<MonthAdjustmentResponse> monthData;

        public static ToTalAdjustmentResponse of(Integer amount,
                                                 AdjustmentStatus adjustmentStatus,
                                                 String reason,
                                                 List<MonthAdjustmentResponse> monthData) {
                return ToTalAdjustmentResponse.builder()
                        .amount(amount)
                        .adjustmentStatus(adjustmentStatus)
                        .reason(reason)
                        .monthData(monthData)
                        .build();
        }
}
