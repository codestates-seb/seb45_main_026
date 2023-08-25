package com.server.module.s3.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class S3ServiceImpl implements S3Service{

    @Override
    public String getImageUrl(String fileName) {
        return null;
    }

    @Override
    public String uploadImageAndGetUrl(String fileName, MultipartFile file) {
        return null;
    }

    @Override
    public void deleteImage(String fileName) {

    }

    @Override
    public String getVideoUrl(String fileName) {
        return null;
    }

    @Override
    public String uploadVideoAndGetUrl(String fileName, MultipartFile file) {
        return null;
    }

    @Override
    public void deleteVideo(String fileName) {

    }
}
