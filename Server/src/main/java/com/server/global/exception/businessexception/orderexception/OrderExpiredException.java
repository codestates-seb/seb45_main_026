package com.server.global.exception.businessexception.orderexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OrderExpiredException extends OrderException {

    public static final String MESSAGE = "주문 취소 기간이 만료되었습니다.";
    public static final String CODE = "ORDER-400";

    public OrderExpiredException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
