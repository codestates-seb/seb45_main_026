package com.server.domain.video.repository;

import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.dto.VideoGetDataRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface VideoRepositoryCustom {

    Optional<Video> findVideoWithMember(Long videoId);

    Optional<Video> findVideoDetail(Long videoId);

    Page<Video> findAllByCategoryPaging(VideoGetDataRequest request);

    List<Boolean> isPurchasedAndIsReplied(Long memberId, Long videoId);

    Page<Video> findChannelVideoByCategoryPaging(Long memberId, String category, Pageable pageable, String sort, Boolean free);

    Optional<Video> findVideoByNameWithMember(Long memberId, String videoName);
}
