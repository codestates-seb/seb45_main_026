package com.server.module.s3.service.dto;

import com.server.global.entity.BaseEnum;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public enum FileType implements BaseEnum {

    PROFILE_IMAGE("profile image", false,
            (attributes) -> getImageCloudFrontUrl() + attributes.get(0) + "/profile/" + attributes.get(1),
            (attributes) -> getImageBucketName() + attributes.get(0) + "/profile/" + attributes.get(1)),
    THUMBNAIL("thumbnail", false,
            (attributes) -> getImageCloudFrontUrl() + attributes.get(0) + "/videos/" + attributes.get(1),
            (attributes) -> getImageBucketName() + attributes.get(0) + "/videos/" + attributes.get(1)),
    VIDEO("video", true,
            (attributes) -> getVideoCloudFrontUrl() + attributes.get(0) + "/videos/" + attributes.get(1),
            (attributes) -> getVideoBucketName() + attributes.get(0) + "/videos/" + attributes.get(1)),
            ;

    private final String description;
    private final boolean requiredAuth;
    private final Function<List<String>, String> cloudFrontUrl;
    private final Function<List<String>, String> s3Url;

    public String getLocation(Long memberId, String path) {
        return this.cloudFrontUrl.apply(List.of(String.valueOf(memberId), path));
    }

    public String s3Location(Long memberId, String path) {
        return this.s3Url.apply(List.of(String.valueOf(memberId), path));
    }

    private static String getImageCloudFrontUrl(){
        return "https://d2ouhv9pc4idoe.cloudfront.net/";
    }

    private static String getVideoCloudFrontUrl(){
        return "https://d3ofjtp6m9wsg6.cloudfront.net/";
    }

    private static String getImageBucketName(){
        return "itprometheus-images/";
    }

    private static String getVideoBucketName(){
        return "itprometheus-videos/";
    }

    @Override
    public String getName() {
        return name();
    }

    public boolean isRequiredAuth() {
        return requiredAuth;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
