package com.server.domain.report.controller.dto.request;

import com.server.global.entity.BaseEnum;
import lombok.AllArgsConstructor;
import org.springframework.beans.TypeMismatchException;

@AllArgsConstructor
public enum ReportSort implements BaseEnum {
    LAST_REPORTED_DATE("최근 신고 순", "last-reported-date", "lastReportedDate"),
    CREATED_DATE("생성 순", "created-date", "createdDate"),
    REPORT_COUNT("신고 많은 순", "report-count", "reportCount"),
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

    public static ReportSort fromUrl(String url) {
        for (ReportSort videoReportSort : values()) {
            if (videoReportSort.url.equals(url)) {
                return videoReportSort;
            }
        }
        throw new TypeMismatchException(url, ReportSort.class);
    }
}
