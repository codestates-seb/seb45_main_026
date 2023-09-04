package com.server.global.exception.businessexception.replyException;

import com.server.global.exception.businessexception.BusinessException;
import org.springframework.http.HttpStatus;

public abstract class ReplyException extends BusinessException {

    protected ReplyException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
