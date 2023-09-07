package com.server.global.exception.businessexception.databaseexception;

import org.springframework.http.HttpStatus;

public class TxRequiredException extends DatabaseException {
	private static final String CODE = "DB-500";
	private static final String MESSAGE = "트랜잭션이 필요한 상황이지만 적용되지 않았습니다.";

	public TxRequiredException() {
		super(CODE, HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE);
	}
}