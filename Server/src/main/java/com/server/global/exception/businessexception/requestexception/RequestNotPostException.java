package com.server.global.exception.businessexception.requestexception;

import org.springframework.http.HttpStatus;

public class RequestNotPostException extends RequestException {

	private static final String CODE = "LOGIN-401";
	private static final String MESSAGE = "로그인 요청은 POST 메서드로만 가능합니다.";

	public RequestNotPostException() {
		super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
	}
}
