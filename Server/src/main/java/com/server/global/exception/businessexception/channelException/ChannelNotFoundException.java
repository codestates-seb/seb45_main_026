package com.server.global.exception.businessexception.channelException;

import org.springframework.http.HttpStatus;

public class ChannelNotFoundException extends ChannelException {
    private static final String CODE = "CHANNEL-401";
    private static final String MESSAGE = "존재하지않는 채널입니다.";

    public ChannelNotFoundException() {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE);
    }
}
