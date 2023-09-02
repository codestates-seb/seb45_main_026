package com.server.global.exception.businessexception.subscribeException;

import com.server.global.exception.businessexception.replyException.ReplyException;
import org.springframework.http.HttpStatus;

public class SubscribeNotFoundException extends ReplyException {
    private static final String CODE = "SUBSCRIBE-401";
    private static final String MESSAGE = "구독하지않는 채널입니다.";

    public SubscribeNotFoundException() {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE);
    }
}
