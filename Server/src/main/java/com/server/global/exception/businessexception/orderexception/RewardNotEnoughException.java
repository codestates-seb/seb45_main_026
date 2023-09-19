package com.server.global.exception.businessexception.orderexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RewardNotEnoughException extends OrderException {

    public static final String MESSAGE = "reward 가 부족합니다.";
    public static final String CODE = "ORDER-400";

    public RewardNotEnoughException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
