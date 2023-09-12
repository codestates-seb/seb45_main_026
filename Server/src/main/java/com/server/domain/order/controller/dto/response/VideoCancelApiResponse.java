package com.server.domain.order.controller.dto.response;

import com.server.domain.order.service.dto.response.CancelServiceResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class VideoCancelApiResponse {

    private int totalRequest;
    private int totalCancelAmount;
    private int totalCancelReward;
    private int usedReward;

    public static VideoCancelApiResponse of(CancelServiceResponse response){
        return VideoCancelApiResponse.builder()
                .totalRequest(response.getTotalRequest())
                .totalCancelAmount(response.getTotalCancelAmount())
                .totalCancelReward(response.getTotalCancelReward())
                .usedReward(response.getUsedReward())
                .build();
    }
}
