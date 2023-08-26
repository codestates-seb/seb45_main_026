package com.server.domain.order.controller.dto.request;

import com.server.domain.order.service.dto.request.OrderCreateServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrderCreateApiRequest {

    private Integer reward;
    private List<Long> videoIds;

    public OrderCreateServiceRequest toServiceRequest() {
        return OrderCreateServiceRequest.builder()
                .reward(reward)
                .videoIds(videoIds)
                .build();
    }
}
