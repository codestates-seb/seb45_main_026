package com.server.global.exception.businessexception.memberexception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class MemberNotFoundException extends MemberException {

    public static final String MESSAGE = "존재하지 않는 회원입니다.";
    public static final String CODE = "MEMBER-401";

    public MemberNotFoundException() {
        super(CODE, HttpStatus.UNAUTHORIZED, MESSAGE);
    }
}
