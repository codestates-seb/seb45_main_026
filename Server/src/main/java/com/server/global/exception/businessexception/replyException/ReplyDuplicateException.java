package com.server.global.exception.businessexception.replyException;

import org.springframework.http.HttpStatus;

public class ReplyDuplicateException extends ReplyException {
    private static final String CODE = "REPLY-400";
    private static final String MESSAGE = "수강평이 이미 존재합니다.";

    public ReplyDuplicateException() {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE);
    }
}