package com.server.domain.order.controller.dto.request;

import com.server.domain.order.service.dto.request.OrderCreateServiceRequest;
import com.server.global.validation.EachNotBlank;
import com.server.global.validation.EachPositive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrderCreateApiRequest {

    @NotNull(message = "{validation.order.reward}")
    @Min(value = 0, message = "{validation.order.reward.min}")
    private Integer reward;
    @NotNull(message = "{validation.order.videoIds}")
    @Size(min = 1, message = "{validation.order.videoIds.size}")
    @EachPositive(message = "{validation.positive}")
    private List<Long> videoIds;

    public OrderCreateServiceRequest toServiceRequest() {
        return OrderCreateServiceRequest.builder()
                .reward(reward)
                .videoIds(videoIds)
                .build();
    }
}
