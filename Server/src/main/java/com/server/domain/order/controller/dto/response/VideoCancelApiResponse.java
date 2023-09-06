package com.server.domain.order.controller.dto.response;

import com.server.domain.order.service.dto.response.CancelServiceResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class VideoCancelApiResponse {

    private int requestAmount;
    private int totalCancelAmount;
    private int usedReward;

    public static VideoCancelApiResponse of(CancelServiceResponse response){
        return VideoCancelApiResponse.builder()
                .requestAmount(response.getRequestAmount())
                .totalCancelAmount(response.getTotalCancelAmount())
                .usedReward(response.getUsedReward())
                .build();
    }
}
