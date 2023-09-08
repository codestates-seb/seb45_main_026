package com.server.global.exception.businessexception.replyException;

import com.server.global.exception.businessexception.channelException.ChannelException;
import org.springframework.http.HttpStatus;

public class ReplyNotFoundException extends ReplyException {
    private static final String CODE = "REPLY-404";
    private static final String MESSAGE = "존재하지않는 댓글입니다.";

    public ReplyNotFoundException() {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE);
    }
}
