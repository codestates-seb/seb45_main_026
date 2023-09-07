package com.server.global.exception.businessexception.databaseexception;

import org.springframework.http.HttpStatus;

import com.server.global.exception.businessexception.mailexception.MailException;

public class EntityNotFoundException extends DatabaseException {
	private static final String CODE = "DB-500";
	private static final String MESSAGE = "존재하지 않는 엔티티입니다.";

	public EntityNotFoundException() {
		super(CODE, HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE);
	}
}
