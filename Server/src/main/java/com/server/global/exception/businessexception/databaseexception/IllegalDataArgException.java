package com.server.global.exception.businessexception.databaseexception;

import org.springframework.http.HttpStatus;

public class IllegalDataArgException extends DatabaseException {
	private static final String CODE = "DB-500";
	private static final String MESSAGE = "잘못된 인수 혹은 삭제할 엔티티가 올바르지 않습니다.";

	public IllegalDataArgException() {
		super(CODE, HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE);
	}
}