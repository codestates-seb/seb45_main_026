package com.server.global.exception.businessexception.orderexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OrderNotFoundException extends OrderException {

    public static final String MESSAGE = "주문 정보를 찾을 수 없습니다.";
    public static final String CODE = "ORDER-404";

    public OrderNotFoundException() {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE);
    }
}
