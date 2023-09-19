package com.server.module.s3.service.dto;

import com.server.global.entity.BaseEnum;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public enum FileType implements BaseEnum {

    PROFILE_IMAGE("profile image", false,
            (attributes) -> getImageBucketName() + attributes.get(0) + "/profile/" + attributes.get(1),
            (attributes) -> getImageCloudFrontUrl() + attributes.get(0),
            (attributes) -> getImageBucketName() + attributes.get(0)),
    THUMBNAIL("thumbnail", false,
            (attributes) -> getImageBucketName() + attributes.get(0) + "/videos/" + attributes.get(1),
            (attributes) -> getImageCloudFrontUrl() + attributes.get(0),
            (attributes) -> getImageBucketName() + attributes.get(0)),
    VIDEO("video", true,
            (attributes) -> getVideoBucketName() + attributes.get(0) + "/videos/" + attributes.get(1),
            (attributes) -> getVideoCloudFrontUrl() + attributes.get(0),
            (attributes) -> getVideoBucketName() + attributes.get(0)),
            ;

    private final String description;
    private final boolean requiredAuth;
    private final Function<List<String>, String> s3Url;
    private final Function<List<String>, String> getFullCloudFrontUrl;
    private final Function<List<String>, String> getFulls3Url;


    public String s3Location(Long memberId, String path) {
        return this.s3Url.apply(List.of(String.valueOf(memberId), path));
    }

    public String getCloudFrontFullLocation(String path) {
        return this.getFullCloudFrontUrl.apply(List.of(path));
    }

    public String s3FullLocation(String path) {
        return this.getFulls3Url.apply(List.of(path));
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
