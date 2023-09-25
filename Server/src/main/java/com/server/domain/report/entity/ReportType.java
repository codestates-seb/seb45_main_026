package com.server.domain.report.entity;


import com.server.global.entity.BaseEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReportType implements BaseEnum {
    VIDEO("비디오 신고"),
    REPLY("댓글 신고"),
    CHANEL("채널 신고"),
    ANNOUNCEMENT("공지사항 신고");

    private final String description;

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
