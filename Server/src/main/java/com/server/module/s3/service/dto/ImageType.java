package com.server.module.s3.service.dto;

import com.server.global.entity.BaseEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ImageType implements BaseEnum {

    JPG("image/jpg"),
    JPEG("image/jpeg"),
    PNG("image/png")
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
