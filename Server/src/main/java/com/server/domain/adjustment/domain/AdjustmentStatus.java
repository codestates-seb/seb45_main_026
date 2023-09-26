package com.server.domain.adjustment.domain;

import com.server.global.entity.BaseEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AdjustmentStatus implements BaseEnum {
    TOTAL("전체 결과 조회"),
    NO_ADJUSTMENT("정산 미진행"),
    NOT_ADJUSTED("정산 전"),
    ADJUSTING("정산중"),
    ADJUSTED("정산완료"),
    FAILED("정산실패");

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
