package com.server.global.exception.businessexception.databaseexception;

import org.springframework.http.HttpStatus;

public class DataAccessFailedException extends DatabaseException {
	private static final String CODE = "DB-500";
	private static final String MESSAGE = "데이터베이스와의 연결 실패 혹은 제약 조건 위반이 발생했습니다.";

	public DataAccessFailedException() {
		super(CODE, HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE);
	}
}