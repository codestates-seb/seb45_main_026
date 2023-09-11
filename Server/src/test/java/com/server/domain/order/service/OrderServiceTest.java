package com.server.domain.order.service;

import com.server.domain.cart.entity.Cart;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderStatus;
import com.server.domain.order.entity.OrderVideo;
import com.server.domain.order.repository.OrderRepository;
import com.server.domain.order.service.dto.request.OrderCreateServiceRequest;
import com.server.domain.order.service.dto.response.CancelServiceResponse;
import com.server.domain.order.service.dto.response.OrderResponse;
import com.server.domain.order.service.dto.response.PaymentServiceResponse;
import com.server.domain.reward.entity.Reward;
import com.server.domain.video.entity.Video;
import com.server.domain.watch.entity.Watch;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.orderexception.*;
import com.server.global.exception.businessexception.videoexception.VideoClosedException;
import com.server.global.exception.businessexception.videoexception.VideoNotFoundException;
import com.server.global.testhelper.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.*;
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
        Member owner = createMemberWithChannel();

        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());

        Member loginMember = createAndSaveMember();

        int useReward = 1000;

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .videoIds(List.of(video1.getVideoId(), video2.getVideoId()))
                .reward(useReward)
                .build();

        //when
        OrderResponse response = orderService.createOrder(loginMember.getMemberId(), request);

        //then
        Order order = orderRepository.findById(response.getOrderId()).orElseThrow();

        assertAll("response 확인",
                () -> assertThat(response.getOrderId()).isNotNull(),
                () -> assertThat(response.getTotalAmount()).isEqualTo(video1.getPrice() + video2.getPrice() - request.getReward())
        );

        assertAll("order 정보 확인",
                () -> assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ORDERED),
                () -> assertThat(order.getMember()).isEqualTo(loginMember),
                () -> assertThat(order.getTotalPayAmount()).isEqualTo(video1.getPrice() + video2.getPrice() - request.getReward()),
                () -> assertThat(order.getReward()).isEqualTo(useReward),
                () -> assertThat(order.getOrderVideos().size()).isEqualTo(2)
        );
    }

    @Test
    @DisplayName("주문 시 closed 된 video 가 있으면 VideoClosedException 이 발생한다.")
    void createOrderVideoClosedException() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);
        video2.close(); // video2 를 closed 상태로 만든다.
        Video video3 = createAndSaveVideo(channel);
        video3.close(); // video3 를 closed 상태로 만든다.

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .videoIds(List.of(video1.getVideoId(), video2.getVideoId(), video3.getVideoId()))
                .reward(0)
                .build();

        //when & then
        assertThatThrownBy(() -> orderService.createOrder(member.getMemberId(), request))
                .isInstanceOf(VideoClosedException.class)
                .hasMessage(VideoClosedException.MESSAGE + video2.getVideoName() + ", " + video3.getVideoName());
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
    void createOrderOrderExistExceptionORDERED() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        createAndSaveOrder(member, List.of(video2), 0); // 2번 비디오는 구매 대기 중

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .videoIds(List.of(video1.getVideoId(), video2.getVideoId()))
                .reward(0)
                .build();

        //when & then
        assertThatThrownBy(() -> orderService.createOrder(member.getMemberId(), request))
                .isInstanceOf(OrderExistException.class);
    }

    @Test
    @DisplayName("이미 주문하고 구매완료한 video Id 로 요청하면 OrderExistException 이 발생한다.")
    void createOrderOrderExistExceptionCOMPLTED() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        createAndSaveOrderWithPurchaseComplete(member, List.of(video2), 0); // 2번 비디오는 구매 완료

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
        order.cancelAllOrder(); // 2번 비디오는 이미 구매했지만 취소했다.

        OrderCreateServiceRequest request = OrderCreateServiceRequest.builder()
                .videoIds(List.of(video1.getVideoId(), video2.getVideoId()))
                .reward(0)
                .build();

        setCancelResponseEntitySuccess();

        //when & then
        OrderResponse response = orderService.createOrder(member.getMemberId(), request);

        assertThat(response.getOrderId()).isNotNull();
    }

    @Test
    @DisplayName("주문을 취소하고 order 와 orderVideo 의 상태를 취소로 변경한다.")
    void deleteOrder() {
        //given
        Member owner = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());

        Member loginMember = createAndSaveMember();

        int useReward = 100;
        Order order = createAndSaveOrder(loginMember, List.of(video1, video2), useReward);
        order.completeOrder(LocalDateTime.now(), "paymentKey"); // 주문 완료

        setCancelResponseEntitySuccess();

        //when
        CancelServiceResponse response = orderService.cancelOrder(loginMember.getMemberId(), order.getOrderId());

        //then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);

        assertThat(response.getTotalRequest()).isEqualTo(video1.getPrice() + video2.getPrice());
        assertThat(response.getTotalCancelAmount()).isEqualTo(video1.getPrice() + video2.getPrice() - useReward);
        assertThat(response.getTotalCancelReward()).isEqualTo(useReward);
        assertThat(response.getUsedReward()).isEqualTo(0);
    }

    @Test
    @DisplayName("주문을 취소하면 Reward 를 다시 환불받는다.")
    void deleteOrderRefundReward() {
        //given
        Member owner = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());

        Member loginMember = createAndSaveMember();

        Order order = createAndSaveOrder(loginMember, List.of(video1, video2), 100);
        order.completeOrder(LocalDateTime.now(), "paymentKey"); // 주문 완료

        setCancelResponseEntitySuccess();

        int currentReward = loginMember.getReward();

        //when
        orderService.cancelOrder(loginMember.getMemberId(), order.getOrderId());

        //then
        Member findMember = memberRepository.findById(loginMember.getMemberId()).orElseThrow();
        assertThat(findMember.getReward()).isEqualTo(currentReward + order.getReward());
    }

    @Test
    @DisplayName("일부 주문이 취소된 상태에서도 주문을 취소할 수 있다.")
    void cancelOrderPartially() {
        //given
        Member owner = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());

        Member loginMember = createAndSaveMember();

        int useReward = 100;
        Order order = createAndSaveOrder(loginMember, List.of(video1, video2), useReward);
        order.completeOrder(LocalDateTime.now(), "paymentKey"); // 주문 완료

        order.cancelVideoOrder(order.getOrderVideos().get(0)); // 1번 비디오만 취소

        setCancelResponseEntitySuccess();

        //when
        CancelServiceResponse response = orderService.cancelOrder(loginMember.getMemberId(), order.getOrderId());

        //then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);

        assertThat(response.getTotalRequest()).isEqualTo(video2.getPrice());
        assertThat(response.getTotalCancelAmount()).isEqualTo(video2.getPrice() - useReward);
        assertThat(response.getTotalCancelReward()).isEqualTo(useReward);
        assertThat(response.getUsedReward()).isEqualTo(0);
    }

    @Test
    @DisplayName("주문을 취소할 때 reward 가 부족하면 order 의 환불 리워드에서 차감된다.")
    void deleteOrderNotEnoughRewardMinusInReward() {
        //given
        Member owner = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());

        Member loginMember = createMemberWithChannel();

        Order order = createAndSaveOrder(loginMember, List.of(video1, video2), 100);

        createAndSaveReward(loginMember, video1); // 리워드 생성
        createAndSaveReward(loginMember, video2);

        order.completeOrder(LocalDateTime.now(), "paymentKey"); // 주문 완료

        loginMember.minusReward(loginMember.getReward()); // 다른 곳에 리워드를 모두 사용

        setCancelResponseEntitySuccess();

        //when
        CancelServiceResponse response = orderService.cancelOrder(loginMember.getMemberId(), order.getOrderId());

        //then
        int lackReward = video1.getRewardPoint() + video2.getRewardPoint();

        assertThat(response.getTotalRequest()).isEqualTo(order.getTotalPayAmount() + order.getReward());
        assertThat(response.getTotalCancelAmount()).isEqualTo(order.getTotalPayAmount());
        assertThat(response.getTotalCancelReward()).isEqualTo(order.getReward() - lackReward);
        assertThat(response.getUsedReward()).isEqualTo(lackReward);
    }

    @Test
    @DisplayName("주문을 취소 시 reward 가 부족할 때, order 의 환불 리워드도 부족하면 order 의 환불금액에서 차감된다.")
    void deleteOrderNotEnoughRewardMinusInAmount() {
        //given
        Member owner = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());

        Member loginMember = createAndSaveMember();

        Order order = createAndSaveOrder(loginMember, List.of(video1, video2), 0);

        createAndSaveReward(loginMember, video1); // 리워드 생성
        createAndSaveReward(loginMember, video2);

        order.completeOrder(LocalDateTime.now(), "paymentKey"); // 주문 완료

        loginMember.minusReward(loginMember.getReward()); // 다른 곳에 리워드를 모두 사용

        setCancelResponseEntitySuccess();

        //when
        CancelServiceResponse response = orderService.cancelOrder(loginMember.getMemberId(), order.getOrderId());

        //then
        int lackReward = video1.getRewardPoint() + video2.getRewardPoint();

        assertThat(response.getTotalRequest()).isEqualTo(order.getTotalPayAmount() + order.getReward());
        assertThat(response.getTotalCancelAmount()).isEqualTo(order.getTotalPayAmount() - lackReward);
        assertThat(response.getTotalCancelReward()).isEqualTo(0);
        assertThat(response.getUsedReward()).isEqualTo(lackReward);
    }

    @Test
    @DisplayName("주문을 취소할 때 Video 시청 기록이 있으면 VideoAlreadyWatchedException 이 발생한다.")
    void deleteOrderOrderCannotBeCanceledException() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Order order = createAndSaveOrderWithPurchaseComplete(member, List.of(video1, video2), 100);

        Watch watch = Watch.createWatch(member, video1);
        watchRepository.save(watch); // video1 을 시청한 기록이 있다.

        setCancelResponseEntitySuccess();

        em.flush();
        em.clear();

        //when & then
        assertThatThrownBy(() -> orderService.cancelOrder(member.getMemberId(), order.getOrderId()))
                .isInstanceOf(VideoAlreadyWatchedException.class);
    }

    @Test
    @DisplayName("주문을 취소할 때 orderId 가 존재하지 않으면 OrderNotFoundException 이 발생한다.")
    void deleteOrderOrderNotFoundException() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Order order = createAndSaveOrder(member, List.of(video1, video2), 100);

        String wrongOrderId = order.getOrderId() + "11"; // 존재하지 않는 orderId

        setCancelResponseEntitySuccess();

        //when & then
        assertThatThrownBy(() -> orderService.cancelOrder(member.getMemberId(), wrongOrderId))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("주문을 취소할 때 memberId 가 존재하지 않으면 MemberNotFoundException 이 발생한다.")
    void deleteOrderMemberNotFoundException() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Order order = createAndSaveOrder(member, List.of(video1, video2), 100);

        Long wrongMemberId = member.getMemberId() + 9999L; // 존재하지 않는 memberId

        setCancelResponseEntitySuccess();

        //when & then
        assertThatThrownBy(() -> orderService.cancelOrder(wrongMemberId, order.getOrderId()))
                .isInstanceOf(MemberNotFoundException.class);

    }

    @Test
    @DisplayName("주문을 취소할 때 자신의 주문이 아니면 MemberAccessDeniedException 이 발생한다.")
    void deleteOrderMemberAccessDeniedException() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Member otherMember = createAndSaveMember();

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Order order = createAndSaveOrder(member, List.of(video1, video2), 100);

        setCancelResponseEntitySuccess();

        //when & then // otherMember 의 id
        assertThatThrownBy(() -> orderService.cancelOrder(otherMember.getMemberId(), order.getOrderId()))
                .isInstanceOf(MemberAccessDeniedException.class);
    }

    @Test
    @DisplayName("주문을 취소할 때 이미 취소된 주문이면 OrderAlreadyCanceledException 이 발생한다.")
    void deleteOrderOrderAlreadyCanceledException() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Order order = createAndSaveOrder(member, List.of(video1, video2), 100);
        order.cancelAllOrder(); // 이미 취소된 주문

        setCancelResponseEntitySuccess();

        //when & then
        assertThatThrownBy(() -> orderService.cancelOrder(member.getMemberId(), order.getOrderId()))
                .isInstanceOf(OrderAlreadyCanceledException.class);
    }

    @Test
    @DisplayName("완료된 주문을 취소할 때 pg 사와 통신에 실패하면 CancelFailException 이 발생한다.")
    void deleteOrderOrderCancelFailException() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Order order = createAndSaveOrder(member, List.of(video1, video2), 100);
        order.completeOrder(LocalDateTime.now(), "paymentKey"); // 완료된 주문

        setCancelResponseEntityFail();

        //when & then
        assertThatThrownBy(() -> orderService.cancelOrder(member.getMemberId(), order.getOrderId()))
                .isInstanceOf(CancelFailException.class);
    }

    @Test
    @DisplayName("완료된 주문을 취소할 때 적립받은 리워드를 다시 반환할 수 없어도 가격에서 차감하여 취소할 수 있다.")
    void deleteOrderRewardNotEnoughException() {
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Order order = createAndSaveOrder(member, List.of(video1, video2), 0);
        order.completeOrder(LocalDateTime.now(), "paymentKey"); // 완료된 주문

        Reward reward1 = createAndSaveReward(member, video1);
        Reward reward2 = createAndSaveReward(member, video2);

        member.minusReward(member.getReward()); // 리워드 소멸

        setCancelResponseEntitySuccess();

        //when & then
        orderService.cancelOrder(member.getMemberId(), order.getOrderId());
    }

    @Test
    @DisplayName("완료된 주문을 취소할 때 가진 리워드는 부족하지만 order 로 사용된 reward 로 차감 가능하면 취소할 수 있다.")
    void deleteOrderRewardEnoughWithOrder() {
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Order order = createAndSaveOrder(member, List.of(video1, video2), 1000); // reward 를 1000원을 사용해서 주문
        order.completeOrder(LocalDateTime.now(), "paymentKey"); // 완료된 주문

        Reward reward1 = createAndSaveReward(member, video1);
        Reward reward2 = createAndSaveReward(member, video2);

        member.minusReward(member.getReward()); // 리워드 소멸

        setCancelResponseEntitySuccess();

        //when
        orderService.cancelOrder(member.getMemberId(), order.getOrderId());

        //then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
        assertThat(member.getReward()).isEqualTo(1000 - reward1.getRewardPoint() - reward2.getRewardPoint());
    }
    
    @Test
    @DisplayName("orderId, paymentKey, amount 를 통해 요청한 주문 결제를 완료한다.")
    void requestFinalPayment() {
        //given
        Member owner = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());

        Member loginMember = createAndSaveMember();

        Cart cart = createAndSaveCart(loginMember, video1);
        Order order = createAndSaveOrder(loginMember, List.of(video1, video2), 100);

        setPayResponseEntitySuccess(order.getTotalPayAmount());

        em.flush();
        em.clear();

        LocalDateTime orderDate = LocalDateTime.now();

        //when
        PaymentServiceResponse response = orderService.requestFinalPayment(
                loginMember.getMemberId(),
                "paymentKey",
                order.getOrderId(),
                order.getTotalPayAmount(),
                orderDate);

        //then
        assertAll("결제 정보 확인",
                () -> assertThat(response.getPaymentKey()).isEqualTo("paymentKey"),
                () -> assertThat(response.getTotalAmount()).isEqualTo(order.getTotalPayAmount()),
                () -> assertThat(response.getOrderName()).isEqualTo("orderName")
        );

        Order findOrder = orderRepository.findById(order.getOrderId()).orElseThrow();

        assertAll("주문정보 변경",
                () -> assertThat(findOrder.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED),
                () -> assertThat(findOrder.getPaymentKey()).isEqualTo("paymentKey"),
                () -> assertThat(findOrder.getCompletedDate()).isEqualTo(orderDate)
        );

        assertAll("카트 삭제",
                () -> assertThat(cartRepository.findById(cart.getCartId()).isPresent()).isFalse()
        );

    }

    @Test
    @DisplayName("주문 결제를 완료하면 Reward 를 적립한다.")
    void requestFinalPaymentReward() {
        //given
        Member owner = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());

        Member loginMember = createAndSaveMember();

        Order order = createAndSaveOrder(loginMember, List.of(video1, video2), 100);

        setPayResponseEntitySuccess(order.getTotalPayAmount());

        em.flush();
        em.clear();

        //when
        orderService.requestFinalPayment(loginMember.getMemberId(), "paymentKey", order.getOrderId(), order.getTotalPayAmount(), LocalDateTime.now());

        //then
        //멤버 리워드 생성
        List<Reward> findRewards = rewardRepository.findAll();
        assertThat(findRewards).hasSize(2)
                .extracting("member")
                .extracting("memberId")
                .contains(loginMember.getMemberId());

        //멤버 리워드 적립
        Member findMember = memberRepository.findById(loginMember.getMemberId()).orElseThrow();
        int expectedReward = loginMember.getReward() - order.getReward() + video1.getRewardPoint() + video2.getRewardPoint();
        assertThat(findMember.getReward()).isEqualTo(expectedReward);
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

        setPayResponseEntitySuccess(order.getTotalPayAmount());

        String wrongOrderId = order.getOrderId() + "11";

        //when & then
        assertThatThrownBy(() -> orderService.requestFinalPayment(member.getMemberId(), "paymentKey", wrongOrderId, order.getTotalPayAmount(), LocalDateTime.now()))
                .isInstanceOf(OrderNotFoundException.class);
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

        setPayResponseEntitySuccess(order.getTotalPayAmount());

        //when & then
        assertThatThrownBy(() -> orderService.requestFinalPayment(member.getMemberId(), "paymentKey", order.getOrderId(), order.getTotalPayAmount(), LocalDateTime.now()))
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
        order.cancelAllOrder(); // 주문 취소

        setPayResponseEntitySuccess(order.getTotalPayAmount());

        //when & then
        assertThatThrownBy(() -> orderService.requestFinalPayment(member.getMemberId(), "paymentKey", order.getOrderId(), order.getTotalPayAmount(), LocalDateTime.now()))
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
        order.completeOrder(LocalDateTime.now(), "paymentKey"); // 완료된 주문

        setPayResponseEntitySuccess(order.getTotalPayAmount());

        //when & then
        assertThatThrownBy(() -> orderService.requestFinalPayment(member.getMemberId(), "paymentKey", order.getOrderId(), order.getTotalPayAmount(), LocalDateTime.now()))
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

        int wrongPrice = order.getTotalPayAmount() + 999; // 잘못된 주문 요청 가격

        setPayResponseEntitySuccess(order.getTotalPayAmount());

        //when & then
        assertThatThrownBy(() -> orderService.requestFinalPayment(member.getMemberId(), "paymentKey", order.getOrderId(), wrongPrice, LocalDateTime.now()))
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

        setPayResponseEntityFail(order.getTotalPayAmount()); // pg 통신 실패

        //when & then
        assertThatThrownBy(() -> orderService.requestFinalPayment(member.getMemberId(), "paymentKey", order.getOrderId(), order.getTotalPayAmount(), LocalDateTime.now()))
                .isInstanceOf(OrderNotValidException.class);
    }

    @Test
    @DisplayName("비디오 단건 취소를 요청하면 비디오로 적립한 리워드만큼 현재 리워드에서 차감된다.")
    void cancelVideoRefundReward() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Member loginMember = createAndSaveMember();
        createAndSaveChannel(loginMember);

        Order order = createAndSaveOrderWithPurchaseComplete(loginMember, List.of(video1, video2), 100);

        Reward reward = createAndSaveReward(loginMember, video1);

        int currentReward = loginMember.getReward();

        setCancelResponseEntitySuccess();

        //when (video1 취소)
        orderService.cancelVideo(loginMember.getMemberId(), order.getOrderId(), video1.getVideoId());

        //then
        assertThat(loginMember.getReward()).isEqualTo(currentReward - video1.getRewardPoint());
    }

    @Test
    @DisplayName("비디오 단건 취소를 하면 orderVideo 의 상태가 CANCELED 로 변경된다.")
    void cancelVideo() {
        //given
        Member owner = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());

        Member loginMember = createMemberWithChannel();

        Order order = createAndSaveOrderWithPurchaseComplete(loginMember, List.of(video1, video2), 100);

        setCancelResponseEntitySuccess();

        //when (video1 취소)
        CancelServiceResponse response =
                orderService.cancelVideo(loginMember.getMemberId(), order.getOrderId(), video1.getVideoId());

        //then
        OrderVideo orderVideo1 = order.getOrderVideos().stream()
                .filter(orderVideo -> orderVideo.getVideo().getVideoId().equals(video1.getVideoId()))
                .findFirst().orElseThrow();

        assertAll("orderVideo 가 CANCELED 로 되었는지 확인",
                () -> assertThat(orderVideo1.getOrderStatus()).isEqualTo(OrderStatus.CANCELED)
        );

        assertAll("video 가격만큼 환불되었는지 확인",
                () -> assertThat(response.getTotalCancelAmount() + response.getTotalCancelReward()).isEqualTo(video1.getPrice()),
                () -> assertThat(order.getRemainRefundAmount()).isEqualTo(order.getTotalPayAmount() - video1.getPrice())
        );
    }

    @Test
    @DisplayName("비디오 가격의 일부를 리워드로 구매했을 때 취소하면 가격과 리워드 모두가 환불된다.")
    void cancelVideoOnlyReward() {
        //given
        Member owner = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner.getChannel(), 500);
        Video video2 = createAndSaveVideo(owner.getChannel(), 500);

        Member loginMember = createMemberWithChannel();

        Order order = createAndSaveOrderWithPurchaseComplete(loginMember, List.of(video1, video2), 700);

        setCancelResponseEntitySuccess();

        int currentReward = loginMember.getReward();

        //when (video1 취소)
        CancelServiceResponse response =
                orderService.cancelVideo(loginMember.getMemberId(), order.getOrderId(), video1.getVideoId());

        //then
        assertAll("300원 환불, 200원 리워드 재적립",
                () -> assertThat(response.getTotalCancelAmount()).isEqualTo(300),
                () -> assertThat(response.getTotalCancelReward()).isEqualTo(200)
        );

        assertAll("로그인멤버에 200원 리워드 재적립 확인",
                () -> assertThat(loginMember.getReward()).isEqualTo(currentReward + 200)
        );
    }

    @Test
    @DisplayName("비디오 단건 취소 시 리워드가 부족하면 환불 금액이 감소된다.")
    void cancelVideoRewardNotEnoughReward() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Member loginMember = createAndSaveMember();
        createAndSaveChannel(loginMember);

        Order order = createAndSaveOrderWithPurchaseComplete(loginMember, List.of(video1, video2), 100);

        Reward reward = createAndSaveReward(loginMember, video1);

        loginMember.minusReward(loginMember.getReward()); // 리워드 부족

        setCancelResponseEntitySuccess();

        //when (video1 취소)
        orderService.cancelVideo(loginMember.getMemberId(), order.getOrderId(), video1.getVideoId());

        //then
        assertThat(order.getRemainRefundReward()).isEqualTo(100 - video1.getRewardPoint());
    }

    @Test
    @DisplayName("결제 완료된 주문의 비디오가 아니면 개별 취소할 수 없다. (OrderNotValidException)")
    void cancelVideoNotCompleted() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Member loginMember = createAndSaveMember();
        createAndSaveChannel(loginMember);

        Order order = createAndSaveOrder(loginMember, List.of(video1, video2), 100);

        //when & then
        assertThatThrownBy(() -> orderService.cancelVideo(loginMember.getMemberId(), order.getOrderId(), video1.getVideoId()))
                .isInstanceOf(OrderNotValidException.class);
    }

    @Test
    @DisplayName("order 중 마지막 비디오를 환불하면 order 상태를 CANCELED 로 변경하고 모든 Reward 를 환불한다.")
    void cancelLastVideo() {
        //given
        Member owner = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());

        Member loginMember = createAndSaveMember();
        createAndSaveChannel(loginMember);

        Order order = createAndSaveOrderWithPurchaseComplete(loginMember, List.of(video1, video2), 100);
        OrderVideo orderVideo1 = order.getOrderVideos().stream()
                .filter(orderVideo -> orderVideo.getVideo().getVideoId().equals(video1.getVideoId()))
                .findFirst().orElseThrow();
        orderVideo1.cancel(); // video1 가 취소된 상황

        Reward reward2 = createAndSaveReward(loginMember, video2);

        int currentReward = loginMember.getReward();

        setCancelResponseEntitySuccess();

        //when (video2 취소)
        orderService.cancelVideo(loginMember.getMemberId(), order.getOrderId(), video2.getVideoId());

        //then
        assertThat(loginMember.getReward()).isEqualTo(currentReward + 100 - video2.getRewardPoint());
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @TestFactory
    @DisplayName("2개 video 의 Order 에서 하나의 video 만 취소한 뒤 order 전체를 취소하는 경우")
    Collection<DynamicTest> cancelVideoAndCancelOrder() {
        //given
        Member owner = createMemberWithChannel();
        int video1Price = 1000;
        int video2Price = 1000;
        Video video1 = createAndSaveVideo(owner.getChannel(), video1Price);
        Video video2 = createAndSaveVideo(owner.getChannel(), video2Price);

        int useReward = 100;
        Member loginMember = createMemberWithChannel();

        Order order = createAndSaveOrderWithPurchaseComplete(loginMember, List.of(video1, video2), useReward);

        setCancelResponseEntitySuccess();



        return List.of(
                dynamicTest("video1 결제를 취소한다.", () -> {
                    //when (video1 취소)
                    CancelServiceResponse response =
                            orderService.cancelVideo(loginMember.getMemberId(), order.getOrderId(), video1.getVideoId());

                    //then
                    OrderVideo orderVideo1 = order.getOrderVideos().stream()
                            .filter(orderVideo -> orderVideo.getVideo().getVideoId().equals(video1.getVideoId()))
                            .findFirst().orElseThrow();

                    assertAll("orderVideo 가 CANCELED 로 되었는지 확인",
                            () -> assertThat(orderVideo1.getOrderStatus()).isEqualTo(OrderStatus.CANCELED)
                    );

                    assertAll("video 가격만큼 환불되었는지 확인",
                            () -> assertThat(response.getTotalCancelAmount()).isEqualTo(video1Price),
                            () -> assertThat(order.getRemainRefundAmount()).isEqualTo(order.getTotalPayAmount() - video1Price)
                    );
                }),
                dynamicTest("order 전체를 취소한다.", () -> {
                    //when (video2 취소)
                    CancelServiceResponse response =
                            orderService.cancelVideo(loginMember.getMemberId(), order.getOrderId(), video2.getVideoId());

                    //then
                    OrderVideo orderVideo2 = order.getOrderVideos().stream()
                            .filter(orderVideo -> orderVideo.getVideo().getVideoId().equals(video2.getVideoId()))
                            .findFirst().orElseThrow();

                    assertAll("orderVideo 가 CANCELED 로 되었는지 확인",
                            () -> assertThat(orderVideo2.getOrderStatus()).isEqualTo(OrderStatus.CANCELED)
                    );

                    assertAll("video 가격만큼 환불되었는지 확인",
                            () -> assertThat(response.getTotalCancelAmount()).isEqualTo(video2Price - useReward),
                            () -> assertThat(response.getTotalCancelReward()).isEqualTo(useReward)
                    );

                    assertAll("order 가 CANCELED 로 되었는지 확인",
                            () -> assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELED),
                            () -> assertThat(order.getRemainRefundAmount()).isEqualTo(0),
                            () -> assertThat(order.getRemainRefundReward()).isEqualTo(0)
                    );

                })
        );
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

    private void setCancelResponseEntitySuccess() {

        given(restTemplate.postForEntity(
                any(URI.class),
                any(HttpEntity.class),
                any(Class.class)
        )).willReturn(new ResponseEntity<>("", HttpStatus.OK));
    }

    private void setCancelResponseEntityFail() {

        given(restTemplate.postForEntity(
                any(URI.class),
                any(HttpEntity.class),
                any(Class.class)
        )).willReturn(new ResponseEntity<>("", HttpStatus.BAD_REQUEST));
    }
}