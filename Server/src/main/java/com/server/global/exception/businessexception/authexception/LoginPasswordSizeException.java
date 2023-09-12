package com.server.global.exception.businessexception.authexception;

import org.springframework.http.HttpStatus;

public class LoginPasswordSizeException extends AuthException {
	private static final String CODE = "LOGIN-400";
	private static final String MESSAGE = "패스워드의 길이는 최소 9자 최대 20자를 만족해야합니다.";

	public LoginPasswordSizeException() {
		super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
	}
}