package com.server.domain.video.repository;

import com.server.domain.video.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface VideoRepositoryCustom {

    Optional<Video> findVideoWithMember(Long videoId);

    Page<Video> findAllByCategoryPaging(String category, Pageable pageable, String sort, Long memberId, boolean subscribe);
}
