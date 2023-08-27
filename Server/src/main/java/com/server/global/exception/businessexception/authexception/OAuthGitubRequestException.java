package com.server.global.exception.businessexception.authexception;

import org.springframework.http.HttpStatus;

public class OAuthGitubRequestException extends AuthException {

    public static final String MESSAGE = "깃허브 이메일을 받아오는데 실패했습니다. 다시 시도해주세요.";
    public static final String CODE = "Auth-400";

    public OAuthGitubRequestException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
