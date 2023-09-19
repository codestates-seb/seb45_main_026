package com.server.global.exception.businessexception.s3exception;

import com.server.global.exception.businessexception.BusinessException;
import org.springframework.http.HttpStatus;

public abstract class S3Exception extends BusinessException {

    protected S3Exception(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
