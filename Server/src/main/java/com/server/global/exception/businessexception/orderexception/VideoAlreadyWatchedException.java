package com.server.global.exception.businessexception.orderexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class VideoAlreadyWatchedException extends OrderException {

    public static final String MESSAGE = "이미 비디오를 시청하여 취소할 수 없습니다.";
    public static final String CODE = "ORDER-404";

    public VideoAlreadyWatchedException() {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE);
    }
}
