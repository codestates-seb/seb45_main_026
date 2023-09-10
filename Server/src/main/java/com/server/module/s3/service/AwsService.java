package com.server.module.s3.service;

import com.server.module.s3.service.dto.FileType;
import com.server.module.s3.service.dto.ImageType;

public interface AwsService {

    String getFileUrl(Long memberId, String fileName, FileType fileType);

    String getUploadVideoUrl(Long memberId, String fileName);

    String getImageUploadUrl(Long memberId, String fileName, FileType fileType, ImageType imageType);

    void deleteFile(Long memberId, String fileName, FileType fileType);

    boolean isExistFile(Long memberId, String fileName, FileType fileType);
}
