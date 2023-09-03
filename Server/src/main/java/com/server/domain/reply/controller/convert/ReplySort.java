package com.server.domain.reply.controller.convert;

import com.server.global.entity.BaseEnum;
import lombok.AllArgsConstructor;


public enum ReplySort implements BaseEnum {
    CREATED_DATE("최신순", "created-date", "createdDate"),
    STAR("별점순", "star", "star"),
    STAR_SCORE("별점별", "star-score", "starScore");



    private final String description;
    private final String property ;
    private final String sort;

    ReplySort(String description, String property, String sort) {
        this.description = description;
        this.property = property;
        this.sort = sort;
    }

    @Override
    public String getName() {
        return description ;
    }

    @Override
    public String getDescription() {
        return property;
    }

    public String getSort() {
        return sort;
    }


}
