package com.server.global.exception.businessexception.authexception;

import org.springframework.http.HttpStatus;

public class LoginPasswordValidException extends AuthException {
	private static final String CODE = "LOGIN-400";
	private static final String MESSAGE = "문자, 숫자, 특수문자로 이루어진 9~20자를 입력하세요.";

	public LoginPasswordValidException() {
		super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
	}
}
