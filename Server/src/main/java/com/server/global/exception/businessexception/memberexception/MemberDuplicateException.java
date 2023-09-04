package com.server.global.exception.businessexception.memberexception;

import org.springframework.http.HttpStatus;

public class MemberDuplicateException extends MemberException{

    public static final String MESSAGE = "이미 존재하는 회원 이메일입니다.";
    public static final String CODE = "MEMBER-409";

    public MemberDuplicateException() {
        super(CODE, HttpStatus.CONFLICT, MESSAGE);
    }
}
