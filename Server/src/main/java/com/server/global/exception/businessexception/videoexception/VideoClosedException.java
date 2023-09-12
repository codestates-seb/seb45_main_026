package com.server.global.exception.businessexception.videoexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class VideoClosedException extends VideoException {

    public static final String MESSAGE = "강의가 폐쇄되었습니다. 강의명 : ";
    public static final String CODE = "VIDEO-404";

    public VideoClosedException(String videoName) {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE + videoName);
    }
}
