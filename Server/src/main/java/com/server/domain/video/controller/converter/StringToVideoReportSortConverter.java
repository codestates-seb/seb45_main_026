package com.server.domain.video.controller.converter;

import com.server.domain.video.controller.dto.request.VideoReportSort;
import com.server.domain.video.controller.dto.request.VideoSort;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToVideoReportSortConverter implements Converter<String, VideoReportSort> {

    @Override
    public VideoReportSort convert(String source) {
        return VideoReportSort.fromUrl(source);
    }
}
