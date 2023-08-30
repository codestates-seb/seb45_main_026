package com.server.domain.video.controller.converter;

import com.server.domain.video.controller.dto.request.VideoSort;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToVideoSortConverter implements Converter<String, VideoSort> {

    @Override
    public VideoSort convert(String source) {
        return VideoSort.fromUrl(source);
    }
}
