package com.server.global.exception.businessexception.orderexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OrderExistException extends OrderException {

    public static final String MESSAGE = "이미 구매한 상품입니다. : ";
    public static final String CODE = "ORDER-400";

    public OrderExistException(String videoName) {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
