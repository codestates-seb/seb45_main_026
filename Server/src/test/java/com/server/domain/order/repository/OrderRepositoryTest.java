package com.server.domain.order.repository;

import com.server.domain.cart.entity.Cart;
import com.server.domain.cart.repository.CartRepository;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderVideo;
import com.server.domain.video.entity.Video;
import com.server.domain.watch.entity.Watch;
import com.server.global.testhelper.RepositoryTest;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.*;

class OrderRepositoryTest extends RepositoryTest {

    @Autowired OrderRepository orderRepository;
    @Autowired CartRepository cartRepository;

    @Test
    @DisplayName("video 1, 2 를 장바구니에 담은 후 video 1, 3 을 주문하면 장바구니에 video 1 이 삭제되고, video 2 는 남아있다. (쿼리 1번)")
    void deleteCartByMemberAndOrderId() {
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
        Long deleteCount = orderRepository.deleteCartByMemberAndOrderId(member.getMemberId(), order.getOrderId());

        //then
        assertThat(deleteCount).isEqualTo(1L);

        assertThat(cartRepository.findById(cart1.getCartId()).isPresent()).isFalse();
        assertThat(cartRepository.findById(cart2.getCartId()).isPresent()).isTrue();

        assertThat(cartRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("memberId 와 videoId 로 해당 video 중 구매하거나 구매대기중인 OrderVideo 를 조회한다.")
    void findOrderedVideosByMemberId() {
        //given
        Member owner = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());
        Video video3 = createAndSaveVideo(owner.getChannel());

        Member loginMember = createAndSaveMember();
        createAndSaveOrderComplete(loginMember, List.of(video1)); // 주문에 1 추가 (결제 완료)
        createAndSaveOrder(loginMember, List.of(video2)); // 주문에 2 추가 (결제 x)

        List<Long> videoIds = List.of(video1.getVideoId(), video2.getVideoId(), video3.getVideoId());

        //when
        List<OrderVideo> orderVideos = orderRepository.findOrderedVideosByMemberId(loginMember.getMemberId(), videoIds);

        //then
        assertThat(orderVideos.size()).isEqualTo(2);
        assertThat(orderVideos).hasSize(2)
                .extracting("video")
                .extracting("videoId")
                .containsExactly(video1.getVideoId(), video2.getVideoId());
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

        Order order = createAndSaveOrder(member, List.of(video1, video2));// 주문에 1, 3 추가 (결제 x)

        em.flush();
        em.clear();

        //when
        Order findOrder = orderRepository.findByIdWithVideos(member.getMemberId(), order.getOrderId()).orElseThrow();

        //then
        assertThat(findOrder.getOrderId()).isEqualTo(order.getOrderId());
        assertThat(Hibernate.isInitialized(findOrder.getOrderVideos())).isTrue();
        assertThat(Hibernate.isInitialized(findOrder.getMember())).isTrue();
        for (OrderVideo orderVideo : findOrder.getOrderVideos()) {
            assertThat(Hibernate.isInitialized(orderVideo.getVideo())).isTrue();
        }
    }

    @Test
    @DisplayName("orderId 로 주문한 비디오 중 시청한 비디오를 찾는다.")
    void findWatchVideosById() throws InterruptedException {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Member member = createAndSaveMember();

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);
        Video video3 = createAndSaveVideo(channel);
        Video video4 = createAndSaveVideo(channel);

        Order order = createAndSaveOrderComplete(member, List.of(video1, video2, video3));// 결제한 주문

        em.flush();
        em.clear();

        sleep(100);

        Watch watch1 = Watch.createWatch(member, video1); //video1 시청
        Watch watch2 = Watch.createWatch(member, video2); //video2 시청
        em.persist(watch1);
        em.persist(watch2);

        em.flush();
        em.clear();

        //when
        List<Video> watchVideos = orderRepository.findWatchVideosAfterPurchaseById(order);

        //then
        assertThat(watchVideos).hasSize(2)
                        .extracting("videoId")
                        .containsExactly(video1.getVideoId(), video2.getVideoId());
    }

    @Test
    @DisplayName("orderId 로 주문한 비디오 중 시청한 비디오를 찾는다.")
    void findWatchVideoAfterPurchaseByVideoId() {
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
        Boolean isWatch = orderRepository.checkIfWatchAfterPurchase(order, video1.getVideoId());

        //then
        assertThat(isWatch).isTrue();
    }

    @Test
    @DisplayName("orderId 로 주문한 비디오 중 시청한 비디오를 찾는다.")
    void findWatchVideoAfterPurchaseByVideoIdNotWatch() {
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

        //when (3번은 시청하지 않음)
        Boolean isWatch = orderRepository.checkIfWatchAfterPurchase(order, video3.getVideoId());

        //then
        assertThat(isWatch).isFalse();
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