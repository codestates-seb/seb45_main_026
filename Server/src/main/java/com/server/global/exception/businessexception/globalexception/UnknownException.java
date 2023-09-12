package com.server.global.exception.businessexception.globalexception;

import org.springframework.http.HttpStatus;

public class UnknownException extends GlobalException {
	private static final String CODE = "ERROR-500";
	private static final String MESSAGE = "알 수 없는 오류입니다. 다시 시도해주세요.";

	public UnknownException() {
		super(CODE, HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE);
	}
}
