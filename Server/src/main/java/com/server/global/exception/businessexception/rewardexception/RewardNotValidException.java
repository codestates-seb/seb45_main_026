package com.server.global.exception.businessexception.rewardexception;

import org.springframework.http.HttpStatus;

public class RewardNotValidException extends RewardException {
    private static final String CODE = "REWARD-400";
    private static final String MESSAGE = "유효하지 않은 리워드 요청입니다.";

    public RewardNotValidException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
