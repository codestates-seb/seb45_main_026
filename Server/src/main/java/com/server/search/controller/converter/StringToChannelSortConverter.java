package com.server.search.controller.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.server.search.controller.dto.ChannelSort;

@Component
public class StringToChannelSortConverter implements Converter<String, ChannelSort> {

    @Override
    public ChannelSort convert(String source) {
        return ChannelSort.fromUrl(source);
    }
}
