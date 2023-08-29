package com.server.global.exception.businessexception.videoexception;

import com.server.global.exception.businessexception.orderexception.OrderException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class VideoAccessDeniedException extends VideoException {

    public static final String MESSAGE = "강의를 접근 권한이 없습니다.";
    public static final String CODE = "VIDEO-403";

    public VideoAccessDeniedException() {
        super(CODE, HttpStatus.FORBIDDEN, MESSAGE);
    }
}
