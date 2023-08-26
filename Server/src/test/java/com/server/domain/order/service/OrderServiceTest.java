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
import com.server.global.exception.businessexception.orderexception.RewardNotEnoughException;
import com.server.global.testhelper.ServiceTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest extends ServiceTest {

    @Autowired VideoRepository videoRepository;
    @Autowired OrderRepository orderRepository;
    @Autowired MemberRepository memberRepository;
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
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Video createAndSaveVideo() {
        Video video = Video.builder()
                .videoName("title")
                .description("description")
                .thumbnailFile("thumbnailFile")
                .videoFile("videoFile")
                .view(0)
                .star(0)
                .price(1000)
                .build();

        videoRepository.save(video);

        return video;
    }

    private Member createAndSaveMember() {
        Member member = Member.builder()
                .email("test@gmail.com")
                .password("1q2w3e4r!")
                .nickname("test")
                .authority(Authority.ROLE_USER)
                .reward(1000)
                .imageFile("imageFile")
                .build();

        memberRepository.save(member);

        return member;
    }
}