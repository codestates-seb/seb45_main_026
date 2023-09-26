package com.server.domain.report.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class MemberBlockServiceRequest {

    private Integer days;
    private String blockReason;
}
