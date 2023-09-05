package com.server.domain.order.repository;

import com.server.domain.cart.entity.Cart;
import com.server.domain.cart.repository.CartRepository;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderVideo;
import com.server.domain.reward.entity.RewardType;
import com.server.domain.video.entity.Video;
import com.server.domain.watch.entity.Watch;
import com.server.global.testhelper.RepositoryTest;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

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

    @Test
    @DisplayName("orderId 로 주문을 조회하면 video 도 초기화되어 조회된다.")
    void findByIdWithVideos() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Member member = createAndSaveMember();

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        createAndSaveVideoReward(member, video1);
        createAndSaveVideoReward(member, video2);

        Order order = createAndSaveOrder(member, List.of(video1, video2));// 주문에 1, 3 추가 (결제 x)

        em.flush();
        em.clear();

        //when
        Order findOrder = orderRepository.findByIdWithVideos(order.getOrderId()).orElseThrow();

        //then
        assertThat(findOrder.getOrderId()).isEqualTo(order.getOrderId());
        assertThat(Hibernate.isInitialized(findOrder.getOrderVideos())).isTrue();
        for (OrderVideo orderVideo : findOrder.getOrderVideos()) {
            assertThat(Hibernate.isInitialized(orderVideo.getVideo())).isTrue();
        }
    }

    @Test
    @DisplayName("orderId 로 주문한 비디오 중 시청한 비디오를 찾는다.")
    void findWatchVideosById() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Member member = createAndSaveMember();

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);
        Video video3 = createAndSaveVideo(channel);
        Video video4 = createAndSaveVideo(channel);

        Order order = createAndSaveOrderComplete(member, List.of(video1, video2, video3));// 결제한 주문

        Watch watch1 = Watch.createWatch(member, video1); //video1 시청
        Watch watch2 = Watch.createWatch(member, video2); //video2 시청
        em.persist(watch1);
        em.persist(watch2);

        em.flush();
        em.clear();

        //when
        List<Video> watchVideos = orderRepository.findWatchVideosById(order.getOrderId());

        //then
        assertThat(watchVideos).hasSize(2)
                        .extracting("videoId")
                        .containsExactly(video1.getVideoId(), video2.getVideoId());
    }

    @Test
    @DisplayName("orderId, videoId 를 통해 orderVideo 를 찾는다. order, member, video 는 초기화되어있다.")
    void findOrderVideoByVideoId() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Member member = createAndSaveMember();
        createAndSaveChannel(member);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Order order = createAndSaveOrderComplete(member, List.of(video1, video2));// 결제한 주문

        em.flush();
        em.clear();

        //when
        OrderVideo orderVideo = orderRepository.findOrderVideoByVideoId(order.getOrderId(), video1.getVideoId()).orElseThrow();

        //then
        assertThat(Hibernate.isInitialized(orderVideo.getOrder())).isTrue();
        assertThat(Hibernate.isInitialized(orderVideo.getVideo())).isTrue();
        assertThat(Hibernate.isInitialized(orderVideo.getOrder().getMember())).isTrue();

        assertThat(orderVideo.getOrder().getOrderId()).isEqualTo(order.getOrderId());
        assertThat(orderVideo.getVideo().getVideoId()).isEqualTo(video1.getVideoId());
    }

    private Cart createAndSaveCart(Member member, Video video) {

        Cart cart = Cart.createCart(member, video, video.getPrice());

        em.persist(cart);

        return cart;
    }
}