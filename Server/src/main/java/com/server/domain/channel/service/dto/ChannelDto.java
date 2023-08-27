package com.server.domain.channel.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ChannelDto {
    @Getter
    @Setter //코드가 너무 길어질 것 같아서 setter로 임시대체 -> 확인 후 수정 계획
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChannelInfo{
        private Long memberId;
        private String channelName;
        private int subscribes;
        private boolean isSubscribed;
        private String description;
        private String imageUrl;
        private LocalDateTime createdDate;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubscribeStatus{
        private boolean data;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class VideoResponse{
        private Long videoId;
        private String videoName;
        private String thumbnailUrl;
        private int views;
        private int price;
        private List<String> categories;
        private LocalDate createdDate;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryInfo{
        private Long categoryId;
        private String categoryName;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class UpdateInfo {
        private String channelName;
        private String description;
    }
}

