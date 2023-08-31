package com.server.global.exception.businessexception.announcementexception;

import org.springframework.http.HttpStatus;

public class AnnouncementNotFoundException extends AnnouncementException {
    private static final String CODE = "CHANNEL-404";
    private static final String MESSAGE = "존재하지않는 공지사항입니다.";

    public AnnouncementNotFoundException() {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE);
    }
}
