package com.server.global.exception.businessexception.memberexception;

import org.springframework.http.HttpStatus;

public class MemberPasswordException extends MemberException {

    public static final String MESSAGE = "비밀번호를 확인해주세요.";
    public static final String CODE = "MEMBER-400";

    public MemberPasswordException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
