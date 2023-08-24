package com.server.global.exception.businessexception.memberexception;

import org.springframework.http.HttpStatus;

import com.server.global.exception.businessexception.BusinessException;

public abstract class MemberException extends BusinessException {

    protected MemberException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
