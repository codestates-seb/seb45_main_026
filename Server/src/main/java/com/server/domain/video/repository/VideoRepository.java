package com.server.domain.video.repository;

import com.server.domain.channel.entity.Channel;
import com.server.domain.video.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long>, VideoRepositoryCustom {

    List<Video> findAllByVideoIdIn(List<Long> videoIds);

    Page<Video> findByChannel(Channel channel, PageRequest pageRequest);
}