package com.server.global.exception.businessexception.replyException;


import org.springframework.http.HttpStatus;

public class ReplyDuplicateException extends ReplyException {
    private static final String CODE = "REPLY-401";
    private static final String MESSAGE = "수강평은 1개만 작성 가능합니다.";

    public ReplyDuplicateException() {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE);
    }
}
