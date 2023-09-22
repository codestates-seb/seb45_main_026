package com.server.module.s3.service;

import com.server.module.s3.service.dto.FileType;
import com.server.module.s3.service.dto.ImageType;

public interface AwsService {

    String getFileUrl(String fileName, FileType fileType);

    String getUploadVideoUrl(Long memberId, String fileName);

    String getPublicUploadUrl(Long memberId, String fileName, FileType fileType, ImageType imageType);

    void deleteFile(String fileName, FileType fileType);

    boolean isExistFile(String fileName, FileType fileType);
}
