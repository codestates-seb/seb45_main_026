package com.server.domain.channel.service.dto;

import com.server.domain.category.entity.Category;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public class ChannelDto {
    @Builder
    @Getter
    public static class ChannelInfo {
        private Long memberId;
        private String channelName;
        private int subscribers;
        private boolean isSubscribed; //구독여부 값 채워넣기
        private boolean isPurchaseVideos;
        private String description;
        private String imageUrl; //aws 서비스에서
        private LocalDateTime createdDate;
    }

    @Builder
    @Getter
    public static class Category {
        private Long categoryId;
        private String categoryName;
    }

    @Builder
    @Getter
    public static class UpdateInfo {
        private String channelName;
        private String description;
    }

    @Builder
    @Getter
    public static class ChannelVideoResponseDto {
        private Long videoId;
        private Long categoryId;
        private String categoryName;
        private String videoName;
        private String thumbnailUrl;
        private int views;
        private int price;
        private List<Category> categories;
        private LocalDate createdDate;
    }

    @Builder
    @Getter
    public static class ChannelVideoListResponseDto {
        private Long categoryId;
        private String categoryName;
        private Long videoId;
        private String videoName;
        private String thumbnailUrl;
        private int views;
        private int price;
        private List<Category> categories;
        private LocalDate createdDate;
    }
}

