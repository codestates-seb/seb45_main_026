package com.server.domain.cart.repository;

import com.server.domain.cart.entity.Cart;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.RepositoryTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;


class CartRepositoryTest extends RepositoryTest {

    @Autowired CartRepository cartRepository;

    @Test
    @DisplayName("member 와 video 로 cart 가 있는지 찾는다.")
    void findByMemberAndVideo() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video = createAndSaveVideo(channel);

        Member loginMember = createAndSaveMember();

        Cart cart = Cart.createCart(loginMember, video, video.getPrice());
        em.persist(cart);

        em.flush();
        em.clear();

        //when
        Cart findCart = cartRepository.findByMemberAndVideo(loginMember, video).orElseThrow();

        //then
        assertThat(findCart.getMember().getMemberId()).isEqualTo(loginMember.getMemberId());
        assertThat(findCart.getVideo().getVideoId()).isEqualTo(video.getVideoId());
    }
}