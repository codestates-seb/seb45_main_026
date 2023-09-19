package com.server.global.exception.businessexception.authexception;

import org.springframework.http.HttpStatus;

public class LoginEmailValidException extends AuthException {
	private static final String CODE = "LOGIN-400";
	private static final String MESSAGE = "이메일 형식을 맞춰주세요. (example@email.com)";

	public LoginEmailValidException() {
		super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
	}
}
