package com.server.global.exception.businessexception.questionexception;

import com.server.global.exception.businessexception.memberexception.MemberException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class QuestionNotFoundException extends MemberException {

    public static final String MESSAGE = "존재하지 않는 질문입니다.";
    public static final String CODE = "QUESTION-404";

    public QuestionNotFoundException() {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE);
    }
}
