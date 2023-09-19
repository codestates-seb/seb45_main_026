package com.server.global.exception.businessexception.orderexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AdjustmentDateException extends OrderException {

    public static final String MESSAGE = "month 입력 시 year 는 필수 값입니다.";
    public static final String CODE = "ORDER-400";

    public AdjustmentDateException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
