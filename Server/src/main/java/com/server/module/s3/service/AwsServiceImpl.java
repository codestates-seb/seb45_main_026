package com.server.module.s3.service;

import com.server.global.exception.businessexception.s3exception.S3DeleteException;
import com.server.module.s3.service.dto.ImageType;
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
    private static final String THUMBNAIL_PATH = "thumbnails/";
    private static final String VIDEO_TYPE = "video/mp4";
    private static final String BUCKET_NAME = "itprometheus-videos";

    CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();
    private final String CLOUDFRONT_URL = "http://d3ofjtp6m9wsg6.cloudfront.net/";
    private final String KEY_PAIR_ID = "K2LLBSJU34F9A";
    private final String PRIVATE_KEY_PATH = "src/main/resources/prometheus.pem";

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final AwsCredentialsProvider credentialsProvider;

    public AwsServiceImpl(S3Client s3Client, S3Presigner s3Presigner, AwsCredentialsProvider credentialsProvider) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.credentialsProvider = credentialsProvider;
    }
    
    @Override
    public String getImageUrl(String fileName) throws Exception {
        
        Instant tenSecondsLater = getInstantDuration(60);

        SignedUrl signedUrlWithCustomPolicy = getFileUrl(IMAGE_PATH + fileName, tenSecondsLater);

        return URLDecoder.decode(signedUrlWithCustomPolicy.url(), UTF_8);
    }


    @Override
    public String getUploadImageUrl(String fileName, ImageType imageType) {
        
        Duration duration = Duration.ofMinutes(10);

        URL presignedPutObjectUrl = getPresignedPutObjectUrl(
                        IMAGE_PATH + fileName, 
                        imageType.getDescription(), 
                        duration);

        return URLDecoder.decode(presignedPutObjectUrl.toString(), UTF_8);
    }

    @Override
    public void deleteImage(String fileName) {

        deleteFile(IMAGE_PATH + fileName);
    }

    @Override
    public String getThumbnailUrl(Long memberId, String fileName) throws Exception {

        Instant tenSecondsLater = getInstantDuration(60);

        String location = memberId + "/" + THUMBNAIL_PATH + fileName;

        SignedUrl signedUrlWithCustomPolicy = getFileUrl(location, tenSecondsLater);

        return URLDecoder.decode(signedUrlWithCustomPolicy.url(), UTF_8);
    }

    @Override
    public String getUploadThumbnailUrl(Long memberId, String fileName, ImageType imageType) {

        Duration duration = Duration.ofMinutes(10);

        String location = memberId + "/" + THUMBNAIL_PATH + fileName;

        URL presignedPutObjectUrl = getPresignedPutObjectUrl(
                location,
                imageType.getDescription(),
                duration);

        return URLDecoder.decode(presignedPutObjectUrl.toString(), UTF_8);
    }

    @Override
    public void deleteThumbnail(Long memberId, String fileName) {

        deleteFile(memberId + "/" + THUMBNAIL_PATH + fileName);
    }

    @Override
    public String getVideoUrl(Long memberId, String fileName) throws Exception {

        Instant tenSecondsLater = getInstantDuration(60);
        
        SignedUrl signedUrlWithCustomPolicy = getFileUrl(memberId + "/" + fileName, tenSecondsLater);
        
        return URLDecoder.decode(signedUrlWithCustomPolicy.url(), UTF_8);
    }

    @Override
    public String getUploadVideoUrl(Long memberId, String fileName) {

        Duration duration = Duration.ofMinutes(10);

        URL presignedPutObjectUrl = getPresignedPutObjectUrl(
                memberId + "/" + fileName,
                VIDEO_TYPE,
                duration);

        return URLDecoder.decode(presignedPutObjectUrl.toString(), UTF_8);
    }

    @Override
    public void deleteVideo(Long memberId, String fileName) {

        deleteFile(memberId + "/" + fileName);
    }

    private Instant getInstantDuration(int secondsToAdd) {
        Instant now = Instant.now();
        return now.plusSeconds(secondsToAdd);
    }

    private SignedUrl getFileUrl(String location, Instant tenSecondsLater) throws Exception {
        CustomSignerRequest customSignerRequest = CustomSignerRequest.builder()
                .resourceUrl(CLOUDFRONT_URL + location)
                .expirationDate(tenSecondsLater)
                .keyPairId(KEY_PAIR_ID)
                .privateKey(Path.of(PRIVATE_KEY_PATH))
                .build();

        return cloudFrontUtilities.getSignedUrlWithCustomPolicy(customSignerRequest);
    }

    private URL getPresignedPutObjectUrl(String fileName, String contentType, Duration duration) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(fileName)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(duration)
                .putObjectRequest(objectRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url();
    }

    private void deleteFile(String location) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(BUCKET_NAME)
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
