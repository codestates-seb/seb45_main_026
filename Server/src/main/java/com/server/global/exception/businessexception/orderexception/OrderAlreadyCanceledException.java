package com.server.global.exception.businessexception.orderexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OrderAlreadyCanceledException extends OrderException {

    public static final String MESSAGE = "이미 취소된 주문입니다.";
    public static final String CODE = "ORDER-400";

    public OrderAlreadyCanceledException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
