package com.server.global.exception.businessexception.accountexception;

import com.server.global.exception.businessexception.answerexception.AnswerException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AccountNotValidException extends AccountException {

    public static final String MESSAGE = "계좌 번호가 유효하지 않습니다.";
    public static final String CODE = "ANSWER-400";

    public AccountNotValidException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
