package com.server.domain.watch.repository;

import com.server.domain.watch.entity.Watch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WatchRepository extends JpaRepository<Watch, Long> {

    @Query("select w from Watch w where w.member.memberId = ?1 and w.video.videoId = ?2")
    Optional<Watch> findByMemberAndVideo(Long memberId, Long videoId);
}