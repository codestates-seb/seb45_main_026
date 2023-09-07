package com.server.global.exception.businessexception.databaseexception;

import org.springframework.http.HttpStatus;

public class TxSystemException extends DatabaseException {
	private static final String CODE = "DB-500";
	private static final String MESSAGE = "트랜잭션의 시작 혹은 커밋 등의 상황에 오류가 발생했습니다.";

	public TxSystemException() {
		super(CODE, HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE);
	}
}
