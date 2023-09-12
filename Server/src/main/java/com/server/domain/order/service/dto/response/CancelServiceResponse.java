package com.server.domain.order.service.dto.response;

import com.server.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CancelServiceResponse {

    private int totalRequest;
    private int totalCancelAmount;
    private int totalCancelReward;
    private int usedReward;

    public static CancelServiceResponse of(int totalRequest, Order.Refund refund){
        return CancelServiceResponse.builder()
                .totalRequest(totalRequest)
                .totalCancelAmount(refund.getRefundAmount())
                .totalCancelReward(refund.getRefundReward())
                .usedReward(totalRequest - refund.getRefundAmount() - refund.getRefundReward())
                .build();
    }
}
