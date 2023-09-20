package com.server.global.exception.businessexception.videoexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class VideoAdminClosedException extends VideoException {

    public static final String MESSAGE = "관리자에 의해 닫혔습니다.";
    public static final String CODE = "VIDEO-403";

    public VideoAdminClosedException() {
        super(CODE, HttpStatus.FORBIDDEN, MESSAGE);
    }
}
