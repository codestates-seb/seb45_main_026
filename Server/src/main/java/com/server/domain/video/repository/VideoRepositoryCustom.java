package com.server.domain.video.repository;

import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.dto.ChannelVideoGetDataRequest;
import com.server.domain.video.repository.dto.VideoGetDataRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface VideoRepositoryCustom {

    Optional<Video> findVideoWithMember(Long videoId);

    Optional<Video> findVideoDetail(Long videoId);

    Page<Video> findAllByCond(VideoGetDataRequest request);

    List<Boolean> isPurchasedAndIsReplied(Long memberId, Long videoId);

    Page<Video> findChannelVideoByCond(ChannelVideoGetDataRequest request);

    Optional<Video> findVideoByNameWithMember(Long memberId, String videoName);

    List<Long> findVideoIdInCart(Long memberId, List<Long> videoIds);
}
