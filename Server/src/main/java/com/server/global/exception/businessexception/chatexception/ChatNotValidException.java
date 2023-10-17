package com.server.global.exception.businessexception.chatexception;

import org.springframework.http.HttpStatus;

public class ChatNotValidException extends ChatException {
    private static final String CODE = "CHAT-400";
    private static final String MESSAGE = "유효하지 않은 채팅방입니다.";

    public ChatNotValidException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
