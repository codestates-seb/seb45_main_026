package com.server.global.exception.businessexception.mailexception;

import org.springframework.http.HttpStatus;

public class MailSendException extends MailException {
	private static final String CODE = "MAIL-400";
	private static final String MESSAGE = "이메일 전송에 실패했습니다.";

	public MailSendException() {
		super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
	}
}
