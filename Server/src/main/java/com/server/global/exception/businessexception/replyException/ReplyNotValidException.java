package com.server.global.exception.businessexception.replyException;

import com.server.global.exception.businessexception.orderexception.OrderException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ReplyNotValidException extends OrderException {

    public static final String MESSAGE = "올바른 형식이 아닙니다.";
    public static final String CODE = "REPLY-400";

    public ReplyNotValidException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}

