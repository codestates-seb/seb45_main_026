package com.server.global.exception.businessexception.databaseexception;

import org.springframework.http.HttpStatus;

public class UnknownDatabaseException extends DatabaseException {
	private static final String CODE = "DB-500";
	private static final String MESSAGE = "알 수 없는 데이터베이스 오류입니다.";

	public UnknownDatabaseException() {
		super(CODE, HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE);
	}
}