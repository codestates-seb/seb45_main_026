package com.server.domain.order.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CancelServiceResponse {

    private int requestAmount;
    private int totalCancelAmount;
    private int usedReward;
}
