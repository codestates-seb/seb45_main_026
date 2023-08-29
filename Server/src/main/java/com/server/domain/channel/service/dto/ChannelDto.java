package com.server.domain.channel.service.dto;

import com.server.domain.category.entity.Category;
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
        private boolean isSubscribed;
        private String description;
        private String imageUrl;
        private LocalDateTime createdDate;
    }

    public static class Category {
        private Long categoryId;
        private String categoryName;
    }

    @Getter
    @Setter
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
        private List<Category> categories;
        private LocalDate createdDate;
    }
}

