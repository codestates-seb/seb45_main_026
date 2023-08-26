package com.server.global.exception.businessexception.mailexception;

import org.springframework.http.HttpStatus;

public class MailCertificationException extends MailException{
	private static final String CODE = "MAIL-408";
	private static final String MESSAGE = "이메일 인증에 실패했습니다.";

	public MailCertificationException() {
		super(CODE, HttpStatus.UNAUTHORIZED, MESSAGE);
	}
}
