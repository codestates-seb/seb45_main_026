package com.server.domain.channel.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ChannelDto {
    @Getter
    @Setter
    public static class ChannelInfo {
        private Long memberId;
        private String channelName;
        private int subscribers;
        private String description;
        private String imageUrl;
        private LocalDateTime createdDate;
    }

    public static class SubscribeStatus {
        private boolean data;
    }

    public static class VideoResponse {
        private Long videoId;
        private String videoName;
        private String thumbnailUrl;
        private int views;
        private int price;
        private List<String> categories;
        private LocalDate createdDate;
    }

    public static class CategoryInfo {
        private Long categoryId;
        private String categoryName;
    }

    @Getter
    public static class UpdateInfo {
        private String channelName;
        private String description;
    }

    @Getter
    @Setter
    public static class ChannelVideoResponseDto {
        private Long videoId;
        private String videoName;
        private String thumbnailUrl;
        private int views;
        private int price;
        private List<String> categories;
        private LocalDate createdDate;
    }
}

