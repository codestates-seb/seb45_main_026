package com.server.global.exception.businessexception.videoexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class VideoNotUploadedException extends VideoException {

    public static final String MESSAGE = "강의 파일이 업로드되지 않았습니다. 강의명 : ";
    public static final String CODE = "VIDEO-404";

    public VideoNotUploadedException(String videoName) {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE + videoName);
    }
}
