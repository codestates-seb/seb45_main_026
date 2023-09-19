package com.server.module.s3.service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.server.auth.oauth.service.OAuthProvider;
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
    @JsonValue
    public String getName() {
        return name();
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @JsonCreator
    public static ImageType from(String value) {
        for (ImageType imageType : ImageType.values()) {
            if (imageType.getName().equalsIgnoreCase(value)) {
                return imageType;
            }
        }
        return null;
    }
}
