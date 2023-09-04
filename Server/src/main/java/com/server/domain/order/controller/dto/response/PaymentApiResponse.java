package com.server.domain.order.controller.dto.response;

import com.server.domain.order.service.dto.response.PaymentServiceResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class PaymentApiResponse {

    private String orderName;
    private String status;
    private Integer totalAmount;

    public static PaymentApiResponse of(PaymentServiceResponse serviceResponse) {
        return PaymentApiResponse.builder()
                .orderName(serviceResponse.getOrderName())
                .status(serviceResponse.getStatus())
                .totalAmount(serviceResponse.getTotalAmount())
                .build();
    }
}
