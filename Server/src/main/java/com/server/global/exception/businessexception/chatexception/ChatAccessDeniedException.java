package com.server.global.exception.businessexception.chatexception;

import org.springframework.http.HttpStatus;

public class ChatAccessDeniedException extends ChatException {
    private static final String CODE = "CHAT-403";
    private static final String MESSAGE = "채팅방에 접근할 수 없습니다.";

    public ChatAccessDeniedException() {
        super(CODE, HttpStatus.FORBIDDEN, MESSAGE);
    }
}
