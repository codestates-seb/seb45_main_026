package com.server.global.exception.businessexception.authexception;

import org.springframework.http.HttpStatus;

public class JwtNotValidException extends AuthException {
	private static final String CODE = "JWT-401";
	private static final String MESSAGE = "잘못된 토큰입니다.";

	public JwtNotValidException() {
		super(CODE, HttpStatus.UNAUTHORIZED, MESSAGE);
	}
}
