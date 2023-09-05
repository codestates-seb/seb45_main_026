package com.server.domain.order.controller.dto.response;

import com.server.domain.order.service.dto.response.VideoCancelServiceResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class VideoCancelApiResponse {

    private int totalCancelAmount;
    private int usedReward;

    public static VideoCancelApiResponse of(VideoCancelServiceResponse response){
        return VideoCancelApiResponse.builder()
                .totalCancelAmount(response.getTotalCancelAmount())
                .usedReward(response.getUsedReward())
                .build();
    }
}
