package com.server.global.exception.businessexception.videoexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class VideoAlreadyCreatedException extends VideoException {

    public static final String MESSAGE = "이미 생성된 VIDEO 입니다.";
    public static final String CODE = "VIDEO-400";

    public VideoAlreadyCreatedException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
