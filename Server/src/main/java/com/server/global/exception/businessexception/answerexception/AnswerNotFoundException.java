package com.server.global.exception.businessexception.answerexception;

import com.server.global.exception.businessexception.memberexception.MemberException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AnswerNotFoundException extends AnswerException {

    public static final String MESSAGE = "존재하지 않는 답변입니다.";
    public static final String CODE = "ANSWER-404";

    public AnswerNotFoundException() {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE);
    }
}
