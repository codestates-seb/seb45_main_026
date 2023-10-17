package com.server.domain.member.entity;

import com.server.global.entity.BaseEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemberStatus implements BaseEnum {
    ACTIVE("활동 중"),
    BLOCKED("차단 상태");

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
