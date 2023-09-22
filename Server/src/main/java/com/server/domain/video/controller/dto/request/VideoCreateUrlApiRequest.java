package com.server.domain.video.controller.dto.request;

import com.server.domain.video.service.dto.request.VideoCreateUrlServiceRequest;
import com.server.module.s3.service.dto.ImageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@Builder
public class VideoCreateUrlApiRequest {

    @NotNull(message = "{validation.video.imageType}")
    private ImageType imageType;
    @NotBlank(message = "{validation.video.name}")
    private String fileName;


    public VideoCreateUrlServiceRequest toServiceRequest() {
        return VideoCreateUrlServiceRequest.builder()
                .imageType(imageType)
                .fileName(fileName)
                .build();
    }
}
