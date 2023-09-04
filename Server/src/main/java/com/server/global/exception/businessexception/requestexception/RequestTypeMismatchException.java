package com.server.global.exception.businessexception.requestexception;

import org.springframework.http.HttpStatus;

public class RequestTypeMismatchException extends RequestException {

    public static final String MESSAGE = "요청 값의 타입이 잘못되었습니다.";
    public static final String CODE = "REQUEST-400";

    public RequestTypeMismatchException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }

    public RequestTypeMismatchException(String failedValue) {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE + " 잘못된 값 : " + failedValue);
    }
}
