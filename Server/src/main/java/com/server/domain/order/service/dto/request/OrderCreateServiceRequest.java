package com.server.domain.order.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.AccessType;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class OrderCreateServiceRequest {

    private Integer reward;
    private List<Long> videoIds;
}
