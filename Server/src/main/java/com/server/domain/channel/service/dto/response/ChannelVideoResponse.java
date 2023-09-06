package com.server.domain.channel.service.dto.response;

import com.server.domain.video.entity.Video;
import com.server.domain.video.service.dto.response.VideoCategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class ChannelVideoResponse {
    private Long videoId;
    private String videoName;
    private String thumbnailUrl;
    private int views;
    private int price;
    private Boolean isPurchased;
    private Boolean isInCart;
    private String description;
    private List<VideoCategoryResponse> categories;
    private LocalDateTime createdDate;

    public static Page<ChannelVideoResponse> of(
            Page<Video> videos,
            List<Boolean> isPurchaseInOrder,
            List<String> thumbnailUrlsInOrder,
            List<Long> videoIdsInCart) {
        return videos.map(video -> ChannelVideoResponse.builder()
                .videoId(video.getVideoId())
                .videoName(video.getVideoName())
                .thumbnailUrl(thumbnailUrlsInOrder.get(videos.getContent().indexOf(video)))
                .views(video.getView())
                .price(video.getPrice())
                .isPurchased(isPurchaseInOrder.get(videos.getContent().indexOf(video)))
                .isInCart(videoIdsInCart.contains(video.getVideoId()))
                .description(video.getDescription())
                .categories(VideoCategoryResponse.of(video.getVideoCategories()))
                .createdDate(video.getCreatedDate())
                .build());
    }
}
