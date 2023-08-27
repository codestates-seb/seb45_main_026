package com.server.domain.answer.entity;

import com.server.global.entity.BaseEnum;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AnswerStatus implements BaseEnum {
    CORRECT("정답"),
    WRONG("오답")
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