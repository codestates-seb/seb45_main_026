package com.server.global.exception.businessexception.authexception;

import org.springframework.http.HttpStatus;

public class JwtExpiredException extends AuthException {
	private static final String CODE = "JWT-401";
	private static final String MESSAGE = "만료된 토큰입니다.";

	public JwtExpiredException() {
		super(CODE, HttpStatus.UNAUTHORIZED, MESSAGE);
	}
}
