package com.server.global.exception.businessexception.orderexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CancelFailException extends OrderException {

    public static final String MESSAGE = "결제 취소에 실패했습니다. 주문 정보를 다시 확인해주세요.";
    public static final String CODE = "ORDER-400";

    public CancelFailException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
