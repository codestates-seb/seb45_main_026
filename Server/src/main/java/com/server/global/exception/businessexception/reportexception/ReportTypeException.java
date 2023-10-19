package com.server.global.exception.businessexception.reportexception;

import com.server.global.exception.businessexception.videoexception.VideoException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ReportTypeException extends ReportException {

    public static final String MESSAGE = "Report 타입이 잘못되었습니다.";
    public static final String CODE = "REPORT-404";

    public ReportTypeException() {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE);
    }
}
