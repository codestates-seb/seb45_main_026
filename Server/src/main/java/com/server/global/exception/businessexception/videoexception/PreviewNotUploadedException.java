package com.server.global.exception.businessexception.videoexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PreviewNotUploadedException extends VideoException {

    public static final String MESSAGE = "미리보기가 업로드되지 않았습니다. 강의명 : ";
    public static final String CODE = "VIDEO-404";

    public PreviewNotUploadedException(String videoName) {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE + videoName);
    }
}
