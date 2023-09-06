package com.server.domain.video.repository;

import com.server.domain.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long>, VideoRepositoryCustom {

    List<Video> findAllByVideoIdIn(List<Long> videoIds);

    @Query(value = "SELECT * FROM video " +
        "INNER JOIN channel ON video.channel_id = channel.channel_id " +
        "INNER JOIN member ON channel.member_id = member.member_id " +
        "WHERE MATCH(video_name) AGAINST (?1 IN BOOLEAN MODE) " +
        "AND video_status != 'CLOSED'",
        nativeQuery = true)
    List<Video> searchVideos(String keyword);

}