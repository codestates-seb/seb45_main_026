package com.server.global.exception.businessexception.memberexception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class MemberDisabledException extends MemberException {

    public static final String MESSAGE = "탈퇴한 회원입니다.";
    public static final String CODE = "MEMBER-403";

    public MemberDisabledException() {
        super(CODE, HttpStatus.FORBIDDEN, MESSAGE);
    }
}
