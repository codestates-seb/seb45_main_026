package com.server.global.exception.businessexception.s3exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class S3FileNotVaildException extends S3Exception{

    public static final String MESSAGE = "파일이 유효하지 않습니다.";
    public static final String CODE = "AWS-400";

    public S3FileNotVaildException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
