package com.server.global.exception.businessexception.videoexception;

import com.server.global.exception.businessexception.orderexception.OrderException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class VideoNotFoundException extends OrderException {

    public static final String MESSAGE = "강의를 찾을 수 없습니다.";
    public static final String CODE = "VIDEO-404";

    public VideoNotFoundException() {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE);
    }
}
