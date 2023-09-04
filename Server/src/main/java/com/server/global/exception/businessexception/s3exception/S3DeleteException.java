package com.server.global.exception.businessexception.s3exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class S3DeleteException extends S3Exception{

    public static final String MESSAGE = "삭제에 실패했습니다.";
    public static final String CODE = "AWS-400";

    public S3DeleteException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
