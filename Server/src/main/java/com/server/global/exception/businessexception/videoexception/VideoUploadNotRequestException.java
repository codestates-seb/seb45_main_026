package com.server.global.exception.businessexception.videoexception;

import com.server.global.exception.businessexception.orderexception.OrderException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class VideoUploadNotRequestException extends VideoException {

    public static final String MESSAGE = "VIDEO 업로드 요청을 먼저 해주세요.";
    public static final String CODE = "VIDEO-400";

    public VideoUploadNotRequestException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
