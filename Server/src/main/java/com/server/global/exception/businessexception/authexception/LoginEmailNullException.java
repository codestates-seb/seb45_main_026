package com.server.global.exception.businessexception.authexception;

import org.springframework.http.HttpStatus;

public class LoginEmailNullException extends AuthException {
	private static final String CODE = "LOGIN-400";
	private static final String MESSAGE = "이메일을 입력해주세요.";

	public LoginEmailNullException() {
		super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
	}
}
