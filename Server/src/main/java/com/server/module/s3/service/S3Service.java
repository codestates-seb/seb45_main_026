package com.server.module.s3.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    String getImageUrl(String fileName);

    String uploadImageAndGetUrl(String fileName, MultipartFile file);

    void deleteImage(String fileName);

    String getVideoUrl(String fileName);

    String uploadVideoAndGetUrl(String fileName, MultipartFile file);

    void deleteVideo(String fileName);
}
