package com.server.domain.report.controller.converter;

import com.server.domain.report.controller.dto.request.ReportSort;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToReportSortConverter implements Converter<String, ReportSort> {

    @Override
    public ReportSort convert(String source) {
        return ReportSort.fromUrl(source);
    }
}
