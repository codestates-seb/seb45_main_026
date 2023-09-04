package com.server.global.exception.businessexception.mailexception;

import org.springframework.http.HttpStatus;

import com.server.global.exception.businessexception.BusinessException;

public abstract class MailException extends BusinessException {

	protected MailException(String errorCode, HttpStatus httpStatus, String message) {
		super(errorCode, httpStatus, message);
	}
}
