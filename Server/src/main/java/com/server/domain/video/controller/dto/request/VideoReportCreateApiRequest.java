package com.server.domain.video.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class VideoReportCreateApiRequest {

    @NotBlank(message = "{validation.report.content}")
    private String reportContent;
}
