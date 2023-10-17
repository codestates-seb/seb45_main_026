package com.server.chat.entity;

import com.server.global.entity.BaseEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MessageType implements BaseEnum {
    TALK("대화"),
    QUIT("퇴장");

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
