package com.server.global.exception.businessexception.channelException;

import com.server.global.exception.businessexception.BusinessException;
import org.springframework.http.HttpStatus;

public abstract class ChannelException extends BusinessException {

    protected ChannelException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
