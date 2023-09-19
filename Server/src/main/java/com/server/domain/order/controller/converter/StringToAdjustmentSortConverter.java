package com.server.domain.order.controller.converter;

import com.server.domain.order.controller.dto.request.AdjustmentSort;
import com.server.domain.video.controller.dto.request.VideoSort;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToAdjustmentSortConverter implements Converter<String, AdjustmentSort> {

    @Override
    public AdjustmentSort convert(String source) {
        return AdjustmentSort.fromUrl(source);
    }
}
