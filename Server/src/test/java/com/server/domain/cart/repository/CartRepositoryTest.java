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

import java.util.List;

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
        Channel loginMemberChannel = createAndSaveChannel(loginMember);

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

    @Test
    @DisplayName("member 와 videoId 리스트로 모든 cart 를 삭제한다.")
    void deleteByMemberAndVideoIds() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);
        Video video3 = createAndSaveVideo(channel);

        Member loginMember = createAndSaveMember();
        Channel loginMemberChannel = createAndSaveChannel(loginMember);

        Cart cart1 = Cart.createCart(loginMember, video1, video1.getPrice());
        Cart cart2 = Cart.createCart(loginMember, video2, video2.getPrice());
        Cart cart3 = Cart.createCart(loginMember, video3, video3.getPrice());
        em.persist(cart1);
        em.persist(cart2);
        em.persist(cart3);

        em.flush();
        em.clear();

        //when (cart 에 담긴 3개 중 2개만 삭제)
        cartRepository.deleteByMemberAndVideoIds(
                loginMember,
                List.of(video1.getVideoId(), video2.getVideoId())
        );

        //then
        assertThat(cartRepository.findAll()).hasSize(1)
                .extracting("cartId")
                .containsExactlyInAnyOrder(cart3.getCartId());
    }
}