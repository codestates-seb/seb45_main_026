package com.server.domain.channel.service.dto;

import com.server.domain.announcement.service.dto.request.AnnouncementCreateServiceRequest;
import com.server.domain.category.entity.Category;
import com.server.domain.channel.entity.Channel;
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
        private String description;
        private String imageUrl;
        private LocalDateTime createdDate;

        public static ChannelInfo of(Channel channel,
                                     String channelName,
                                     int subscribers,
                                     boolean isSubscribed,
                                     String description,
                                     String imageUrl,
                                     LocalDateTime createdDate) {

            return ChannelInfo.builder()
                    .channelName(channel.getChannelName())
                    .subscribers(subscribers)
                    .isSubscribed(isSubscribed)
                    .description(channel.getDescription())
                    .createdDate(channel.getCreatedDate())
                    .build();
        }
    }


    @Builder
    @Getter
    public static class UpdateInfo {
        private String channelName;
        private String description;
    }


    @AllArgsConstructor
    @Builder
    @Getter
    public static class ChannelResponseDto{
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

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateAnnouncementApiRequest {
        private String content;

        public AnnouncementCreateServiceRequest toServiceRequest(Long memberId) {
            return AnnouncementCreateServiceRequest.builder()
                    .memberId(memberId)
                    .content(content)
                    .build();
        }
    }
}

