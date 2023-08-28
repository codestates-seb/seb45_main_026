package com.server.global.exception.businessexception.s3exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class S3KeyException extends S3Exception{

    public static final String MESSAGE = "파일에 접근할 수 없습니다. 다시 시도해주세요.";
    public static final String CODE = "AWS-500";

    public S3KeyException() {
        super(CODE, HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE);
    }
}
