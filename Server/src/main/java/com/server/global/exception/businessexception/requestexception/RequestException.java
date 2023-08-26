package com.server.global.exception.businessexception.requestexception;

import org.springframework.http.HttpStatus;

import com.server.global.exception.businessexception.BusinessException;

public abstract class RequestException extends BusinessException {

	protected RequestException(String errorCode, HttpStatus httpStatus, String message) {
		super(errorCode, httpStatus, message);
	}
}
