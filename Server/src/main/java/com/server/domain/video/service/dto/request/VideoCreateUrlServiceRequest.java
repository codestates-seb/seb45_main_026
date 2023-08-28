package com.server.domain.video.service.dto.request;

import com.server.module.s3.service.dto.ImageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class VideoCreateUrlServiceRequest {

    private ImageType imageType;
    private String fileName;
}
