package com.server.domain.video.repository;

import com.server.domain.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import javax.persistence.Tuple;

public interface VideoRepository extends JpaRepository<Video, Long>, VideoRepositoryCustom {

    List<Video> findAllByVideoIdIn(List<Long> videoIds);

    @Query(value = "select v.video_id, v.thumbnail_file, v.video_name, c.member_id " +
        "from video v join channel c on v.channel_id = c.channel_id " +
        "where match(v.video_name) against(?1 in boolean mode) limit ?2", nativeQuery = true)
    List<Tuple> searchVideoByKeyword(String keyword, int limit);

}