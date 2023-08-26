package com.server.domain.order.service.dto.response;

import com.server.domain.order.entity.Order;
import com.server.domain.video.entity.Video;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Getter
public class OrderVideoResponse {

    private Long orderId;
    private Integer totalAmount;
    private Integer rewardAmount;
    private Integer paymentAmount;
    private LocalDateTime paymentDate;
    private List<VideoResponse> videos;

    public static OrderVideoResponse of(Order order){
        return OrderVideoResponse.builder()
                .orderId(order.getOrderId())
                .totalAmount(order.getPrice() + order.getReward())
                .rewardAmount(order.getReward())
                .paymentAmount(order.getPrice())
                .paymentDate(order.getModifiedDate())
                .videos(VideoResponse.of(order.getVideos()))
                .build();
    }

    @AllArgsConstructor
    @Builder
    @Getter
    public static class VideoResponse {
        private Long videoId;
        private String videoUrl;
        private String videoName;
        private Integer price;

        public static List<VideoResponse> of(List<Video> videos){
            return videos.stream()
                    .map(video -> VideoResponse.builder()
                            .videoId(video.getVideoId())
                            .videoUrl(video.getVideoFile())
                            .videoName(video.getVideoName())
                            .price(video.getPrice())
                            .build())
                    .collect(Collectors.toList());
        }
    }

}
