package com.server.global.exception.businessexception.authexception;

import org.springframework.http.HttpStatus;

public class LoginPasswordNullException extends AuthException {
	private static final String CODE = "LOGIN-400";
	private static final String MESSAGE = "패스워드를 입력해주세요.";

	public LoginPasswordNullException() {
		super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
	}
}
