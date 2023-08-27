package com.server.global.exception.businessexception.answerexception;

import com.server.global.exception.businessexception.memberexception.MemberException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AnswerCountException extends MemberException {

    public static final String MESSAGE = "답변 개수와 문제의 개수가 다릅니다.";
    public static final String CODE = "ANSWER-400";

    public AnswerCountException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
