package com.server.global.exception.businessexception.subscribeException;

import com.server.global.exception.businessexception.BusinessException;
import org.springframework.http.HttpStatus;

public abstract class SubscribeException extends BusinessException {

    protected SubscribeException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
