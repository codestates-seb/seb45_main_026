package com.server.global.exception.businessexception.rewardexception;

import com.server.global.exception.businessexception.BusinessException;
import org.springframework.http.HttpStatus;

public abstract class RewardException extends BusinessException {

    protected RewardException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
