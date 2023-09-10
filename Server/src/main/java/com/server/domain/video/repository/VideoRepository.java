package com.server.domain.video.repository;

import com.server.domain.channel.entity.Channel;
import com.server.domain.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.persistence.Tuple;

public interface VideoRepository extends JpaRepository<Video, Long>, VideoRepositoryCustom {

    List<Video> findAllByVideoIdIn(List<Long> videoIds);

    @Modifying
    @Query("UPDATE Video v SET v.channel = null WHERE v.channel = :channel")
    void disconnectVideosFromChannel(@Param("channel") Channel channel);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Video v SET v.star = (SELECT IFNULL(AVG(r.star), 0) FROM Reply r WHERE r.video_id = v.video_id) WHERE v.video_id IN :videoIdsToUpdate", nativeQuery = true)
    void updateVideoRatings(@Param("videoIdsToUpdate") List<Long> videoIdsToUpdate);

    @Query(value = "select v.video_id, v.thumbnail_file, v.video_name, c.member_id " +
        "from video v join channel c on v.channel_id = c.channel_id " +
        "where match(v.video_name) against(?1 in boolean mode) limit ?2", nativeQuery = true)
    List<Tuple> searchVideoByKeyword(String keyword, int limit);
}