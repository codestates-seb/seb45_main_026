package com.server.global.exception.businessexception.databaseexception;

import org.springframework.http.HttpStatus;

public class VersionControllException extends DatabaseException {
	private static final String CODE = "DB-500";
	private static final String MESSAGE = "데이터베이스의 버전 관리가 활성화 되어 다른 트랜잭션과의 충돌이 발생했습니다.";

	public VersionControllException() {
		super(CODE, HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE);
	}
}