package com.server.global.exception.businessexception.databaseexception;

import org.springframework.http.HttpStatus;

import com.server.global.exception.businessexception.BusinessException;

public abstract class DatabaseException extends BusinessException {

	protected DatabaseException(String errorCode, HttpStatus httpStatus, String message) {
		super(errorCode, httpStatus, message);
	}
}
