package com.server.global.exception.businessexception.orderexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RewardExceedException extends OrderException {

    public static final String MESSAGE = "주문 금액보다 reward 가 초과합니다.";
    public static final String CODE = "ORDER-400";

    public RewardExceedException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
