package com.server.module.s3.service;

import com.server.module.s3.service.dto.ImageType;

public interface AwsService {

    String getImageUrl(String fileName) throws Exception;

    String getUploadImageUrl(String fileName, ImageType imageType);

    void deleteImage(String fileName);

    String getThumbnailUrl(Long memberId, String fileName) throws Exception;

    String getUploadThumbnailUrl(Long memberId, String fileName, ImageType imageType);

    void deleteThumbnail(Long memberId, String fileName);

    String getVideoUrl(Long memberId, String fileName) throws Exception;

    String getUploadVideoUrl(Long memberId, String fileName);

    void deleteVideo(Long memberId, String fileName);
}
