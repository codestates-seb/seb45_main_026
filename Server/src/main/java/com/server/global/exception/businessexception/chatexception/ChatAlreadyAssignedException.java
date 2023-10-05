package com.server.global.exception.businessexception.chatexception;

import org.springframework.http.HttpStatus;

public class ChatAlreadyAssignedException extends ChatException {
    private static final String CODE = "CHAT-409";
    private static final String MESSAGE = "이미 채팅이 배정되었습니다.";

    public ChatAlreadyAssignedException() {
        super(CODE, HttpStatus.CONFLICT, MESSAGE);
    }
}
