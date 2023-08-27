package com.server.global.exception.businessexception.orderexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OrderNotValidException extends OrderException {

    public static final String MESSAGE = "유효한 주문이 아닙니다.";
    public static final String CODE = "ORDER-400";

    public OrderNotValidException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
