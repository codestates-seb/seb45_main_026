package com.server.domain.video.entity;

import com.server.global.entity.BaseEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum VideoStatus implements BaseEnum {
    UPLOADING("업로드중"),
    CREATED("생성됨"),
    CLOSED("폐쇄됨"),
    ADMIN_CLOSED("관리자에 의해 폐쇄됨")
    ;

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
