package com.server.global.exception.businessexception.authexception;

import org.springframework.http.HttpStatus;

public class OAuthCodeRequestException extends AuthException {

    public static final String MESSAGE = "OAuth 로그인에 실패했습니다. 코드를 확인해주세요.";
    public static final String CODE = "Auth-400";

    public OAuthCodeRequestException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
