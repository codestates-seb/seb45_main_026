package com.server.module.s3.service;

import com.server.global.exception.businessexception.s3exception.S3DeleteException;
import com.server.global.exception.businessexception.s3exception.S3FileNotVaildException;
import com.server.global.exception.businessexception.s3exception.S3KeyException;
import com.server.module.s3.service.dto.ImageType;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CustomSignerRequest;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class AwsServiceImpl implements AwsService {

    private static final String IMAGE_PATH = "images/";
    private static final String VIDEO_TYPE = "video/mp4";
    private static final String VIDEO_BUCKET_NAME = "itprometheus-videos";
    private static final String IMAGE_BUCKET_NAME = "itprometheus-images";

    CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();
    private final String VIDEO_CLOUDFRONT_URL = "https://d3ofjtp6m9wsg6.cloudfront.net/";
    private final String IMAGE_CLOUDFRONT_URL = "https://d2ouhv9pc4idoe.cloudfront.net/";

    private final String KEY_PAIR_ID = "K2LLBSJU34F9A";

    @Value("${pem.location}")
    private  String PRIVATE_KEY_PATH;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final AwsCredentialsProvider credentialsProvider;

    public AwsServiceImpl(S3Client s3Client, S3Presigner s3Presigner, AwsCredentialsProvider credentialsProvider) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.credentialsProvider = credentialsProvider;
    }

    //todo : API 를 하나로 통일하고 File 형식을 enum 으로 받아도 될 듯 -> 총 3개 (get, put, delete)
    
    @Override
    public String getImageUrl(String fileName) {

        checkValidFile(fileName);

        return getPublicImageUrl(IMAGE_PATH + fileName);
    }


    @Override
    public String getUploadImageUrl(String fileName, ImageType imageType) {

        checkValidFile(fileName);

        Duration duration = Duration.ofMinutes(10);

        URL presignedPutObjectUrl = getPresignedPutImageObjectUrl(
                        IMAGE_PATH + fileName,
                        imageType.getDescription(),
                        duration);

        return URLDecoder.decode(presignedPutObjectUrl.toString(), UTF_8);
    }

    @Override
    public void deleteImage(String fileName) {

        checkValidFile(fileName);

        deleteImageFile(IMAGE_PATH + fileName);
    }

    @Override
    @SneakyThrows
    public String getThumbnailUrl(Long memberId, String fileName) {

        checkValidFile(fileName);

        return getPublicImageUrl(memberId + "/" + fileName);
    }

    @Override
    public String getUploadThumbnailUrl(Long memberId, String fileName, ImageType imageType) {

        checkValidFile(fileName);

        Duration duration = Duration.ofMinutes(10);

        String location = memberId + "/" + fileName;

        URL presignedPutObjectUrl = getPresignedPutImageObjectUrl(
                location,
                imageType.getDescription(),
                duration);

        return URLDecoder.decode(presignedPutObjectUrl.toString(), UTF_8);
    }

    @Override
    public void deleteThumbnail(Long memberId, String fileName) {

        checkValidFile(fileName);

        deleteImageFile(memberId + "/" + fileName);
    }

    @Override
    @SneakyThrows
    public String getVideoUrl(Long memberId, String fileName) {

        checkValidFile(fileName);

        Instant tenSecondsLater = getInstantDuration(60);

        SignedUrl signedUrlWithCustomPolicy = getFileUrl(memberId + "/" + fileName, tenSecondsLater);

        return URLDecoder.decode(signedUrlWithCustomPolicy.url(), UTF_8);
    }

    @Override
    public String getUploadVideoUrl(Long memberId, String fileName) {

        checkValidFile(fileName);

        Duration duration = Duration.ofMinutes(10);

        URL presignedPutObjectUrl = getPresignedPutVideoObjectUrl(
                memberId + "/" + fileName,
                VIDEO_TYPE,
                duration);

        return URLDecoder.decode(presignedPutObjectUrl.toString(), UTF_8);
    }

    @Override
    public void deleteVideo(Long memberId, String fileName) {

        checkValidFile(fileName);

        deleteVideoFile(memberId + "/" + fileName);
    }

    private void checkValidFile(String fileName) {
        if(fileName == null) {
            throw new S3FileNotVaildException();
        }
    }

    private Instant getInstantDuration(int secondsToAdd) {
        Instant now = Instant.now();
        return now.plusSeconds(secondsToAdd);
    }

    private SignedUrl getFileUrl(String location, Instant tenSecondsLater) {
        CustomSignerRequest customSignerRequest = null;
        try {
            customSignerRequest = CustomSignerRequest.builder()
                    .resourceUrl(VIDEO_CLOUDFRONT_URL + location)
                    .expirationDate(tenSecondsLater)
                    .keyPairId(KEY_PAIR_ID)
                    .privateKey(Path.of(PRIVATE_KEY_PATH))
                    .build();
        } catch (Exception e) {
            throw new S3KeyException();
        }

        return cloudFrontUtilities.getSignedUrlWithCustomPolicy(customSignerRequest);
    }

    private String getPublicImageUrl(String filePath){
        return IMAGE_CLOUDFRONT_URL + filePath;
    }

    private URL getPresignedPutVideoObjectUrl(String fileName, String contentType, Duration duration) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(VIDEO_BUCKET_NAME)
                .key(fileName)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(duration)
                .putObjectRequest(objectRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url();
    }

    private URL getPresignedPutImageObjectUrl(String fileName, String contentType, Duration duration) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(IMAGE_BUCKET_NAME)
                .key(fileName)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(duration)
                .putObjectRequest(objectRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url();
    }

    private void deleteVideoFile(String location) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(VIDEO_BUCKET_NAME)
                .key(location)
                .build();

        DeleteObjectResponse deleteObjectResponse = s3Client.deleteObject(deleteObjectRequest);

        check204Response(deleteObjectResponse);
    }

    private void deleteImageFile(String location) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(IMAGE_BUCKET_NAME)
                .key(location)
                .build();

        DeleteObjectResponse deleteObjectResponse = s3Client.deleteObject(deleteObjectRequest);

        check204Response(deleteObjectResponse);
    }



    private void check204Response(DeleteObjectResponse deleteObjectResponse) {
        if (deleteObjectResponse.sdkHttpResponse().statusCode() != 204) {
            throw new S3DeleteException();
        }
    }
}
