package com.server.global.exception.businessexception.videoexception;

import com.server.global.exception.businessexception.BusinessException;
import org.springframework.http.HttpStatus;

public abstract class VideoException extends BusinessException {

    protected VideoException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
