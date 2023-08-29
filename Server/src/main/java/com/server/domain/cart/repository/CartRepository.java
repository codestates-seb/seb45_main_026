package com.server.domain.cart.repository;

import com.server.domain.cart.entity.Cart;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByMemberAndVideo(Member member, Video video);
}