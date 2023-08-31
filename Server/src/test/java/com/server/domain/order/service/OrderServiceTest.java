package com.server.domain.order.service;

import com.server.domain.cart.entity.Cart;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderStatus;
import com.server.domain.order.repository.OrderRepository;
import com.server.domain.order.service.dto.request.OrderCreateServiceRequest;
import com.server.domain.order.service.dto.response.OrderResponse;
import com.server.domain.order.service.dto.response.PaymentServiceResponse;
import com.server.domain.video.entity.Video;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.orderexception.*;
import com.server.global.exception.businessexception.videoexception.VideoNotFoundException;
import com.server.global.testhelper.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class OrderServiceTest extends ServiceTest {


    @Autowired OrderRepository orderRepository;
    @Autowired OrderService orderService;

    @Test
    @DisplayName("videoId 리스트와 사용하는 reward 를 통해 주문을 생성한다.")
    void createOrder() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

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
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

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
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

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
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

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
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

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
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Order order = createAndSaveOrder(member, List.of(video1, video2), 0);

        orderRepository.save(order);

        //when
        orderService.deleteOrder(member.getMemberId(), order.getOrderId());

        //then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
    }
    
    @Test
    @DisplayName("orderId, paymentKey, amount 를 통해 요청한 주문 결제를 완료한다.")
    void requestFinalPayment() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Member member = createAndSaveMember();

        Cart cart = createAndSaveCart(member, video1);

        Order order = createAndSaveOrder(member, List.of(video1, video2), 100);

        setPayResponseEntitySuccess(order.getPrice());

        em.flush();
        em.clear();

        //when
        PaymentServiceResponse response = orderService.requestFinalPayment(member.getMemberId(), "paymentKey", order.getOrderId(), order.getPrice());

        //then
        //결제 정보 확인
        assertThat(response.getPaymentKey()).isEqualTo("paymentKey");
        assertThat(response.getTotalAmount()).isEqualTo(order.getPrice());
        assertThat(response.getOrderName()).isEqualTo("orderName");

        //주문 정보 변경
        Order findOrder = orderRepository.findById(order.getOrderId()).orElseThrow();
        assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(findOrder.getPaymentKey()).isEqualTo("paymentKey");

        //멤버 리워드 변경
        Member findMember = memberRepository.findById(member.getMemberId()).orElseThrow();
        assertThat(findMember.getReward()).isEqualTo(member.getReward() - order.getReward());

        //카트 삭제
        assertThat(cartRepository.findById(cart.getCartId()).isPresent()).isFalse();
    }

    @Test
    @DisplayName("주문 결제 시 잘못된 orderId 로 요청하면 OrderNotFoundException 이 발생한다.")
    void requestFinalPaymentOrderNotFoundException() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Member member = createAndSaveMember();

        createAndSaveCart(member, video1);

        Order order = createAndSaveOrder(member, List.of(video1, video2), 100);

        setPayResponseEntitySuccess(order.getPrice());

        String wrongOrderId = order.getOrderId() + "11";

        //when & then
        assertThatThrownBy(() -> orderService.requestFinalPayment(member.getMemberId(), "paymentKey", wrongOrderId, order.getPrice()))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("주문 결제 시 member 의 orderId 가 아니면 MemberAccessDeniedException 이 발생한다.")
    void requestFinalPaymentMemberAccessDeniedException() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Member member = createAndSaveMember();
        Member wrongMember = createAndSaveMember();

        createAndSaveCart(member, video1);

        Order order = createAndSaveOrder(member, List.of(video1, video2), 100);

        setPayResponseEntitySuccess(order.getPrice());

        //when & then
        assertThatThrownBy(() -> orderService.requestFinalPayment(wrongMember.getMemberId(), "paymentKey", order.getOrderId(), order.getPrice()))
                .isInstanceOf(MemberAccessDeniedException.class);
    }

    @Test
    @DisplayName("주문 결제 시 잘못된 memberId 로 요청하면 MemberNotFoundException 이 발생한다.")
    void requestFinalPaymentMemberNotFoundException() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Member member = createAndSaveMember();
        Long wrongMemberId = member.getMemberId() + 999L;

        createAndSaveCart(member, video1);

        Order order = createAndSaveOrder(member, List.of(video1, video2), 100);

        setPayResponseEntitySuccess(order.getPrice());

        //when & then
        assertThatThrownBy(() -> orderService.requestFinalPayment(wrongMemberId, "paymentKey", order.getOrderId(), order.getPrice()))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("주문 결제 시 member 의 리워드가 부족하면 RewardNotEnoughException 이 발생한다.")
    void requestFinalPaymentRewardNotEnoughException() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Member member = createAndSaveMember();

        createAndSaveCart(member, video1);

        Order order = createAndSaveOrder(member, List.of(video1, video2), 100);

        member.minusReward(member.getReward()); //리워드 소멸;

        setPayResponseEntitySuccess(order.getPrice());

        //when & then
        assertThatThrownBy(() -> orderService.requestFinalPayment(member.getMemberId(), "paymentKey", order.getOrderId(), order.getPrice()))
                .isInstanceOf(RewardNotEnoughException.class);
    }

    @Test
    @DisplayName("주문 결제 시 order 가 canceled 이면 OrderNotValidException 이 발생한다.")
    void requestFinalPaymentOrderNotValidException() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Member member = createAndSaveMember();

        createAndSaveCart(member, video1);

        Order order = createAndSaveOrder(member, List.of(video1, video2), 100);
        order.deleteOrder(); // 주문 취소

        setPayResponseEntitySuccess(order.getPrice());

        //when & then
        assertThatThrownBy(() -> orderService.requestFinalPayment(member.getMemberId(), "paymentKey", order.getOrderId(), order.getPrice()))
                .isInstanceOf(OrderNotValidException.class);
    }

    @Test
    @DisplayName("주문 결제 시 order 가 complete 이면 OrderNotValidException 이 발생한다.")
    void requestFinalPaymentOrderNotValidExceptionComplete() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Member member = createAndSaveMember();

        createAndSaveCart(member, video1);

        Order order = createAndSaveOrder(member, List.of(video1, video2), 100);
        order.completeOrder(); // 완료된 주문

        setPayResponseEntitySuccess(order.getPrice());

        //when & then
        assertThatThrownBy(() -> orderService.requestFinalPayment(member.getMemberId(), "paymentKey", order.getOrderId(), order.getPrice()))
                .isInstanceOf(OrderNotValidException.class);
    }

    @Test
    @DisplayName("주문 결제 시 order 에 있는 price 와 요청한 amount 가 다르면 PriceNotMatchException 이 발생한다.")
    void requestFinalPaymentPriceNotMatchException() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Member member = createAndSaveMember();

        createAndSaveCart(member, video1);

        Order order = createAndSaveOrder(member, List.of(video1, video2), 100);

        int wrongPrice = order.getPrice() + 999; // 잘못된 주문 요청 가격

        setPayResponseEntitySuccess(order.getPrice());

        //when & then
        assertThatThrownBy(() -> orderService.requestFinalPayment(member.getMemberId(), "paymentKey", order.getOrderId(), wrongPrice))
                .isInstanceOf(PriceNotMatchException.class);
    }

    @Test
    @DisplayName("주문 결제 시 pg 사와 통신에 실패하면 OrderNotValidException 이 발생한다.")
    void requestFinalPaymentOrderNotValidExceptionPG() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Member member = createAndSaveMember();

        createAndSaveCart(member, video1);

        Order order = createAndSaveOrder(member, List.of(video1, video2), 100);

        setPayResponseEntityFail(order.getPrice()); // pg 통신 실패

        //when & then
        assertThatThrownBy(() -> orderService.requestFinalPayment(member.getMemberId(), "paymentKey", order.getOrderId(), order.getPrice()))
                .isInstanceOf(OrderNotValidException.class);
    }

    private void setPayResponseEntitySuccess(int price) {

        PaymentServiceResponse paymentServiceResponse = PaymentServiceResponse.builder()
                .paymentKey("paymentKey")
                .orderName("orderName")
                .totalAmount(price)
                .build();

        given(restTemplate.postForEntity(
                anyString(),
                any(HttpEntity.class),
                any(Class.class)
        )).willReturn(new ResponseEntity<>(paymentServiceResponse, HttpStatus.OK));
    }

    private void setPayResponseEntityFail(int price) {

        PaymentServiceResponse paymentServiceResponse = PaymentServiceResponse.builder()
                .paymentKey("paymentKey")
                .orderName("orderName")
                .totalAmount(price)
                .build();

        given(restTemplate.postForEntity(
                anyString(),
                any(HttpEntity.class),
                any(Class.class)
        )).willReturn(new ResponseEntity<>(paymentServiceResponse, HttpStatus.BAD_REQUEST));
    }

    private Cart createAndSaveCart(Member member, Video video) {
        Cart cart = Cart.createCart(member, video, video.getPrice());

        cartRepository.save(cart);

        return cart;
    }
}