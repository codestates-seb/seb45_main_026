package com.server.domain.video.service.dto.response;

import com.server.domain.video.entity.Video;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class VideoPageResponse {

    private Long videoId;
    private String videoName;
    private String thumbnailUrl;
    private Integer views;
    private Integer price;
    private Float star;
    private Boolean isPurchased;
    private List<VideoCategoryResponse> categories;
    private VideoChannelResponse channel;
    private LocalDateTime createdDate;

    public static Page<VideoPageResponse> of(Page<Video> videos, List<Boolean> isPurchaseInOrder, List<Boolean> isSubscribeInOrder, List<String[]> urlsInOrder) {
        return videos.map(video
                -> of(video,
                isPurchaseInOrder.get(videos.getContent().indexOf(video)),
                isSubscribeInOrder.get(videos.getContent().indexOf(video)),
                urlsInOrder.get(videos.getContent().indexOf(video))
        ));
    }

    private static VideoPageResponse of(Video video, boolean isPurchased, boolean isSubscribed, String[] urls) {
        return VideoPageResponse.builder()
                .videoId(video.getVideoId())
                .videoName(video.getVideoName())
                .thumbnailUrl(urls[0])
                .views(video.getView())
                .price(video.getPrice())
                .star(video.getStar())
                .isPurchased(isPurchased)
                .categories(VideoCategoryResponse.of(video.getVideoCategories()))
                .channel(VideoChannelResponse.of(video.getChannel(), isSubscribed, urls[1]))
                .createdDate(video.getCreatedDate())
                .build();
    }
}
