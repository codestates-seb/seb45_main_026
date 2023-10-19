package com.server.domain.report.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ReportCreateApiRequest {

    @NotBlank(message = "{validation.report.content}")
    private String reportContent;
}
