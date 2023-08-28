package com.server.domain.watch.repository;

import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.domain.watch.entity.Watch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WatchRepository extends JpaRepository<Watch, Long> {
    Optional<Watch> findByMemberAndVideo(Member member, Video video);
}