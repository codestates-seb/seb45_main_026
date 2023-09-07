package com.server.domain.cart.repository;

import com.server.domain.cart.entity.Cart;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByMemberAndVideo(Member member, Video video);

    @Query("delete Cart c " +
            "where c.video.videoId in :videoIds " +
            "and c.member = :member")
    @Modifying
    int deleteByMemberAndVideoIds(Member member, List<Long> videoIds);
}