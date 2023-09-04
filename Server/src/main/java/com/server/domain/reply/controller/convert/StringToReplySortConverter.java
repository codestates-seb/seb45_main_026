package com.server.domain.reply.controller.convert;

import com.server.domain.video.controller.dto.request.VideoSort;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToReplySortConverter implements Converter<String, ReplySort> {

    @Override
    public ReplySort convert(String source) {
        return ReplySort.fromUrl(source);
    }
}

