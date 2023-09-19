package com.server.global.exception.businessexception.videoexception;

import com.server.global.exception.businessexception.orderexception.OrderException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class VideoFileNameNotMatchException extends VideoException {

    public static final String MESSAGE = "요청한 FileName 과 일치하지 않습니다.";
    public static final String CODE = "VIDEO-400";

    public VideoFileNameNotMatchException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
