package com.server.domain.reply.controller.convert;

import com.server.global.entity.BaseEnum;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ReplySort implements BaseEnum {
    CREATED_DATE("최신순", "created-date", "createdDate"),
    STAR("별점순", "star", "star"),
    STAR_SCORE("별점별", "star-score", "star-score");

    ;


    private final String description;
    private final Integer property ;
    private final String sort;

    ReplySort(String description, String star, String sort) {
        this.description = description;
        this.property = Integer.parseInt(star);
        this.sort = sort;
    }

    @Override
    public String getName() {
        return description ;
    }

    @Override
    public String getDescription() {
        return property.toString();
    }

    public String getSort() {
        return this.sort;
    }


}
