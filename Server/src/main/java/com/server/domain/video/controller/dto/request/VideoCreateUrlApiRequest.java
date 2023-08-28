package com.server.domain.video.controller.dto.request;

import com.server.domain.video.service.dto.request.VideoCreateUrlServiceRequest;
import com.server.module.s3.service.dto.ImageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class VideoCreateUrlApiRequest {

    private ImageType imageType;
    private String fileName;

    public VideoCreateUrlServiceRequest toServiceRequest() {
        return VideoCreateUrlServiceRequest.builder()
                .imageType(imageType)
                .fileName(fileName)
                .build();
    }
}
