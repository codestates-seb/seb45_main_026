package com.server.global.exception.businessexception.videoexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class VideoNameDuplicateException extends VideoException {

    public static final String MESSAGE = "채널 내 강의명이 중복됩니다.";
    public static final String CODE = "VIDEO-409";

    public VideoNameDuplicateException() {
        super(CODE, HttpStatus.CONFLICT, MESSAGE);
    }
}
