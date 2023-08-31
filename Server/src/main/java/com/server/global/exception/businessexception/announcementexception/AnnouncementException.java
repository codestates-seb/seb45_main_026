package com.server.global.exception.businessexception.announcementexception;

import com.server.global.exception.businessexception.BusinessException;
import org.springframework.http.HttpStatus;

public abstract class AnnouncementException extends BusinessException {

    protected AnnouncementException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
