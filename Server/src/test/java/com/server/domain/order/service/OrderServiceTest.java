package com.server.domain.order.service;

import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderStatus;
import com.server.domain.order.repository.OrderRepository;
import com.server.domain.order.service.dto.request.OrderCreateServiceRequest;
import com.server.domain.order.service.dto.response.OrderResponse;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import com.server.global.exception.businessexception.orderexception.OrderExistException;
import com.server.global.exception.businessexception.orderexception.RewardNotEnoughException;
import com.server.global.exception.businessexception.videoexception.VideoNotFoundException;
import com.server.global.testhelper.ServiceTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

class OrderServiceTest extends ServiceTest {


    @Autowired OrderRepository orderRepository;
    @Autowired OrderService orderService;

    @Test
    @DisplayName("videoId 리스트와 사용하는 reward 를 통해 주문을 생성한다.")
    void createOrder() {
        //given
        Video video1 = createAndSaveVideo();
        Video video2 = createAndSaveVideo();

        Member member = createAndSaveMember();

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .videoIds(List.of(video1.getVideoId(), video2.getVideoId()))
                .reward(1000)
                .build();

        //when
        OrderResponse response = orderService.createOrder(member.getMemberId(), request);

        //then
        Order order = orderRepository.findById(response.getOrderId()).orElseThrow();

        assertThat(response.getOrderId()).isNotNull();
        assertThat(response.getTotalAmount()).isEqualTo(video1.getPrice() + video2.getPrice() - request.getReward());
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ORDERED);
    }

    @Test
    @DisplayName("주문 시 reward 가 부족하면 RewardNotEnoughException 이 발생한다.")
    void createOrderRewardNotEnoughException() {
        //given
        Video video1 = createAndSaveVideo();
        Video video2 = createAndSaveVideo();

        Member member = createAndSaveMember();

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .videoIds(List.of(video1.getVideoId(), video2.getVideoId()))
                .reward(member.getReward() + 1000) // member 가 가진 reward 보다 많은 reward 를 사용하려고 한다.
                .build();

        //when & then
        assertThatThrownBy(() -> orderService.createOrder(member.getMemberId(), request))
                .isInstanceOf(RewardNotEnoughException.class);
    }

    @Test
    @DisplayName("주문 시 없는 video Id 로 요청하면 VideoNotFoundException 이 발생한다.")
    void createOrderVideoNotFoundException() {
        //given
        Video video1 = createAndSaveVideo();
        Video video2 = createAndSaveVideo();

        Member member = createAndSaveMember();

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .videoIds(List.of(video1.getVideoId(), video2.getVideoId(), 999L)) // 999L 은 존재하지 않는 videoId 이다.
                .reward(1000)
                .build();

        //when & then
        assertThatThrownBy(() -> orderService.createOrder(member.getMemberId(), request))
                .isInstanceOf(VideoNotFoundException.class);
    }

    @Test
    @DisplayName("이미 주문한 video Id 로 요청하면 OrderExistException 이 발생한다.")
    void createOrderOrderExistException() {
        //given
        Video video1 = createAndSaveVideo();
        Video video2 = createAndSaveVideo();

        Member member = createAndSaveMember();

        Order order = createAndSaveOrder(member, List.of(video2), 0); // 2번 비디오는 이미 구매

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .videoIds(List.of(video1.getVideoId(), video2.getVideoId()))
                .reward(0)
                .build();

        //when & then
        assertThatThrownBy(() -> orderService.createOrder(member.getMemberId(), request))
                .isInstanceOf(OrderExistException.class);
    }

    @Test
    @DisplayName("이미 Order 에 있는 video 라도 order 의 상태가 CANCEL 이면 주문이 가능하다.")
    void createOrderWithCanceledOrder() {
        //given
        Video video1 = createAndSaveVideo();
        Video video2 = createAndSaveVideo();

        Member member = createAndSaveMember();

        Order order = createAndSaveOrder(member, List.of(video2), 0);
        order.deleteOrder(); // 2번 비디오는 이미 구매했지만 취소했다.

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .videoIds(List.of(video1.getVideoId(), video2.getVideoId()))
                .reward(0)
                .build();

        //when & then
        OrderResponse response = orderService.createOrder(member.getMemberId(), request);

        assertThat(response.getOrderId()).isNotNull();
    }



    @Test
    @DisplayName("주문을 취소하고 order 의 상태를 취소로 변경한다.")
    void deleteOrder() {
        //given
        Video video1 = createAndSaveVideo();
        Video video2 = createAndSaveVideo();

        Member member = createAndSaveMember();

        Order order = createAndSaveOrder(member, List.of(video1, video2), 0);

        orderRepository.save(order);

        //when
        orderService.deleteOrder(member.getMemberId(), order.getOrderId());

        //then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    private Order createAndSaveOrder(Member member, List<Video> video, int reward) {
        Order order = Order.createOrder(member, video, reward);

        orderRepository.save(order);

        return order;
    }
}