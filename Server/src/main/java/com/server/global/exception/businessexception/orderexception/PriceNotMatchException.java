package com.server.global.exception.businessexception.orderexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PriceNotMatchException extends OrderException {

    public static final String MESSAGE = "요청한 가격과 다릅니다.";
    public static final String CODE = "ORDER-400";

    public PriceNotMatchException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
