package com.server.global.exception.businessexception.answerexception;

import com.server.global.exception.businessexception.BusinessException;
import org.springframework.http.HttpStatus;

public abstract class AnswerException extends BusinessException {

    protected AnswerException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
