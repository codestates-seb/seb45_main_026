package com.server.domain.order.entity;

import com.server.global.entity.BaseEnum;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum OrderStatus implements BaseEnum {
    ORDERED("주문 완료"),
    CANCELED("주문 취소"),
    COMPLETED("결제 완료")
    ;

    private final String description;
    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
