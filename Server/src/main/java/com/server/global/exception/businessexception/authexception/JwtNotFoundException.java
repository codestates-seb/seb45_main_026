package com.server.global.exception.businessexception.authexception;

import org.springframework.http.HttpStatus;

public class JwtNotFoundException extends AuthException {
	private static final String CODE = "JWT-401";
	private static final String MESSAGE = "토큰이 존재하지 않습니다.";

	public JwtNotFoundException() {
		super(CODE, HttpStatus.UNAUTHORIZED, MESSAGE);
	}
}