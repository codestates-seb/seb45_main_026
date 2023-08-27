package com.server.domain.video.repository;

import com.server.domain.video.entity.Video;

import java.util.Optional;

public interface VideoRepositoryCustom {

    Optional<Video> findVideoWithMember(Long videoId);
}
