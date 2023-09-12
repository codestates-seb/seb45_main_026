package com.server.global.exception.businessexception.databaseexception;

import org.springframework.http.HttpStatus;

public class DataConstraintViolationException extends DatabaseException {
	private static final String CODE = "DB-500";
	private static final String MESSAGE = "제약 조건을 위반하였습니다.(예: 외래키 제약 조건)";

	public DataConstraintViolationException() {
		super(CODE, HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE);
	}
}