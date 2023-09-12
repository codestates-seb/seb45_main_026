package com.server.domain.reply.repository;

import com.server.domain.member.entity.Member;
import com.server.domain.reply.dto.ReplyInfo;
import com.server.domain.reply.entity.Reply;
import com.server.domain.video.entity.Video;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("select r from Reply r where r.video.videoId = :videoId")
    Page<Reply> findAllByVideoIdPaging(@Param("videoId") Long videoId, Pageable pageable);

    @Query("select r from Reply r where r.video.videoId = :videoId and (:star is null or r.star >= :star)")
    Page<Reply> findAllByVideoIdAndStarOrStarIsNull(@Param("videoId") Long videoId, @Param("star") Integer star, Pageable pageable);

    @Query("select r from Reply r where r.member.memberId = :memberId and r.video.videoId = :videoId")
    List<Reply> findAllByMemberIdAndVideoId(@Param("memberId")Long memberId, @Param("videoId")Long videoId);
}
