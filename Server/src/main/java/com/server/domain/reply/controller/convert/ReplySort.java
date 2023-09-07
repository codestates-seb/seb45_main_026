package com.server.domain.reply.controller.convert;

import com.server.domain.video.controller.dto.request.VideoSort;
import com.server.global.entity.BaseEnum;
import lombok.AllArgsConstructor;
import org.springframework.beans.TypeMismatchException;

@AllArgsConstructor
public enum ReplySort implements BaseEnum {
    CREATED_DATE("최신순", "created-date", "createdDate"),
    STAR("별점순", "star", "star"),

    ;

    private final String description;
    private final String url;
    private final String sort;

    @Override
    public String getName() {
        return this.url;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public String getSort() {
        return this.sort;
    }

    public static ReplySort fromUrl(String url) {
        for (ReplySort replySort : values()) {
            if (replySort.url.equals(url)) {
                return replySort;
            }
        }
        throw new TypeMismatchException(url, ReplySort.class);
    }


}
