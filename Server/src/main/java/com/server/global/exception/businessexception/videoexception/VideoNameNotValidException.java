package com.server.global.exception.businessexception.videoexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class VideoNameNotValidException extends VideoException {

    public static final String MESSAGE = "해당 문자는 비디오 명으로 사용할 수 없습니다. : %s";
    public static final String CODE = "VIDEO-400";

    public VideoNameNotValidException(String character) {
        super(CODE, HttpStatus.CONFLICT, String.format(MESSAGE, character));
    }
}
