package com.server.domain.order.repository;

import com.server.domain.cart.entity.Cart;
import com.server.domain.cart.repository.CartRepository;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class OrderRepositoryTest extends RepositoryTest {

    @Autowired OrderRepository orderRepository;
    @Autowired CartRepository cartRepository;

    @Test
    @DisplayName("video 1, 2 를 장바구니에 담은 후 video 1, 3 을 주문하면 장바구니에 video 1 이 삭제되고, video 2 는 남아있다. (쿼리 1번)")
    void deleteCartByMemberAndOrderId1() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Member member = createAndSaveMember();
        Member otherMember = createAndSaveMember();

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);
        Video video3 = createAndSaveVideo(channel);

        Cart cart1 = createAndSaveCart(member, video1);
        Cart cart2 = createAndSaveCart(member, video2); // 장바구니에 1, 2 추가
        Cart otherCart1 = createAndSaveCart(otherMember, video1);
        Cart otherCart2 = createAndSaveCart(otherMember, video3);


        Order order = createAndSaveOrder(member, List.of(video1, video3));// 주문에 1, 3 추가 (결제 x)

        em.flush();
        em.clear();

        //when
        Long deleteCount = orderRepository.deleteCartByMemberAndOrderId1(member.getMemberId(), order.getOrderId());

        //then
        assertThat(deleteCount).isEqualTo(1L);

        assertThat(cartRepository.findById(cart1.getCartId()).isPresent()).isFalse();
        assertThat(cartRepository.findById(cart2.getCartId()).isPresent()).isTrue();

        assertThat(cartRepository.findAll().size()).isEqualTo(3);

    }

    @Test
    @DisplayName("video 1, 2 를 장바구니에 담은 후 video 1, 3 을 주문하면 장바구니에 video 1 이 삭제되고, video 2 는 남아있다. (쿼리 2번)")
    void deleteCartByMemberAndOrderId2() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Member member = createAndSaveMember();
        Member otherMember = createAndSaveMember();

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);
        Video video3 = createAndSaveVideo(channel);

        Cart cart1 = createAndSaveCart(member, video1);
        Cart cart2 = createAndSaveCart(member, video2); // 장바구니에 1, 2 추가
        Cart otherCart1 = createAndSaveCart(otherMember, video1);
        Cart otherCart2 = createAndSaveCart(otherMember, video3);


        Order order = createAndSaveOrder(member, List.of(video1, video3));// 주문에 1, 3 추가 (결제 x)

        em.flush();
        em.clear();

        //when
        Long deleteCount = orderRepository.deleteCartByMemberAndOrderId2(member.getMemberId(), order.getOrderId());

        //then
        assertThat(deleteCount).isEqualTo(1L);

        assertThat(cartRepository.findById(cart1.getCartId()).isPresent()).isFalse();
        assertThat(cartRepository.findById(cart2.getCartId()).isPresent()).isTrue();

        assertThat(cartRepository.findAll().size()).isEqualTo(3);
    }

    private Cart createAndSaveCart(Member member, Video video) {

        Cart cart = Cart.createCart(member, video, video.getPrice());

        em.persist(cart);

        return cart;
    }
}