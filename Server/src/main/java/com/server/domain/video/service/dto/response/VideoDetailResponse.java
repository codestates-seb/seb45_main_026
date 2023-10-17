package com.server.domain.video.service.dto.response;

import com.server.domain.video.entity.Video;
import com.server.domain.video.entity.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Builder
@Getter
public class VideoDetailResponse {

    private Long videoId;
    private String videoName;
    private String description;
    private String thumbnailUrl;
    private String previewUrl;
    private Integer views;
    private Float star;
    private Integer price;
    private Integer reward;
    private Boolean isReplied;
    private Boolean isPurchased;
    private Boolean isInCart;
    private VideoStatus videoStatus;
    private List<VideoCategoryResponse> categories;
    private VideoChannelResponse channel;
    private LocalDateTime createdDate;

    public static VideoDetailResponse of(Video video,
                                         Boolean subscribed,
                                         Map<String, String> urlMap,
                                         Boolean isPurchased,
                                         Boolean isReplied,
                                         Boolean isInCart) {
        return VideoDetailResponse.builder()
                .videoId(video.getVideoId())
                .videoName(video.getVideoName())
                .description(video.getDescription())
                .thumbnailUrl(urlMap.get("thumbnailUrl"))
                .previewUrl(urlMap.get("previewUrl"))
                .views(video.getView())
                .star(video.getStar())
                .price(video.getPrice())
                .reward(video.getRewardPoint())
                .isPurchased(isPurchased)
                .isReplied(isReplied)
                .isInCart(isInCart)
                .videoStatus(video.getVideoStatus())
                .categories(VideoCategoryResponse.of(video.getVideoCategories()))
                .channel(VideoChannelResponse.of(video.getChannel(), subscribed, urlMap.get("imageUrl")))
                .createdDate(video.getCreatedDate())
                .build();
    }
}
