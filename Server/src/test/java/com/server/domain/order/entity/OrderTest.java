package com.server.domain.order.entity;

import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.global.exception.businessexception.orderexception.*;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.*;

class OrderTest {

    @Test
    void addOrderVideo() {
    }

    @Test
    @DisplayName("member, video, reward 를 받아 order 를 생성한다.")
    void createOrder() {
        //given
        Member member = createMember();
        Video video1 = createVideo();
        Video video2 = createVideo();

        int usingReward = 500;
        int totalPayAmount = video1.getPrice() + video2.getPrice() - usingReward;

        //when
        Order order = Order.createOrder(member, List.of(video1, video2), usingReward);

        //then
        assertThat(order.getTotalPayAmount()).isEqualTo(totalPayAmount);
        assertThat(order.getRemainRefundAmount()).isEqualTo(0);
        assertThat(order.getReward()).isEqualTo(usingReward);
        assertThat(order.getRemainRefundReward()).isEqualTo(0);
        assertThat(order.getMember()).isEqualTo(member);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ORDERED);
        assertThat(order.getOrderVideos()).hasSize(2)
                .extracting("video", "orderStatus")
                .containsExactlyInAnyOrder(
                        Tuple.tuple(video1, OrderStatus.ORDERED),
                        Tuple.tuple(video2, OrderStatus.ORDERED)
                );
    }

    @Test
    @DisplayName("order 생성 시 member 의 reward 가 부족하면 RewardNotEnoughException 이 발생한다.")
    void createOrderRewardNotEnoughException() {
        //given
        Member member = createMember();
        Video video1 = createVideo();
        Video video2 = createVideo();

        int usingReward = 1500;

        //when
        assertThatThrownBy(() -> Order.createOrder(member, List.of(video1, video2), usingReward))
                .isInstanceOf(RewardNotEnoughException.class);
    }

    @Test
    @DisplayName("order 생성 시 totalAmount 보다 사용하는 reward 가 크면 RewardExceedException 이 발생한다.")
    void createOrderRewardExceedException() {
        //given
        Member member = createMember();
        Video video1 = createVideo(100);
        Video video2 = createVideo(100);

        int usingReward = 500;

        //when
        assertThatThrownBy(() -> Order.createOrder(member, List.of(video1, video2), usingReward))
                .isInstanceOf(RewardExceedException.class);
    }

    @Test
    @DisplayName("order 의 비디오 목록을 가져온다.")
    void getVideos() {
        //given
        Member member = createMember();
        Video video1 = createVideo("title1");
        Video video2 = createVideo("title2");

        Order order = createOrder(member, List.of(video1, video2), 0);

        //when
        List<Video> videos = order.getVideos();

        //then
        assertThat(videos).hasSize(2)
                .extracting("videoName").containsExactly("title1", "title2");
    }

    @Test
    @DisplayName("order 가 주문완료되기에 유효한 상태인지 확인한다.")
    void checkValidOrder() {
        //given
        Member member = createMember();
        Video video1 = createVideo();
        Video video2 = createVideo();

        int useReward = 500;
        int payAmount = video1.getPrice() + video2.getPrice() - useReward;
        Order order = createOrder(member, List.of(video1, video2), useReward);

        //when & then
        assertThatNoException().isThrownBy(() -> order.checkValidOrder(payAmount));
    }

    @Test
    @DisplayName("order 가 유효한 상태인지 확인할 때 이미 주문완료 상태면 OrderNotValidException 이 발생한다.")
    void checkValidOrderIsComplete() {
        //given
        Member member = createMember();
        Video video1 = createVideo();
        Video video2 = createVideo();

        int useReward = 500;
        int payAmount = video1.getPrice() + video2.getPrice() - useReward;
        Order order = createOrderComplete(member, List.of(video1, video2), useReward);

        //when & then
        assertThatThrownBy(() -> order.checkValidOrder(payAmount))
                .isInstanceOf(OrderNotValidException.class);
    }

    @Test
    @DisplayName("order 가 유효한 상태인지 확인할 때 취소된 상태이면 OrderNotValidException 이 발생한다.")
    void checkValidOrderIsCanceled() {
        //given
        Member member = createMember();
        Video video1 = createVideo();
        Video video2 = createVideo();

        int useReward = 500;
        int payAmount = video1.getPrice() + video2.getPrice() - useReward;
        Order order = createOrderCancel(member, List.of(video1, video2), useReward);

        //when & then
        assertThatThrownBy(() -> order.checkValidOrder(payAmount))
                .isInstanceOf(OrderNotValidException.class);
    }

    @Test
    @DisplayName("order 가 유효한 상태인지 확인할 때 최초에 요청했던 값과 맞지 않으면 PriceNotMatchException 이 발생한다.")
    void checkValidOrderPriceNotMatchException() {
        //given
        Member member = createMember();
        Video video1 = createVideo();
        Video video2 = createVideo();

        int useReward = 500;
        int payAmount = video1.getPrice() + video2.getPrice() - useReward;
        Order order = createOrder(member, List.of(video1, video2), useReward);

        //when & then
        assertThatThrownBy(() -> order.checkValidOrder(payAmount - 100))
                .isInstanceOf(PriceNotMatchException.class);
    }

    @Test
    @DisplayName("ORDERED 상태인 Order 을 주문완료(COMPLETED) 상태로 만든다.")
    void completeOrder() {
        //given
        Member member = createMember();
        int currentReward = member.getReward();
        Video video1 = createVideo();
        Video video2 = createVideo();

        int useReward = 500;
        int payAmount = video1.getPrice() + video2.getPrice() - useReward;
        LocalDateTime orderDate = LocalDateTime.now();
        Order order = createOrder(member, List.of(video1, video2), useReward);

        //when
        order.completeOrder(orderDate, "paymentKey");

        //then
        assertAll("order 상태가 completed 로 바뀐다.",
                () -> assertThat(order.getRemainRefundAmount()).isEqualTo(payAmount),
                () -> assertThat(order.getRemainRefundReward()).isEqualTo(useReward),
                () -> assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED),
                () -> assertThat(order.getPaymentKey()).isEqualTo("paymentKey"),
                () -> assertThat(order.getCompletedDate()).isEqualTo(orderDate)
        );

        assertAll("orderVideo 의 상태가 completed 로 바뀐다.",
                () -> assertThat(order.getOrderVideos()).hasSize(2)
                        .extracting("orderStatus").containsExactly(OrderStatus.COMPLETED, OrderStatus.COMPLETED)
        );

        assertAll("member 의 리워드가 차감된다.",
                () -> assertThat(member.getReward()).isEqualTo(currentReward - useReward)
        );
    }

    @Test
    @DisplayName("ORDERED 상태인 Order 을 주문완료(COMPLETED) 상태로 만들 때 처음에 요청했던 reward 만큼 보유하지 않으면 RewardNotEnoughException 이 발생한다.")
    void completeOrderRewardNotEnoughException() {
        //given
        Member member = createMember();
        Video video1 = createVideo();
        Video video2 = createVideo();

        int useReward = 500;
        LocalDateTime orderDate = LocalDateTime.now();
        Order order = createOrder(member, List.of(video1, video2), useReward);

        member.minusReward(member.getReward()); // 리워드를 0 으로 만든다.

        //when & then
        assertThatThrownBy(() -> order.completeOrder(orderDate, "paymentKey"))
                .isInstanceOf(RewardNotEnoughException.class);
    }

    @Test
    @DisplayName("order 내의 모든 orderVideo, order 를 취소 상태로 변경한다.")
    void cancelAllOrder() {
        //given
        Member member = createMember();
        Video video1 = createVideo();
        Video video2 = createVideo();

        int useReward = 500;
        Order order = createOrderComplete(member, List.of(video1, video2), useReward);

        //when
        Order.Refund refund = order.cancelAllOrder();

        //then
        assertAll("orderVideo 의 상태가 canceled 로 바뀐다.",
                () -> assertThat(order.getOrderVideos()).hasSize(2)
                        .extracting("orderStatus").containsExactly(OrderStatus.CANCELED, OrderStatus.CANCELED)
        );

        assertAll("order 의 상태가 canceled 로 바뀐다.",
                () -> assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELED)
        );
    }

    @Test
    @DisplayName("order 을 취소하면 환불할 금액과 리워드를 반환하고 남은 금액을 0으로 만든다.")
    void cancelAllOrderReturnValue() {
        //given
        Member member = createMember();
        Video video1 = createVideo();
        Video video2 = createVideo();

        int useReward = 500;
        Order order = createOrderComplete(member, List.of(video1, video2), useReward);

        int totalOrderAmount = video1.getPrice() + video2.getPrice() - useReward;

        //when
        Order.Refund refund = order.cancelAllOrder();

        //then
        assertAll("환불할 금액과 리워드를 반환한다.",
                () -> assertThat(refund.getRefundAmount()).isEqualTo(totalOrderAmount),
                () -> assertThat(refund.getRefundReward()).isEqualTo(useReward)
        );

        assertAll("남은 금액을 0으로 만든다.",
                () -> assertThat(order.getRemainRefundAmount()).isEqualTo(0),
                () -> assertThat(order.getRemainRefundReward()).isEqualTo(0)
        );
    }

    @Test
    @DisplayName("order 을 취소하면 남은 리워드 만큼 member Reward 가 반환된다.")
    void cancelAllOrderRefundReward() {
        //given
        Member member = createMember();
        Video video1 = createVideo();
        Video video2 = createVideo();

        int useReward = 500;
        Order order = createOrderComplete(member, List.of(video1, video2), useReward);

        int afterOrderRemainReward = member.getReward();

        //when
        order.cancelAllOrder();

        //then
        assertThat(member.getReward()).isEqualTo(afterOrderRemainReward + useReward);
    }

    @Test
    @DisplayName("order 가 completed 상태인지 확인한다. - true")
    void isCompleteTrue() {
        //given
        Member member = createMember();
        Video video1 = createVideo();

        Order order = createOrderComplete(member, List.of(video1), 0);

        //when
        boolean complete = order.isComplete();

        //then
        assertThat(complete).isTrue();
    }

    @Test
    @DisplayName("order 가 completed 상태인지 확인한다. - false")
    void isCompleteFalse() {
        //given
        Member member = createMember();
        Video video1 = createVideo();

        Order order = createOrder(member, List.of(video1), 0);

        //when
        boolean complete = order.isComplete();

        //then
        assertThat(complete).isFalse();
    }

    @Test
    @DisplayName("order 의 상태를 확인하고 canceled 상태이면 OrderAlreadyCanceledException 이 발생한다.")
    void checkAlreadyCanceled() {
        //given
        Member member = createMember();
        Video video1 = createVideo();

        Order order = createOrderCancel(member, List.of(video1), 0);

        //when & then
        assertThatThrownBy(() -> order.checkAlreadyCanceled())
                .isInstanceOf(OrderAlreadyCanceledException.class);
    }

    @TestFactory
    @DisplayName("order 의 상태를 확인하고 canceled 상태가 아니면 예외가 발생하지 않는다.")
    Collection<DynamicTest> checkAlreadyCanceledNotCanceled() {
        //given
        Member member = createMember();
        Video video1 = createVideo();

        return List.of(
                dynamicTest("order 의 상태가 ordered 이면 예외가 발생하지 않는다.", () -> {
                    //given
                    Order order = createOrder(member, List.of(video1), 0);

                    //when & then
                    assertThatNoException().isThrownBy(() -> order.checkAlreadyCanceled());

                }),
                dynamicTest("order 의 상태가 completed 이면 예외가 발생하지 않는다.", () -> {
                    //given
                    Order order = createOrderComplete(member, List.of(video1), 0);

                    //when & then
                    assertThatNoException().isThrownBy(() -> order.checkAlreadyCanceled());
                })
        );
    }

    @Test
    @DisplayName("orderVideo 를 취소하면 상태가 취소 상태로 변경한다.")
    void cancelVideoOrder() {
        //given
        Member member = createMember();
        Video video1 = createVideo();
        Video video2 = createVideo();

        Order order = createOrderCancel(member, List.of(video1, video2), 0);

        OrderVideo orderVideo = getOrderVideo(order, video1);

        //when
        order.cancelVideoOrder(orderVideo);

        //then
        assertThat(orderVideo.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @TestFactory
    @DisplayName("orderVideo 를 취소하면 환불할 금액과 리워드를 반환한다.")
    Collection<DynamicTest> cancelVideoOrderReturnValue() {
        //given
        Member member = createMember();
        Video video1 = createVideo(1000);
        Video video2 = createVideo(1000);

        int useReward = 1500;

        Order order = createOrderComplete(member, List.of(video1, video2), useReward);

        return List.of(
                dynamicTest("video1 을 취소한다.", () -> {
                    //given
                    OrderVideo orderVideo1 = getOrderVideo(order, video1);

                    int currentMemberReward = member.getReward();

                    //when
                    Order.Refund refund = order.cancelVideoOrder(orderVideo1);

                    //then
                    assertAll("취소되는 금액, 리워드 확인",
                            () -> assertThat(refund.getRefundAmount()).isEqualTo(500),
                            () -> assertThat(refund.getRefundReward()).isEqualTo(500)
                    );

                    assertAll("orderVideo 취소 확인",
                            () -> assertThat(orderVideo1.getOrderStatus()).isEqualTo(OrderStatus.CANCELED)
                    );

                    assertAll("멤버 리워드 확인",
                            () -> assertThat(member.getReward()).isEqualTo(currentMemberReward + 500)
                    );
                }),
                dynamicTest("video2 를 취소한다.", ()-> {
                    //given
                    OrderVideo orderVideo2 = getOrderVideo(order, video2);

                    int currentMemberReward = member.getReward();

                    //when
                    Order.Refund refund = order.cancelVideoOrder(orderVideo2);

                    //then
                    assertAll("취소되는 금액, 리워드 확인",
                            () -> assertThat(refund.getRefundAmount()).isEqualTo(0),
                            () -> assertThat(refund.getRefundReward()).isEqualTo(1000)
                    );

                    assertAll("멤버 리워드 확인",
                            () -> assertThat(member.getReward()).isEqualTo(currentMemberReward + 1000)
                    );

                    assertAll("orderVideo 취소 확인",
                            () -> assertThat(orderVideo2.getOrderStatus()).isEqualTo(OrderStatus.CANCELED)
                    );

                    assertAll("order 취소 확인",
                            () -> assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELED),
                            () -> assertThat(order.getRemainRefundAmount()).isEqualTo(0),
                            () -> assertThat(order.getRemainRefundReward()).isEqualTo(0)
                    );
                })
        );
    }

    @TestFactory
    @DisplayName("환불할 reward 가 부족한 상황일 때 결제 금액 혹은 리워드에서 차감하여 member 에 추가한다.")
    Collection<DynamicTest> convertAmountToReward() {
        //given
        Member member = createMember();
        Video video1 = createVideo(1000);
        Video video2 = createVideo(1000);

        int useReward = 1000;

        Order order = createOrderComplete(member, List.of(video1, video2), useReward);

        return List.of(
                dynamicTest("500원을 리워드에서 차감해서 member reward 로 추가한다.", () -> {
                    //given
                    int currentMemberReward = member.getReward();
                    int convertAmount = 500;
                    int orderRemainRefundReward = order.getRemainRefundReward();

                    //when
                    order.convertAmountToReward(convertAmount);

                    //then
                    assertAll("멤버 리워드 확인",
                            () -> assertThat(member.getReward()).isEqualTo(currentMemberReward + convertAmount)
                    );

                    assertAll("order 리워드 확인",
                            () -> assertThat(order.getRemainRefundReward()).isEqualTo(orderRemainRefundReward - convertAmount)
                    );
                }),
                dynamicTest("700원을 차감하면 500원은 리워드에서, 200원은 금액에서 차감한 후 member reward 로 추가한다.", ()-> {
                    //given
                    int currentMemberReward = member.getReward();
                    int convertAmount = 700;
                    int orderRemainRefundAmount = order.getRemainRefundAmount();
                    int orderRemainRefundReward = order.getRemainRefundReward();

                    //when
                    order.convertAmountToReward(convertAmount);

                    //then
                    assertAll("멤버 리워드 확인",
                            () -> assertThat(member.getReward()).isEqualTo(currentMemberReward + convertAmount)
                    );

                    assertAll("order 리워드 확인",
                            () -> assertThat(order.getRemainRefundReward()).isEqualTo(orderRemainRefundReward - 500),
                            () -> assertThat(order.getRemainRefundAmount()).isEqualTo(orderRemainRefundAmount - (convertAmount - 500))
                    );
                }),
                dynamicTest("800원을 주문 금액에서 차감한 후 member reward 로 추가한다.", ()-> {
                    //given
                    int currentMemberReward = member.getReward();
                    int convertAmount = 800;
                    int orderRemainRefundAmount = order.getRemainRefundAmount();

                    //when
                    order.convertAmountToReward(convertAmount);

                    //then
                    assertAll("멤버 리워드 확인",
                            () -> assertThat(member.getReward()).isEqualTo(currentMemberReward + convertAmount)
                    );

                    assertAll("order 리워드 확인",
                            () -> assertThat(order.getRemainRefundAmount()).isEqualTo(orderRemainRefundAmount - convertAmount)
                    );
                }),
                dynamicTest("주문 금액에서 차감 시 금액이 부족하면 RewardNotEnoughException 이 발생한다.", ()-> {
                    //given
                    int convertAmount = 500;

                    //when & then
                    assertThatThrownBy(() -> order.convertAmountToReward(convertAmount))
                            .isInstanceOf(RewardNotEnoughException.class);
                })
        );
    }

    private OrderVideo getOrderVideo(Order order, Video video) {
        return order.getOrderVideos().stream()
                .filter(orderVideo -> orderVideo.getVideo().equals(video))
                .findFirst()
                .orElseThrow();
    }


    private Member createMember() {

        return Member.builder()
                .email("email")
                .password("password")
                .reward(1000)
                .build();
    }

    private Video createVideo(){
        return Video.builder()
                .videoName("title")
                .description("description")
                .thumbnailFile("thumbnailFile")
                .videoFile("videoFile")
                .view(0)
                .star(0.0F)
                .price(1000)
                .build();
    }

    private Video createVideo(int price){
        return Video.builder()
                .videoName("title")
                .description("description")
                .thumbnailFile("thumbnailFile")
                .videoFile("videoFile")
                .view(0)
                .star(0.0F)
                .price(price)
                .build();
    }

    private Video createVideo(String title) {
        return Video.builder()
                .videoName(title)
                .description("description")
                .thumbnailFile("thumbnailFile")
                .videoFile("videoFile")
                .view(0)
                .star(0.0F)
                .price(1000)
                .build();
    }

    private Order createOrder(Member member, List<Video> videos, int usingReward){

        Order order = Order.builder()
                .member(member)
                .totalPayAmount(videos.stream().mapToInt(Video::getPrice).sum() - usingReward)
                .remainRefundAmount(videos.stream().mapToInt(Video::getPrice).sum() - usingReward)
                .reward(usingReward)
                .remainRefundReward(usingReward)
                .orderStatus(OrderStatus.ORDERED)
                .orderVideos(new ArrayList<>())
                .build();

        for(Video video : videos){
            OrderVideo orderVideo = OrderVideo.createOrderVideo(order, video, video.getPrice());
            order.addOrderVideo(orderVideo);
        }

        return order;
    }

    private Order createOrderComplete(Member member, List<Video> videos, int usingReward){

        Order order = Order.builder()
                .member(member)
                .totalPayAmount(videos.stream().mapToInt(Video::getPrice).sum() - usingReward)
                .remainRefundAmount(videos.stream().mapToInt(Video::getPrice).sum() - usingReward)
                .reward(usingReward)
                .remainRefundReward(usingReward)
                .orderStatus(OrderStatus.COMPLETED)
                .orderVideos(new ArrayList<>())
                .build();

        for(Video video : videos){
            OrderVideo orderVideo = OrderVideo.createOrderVideo(order, video, video.getPrice());
            order.addOrderVideo(orderVideo);
        }

        return order;
    }

    private Order createOrderCancel(Member member, List<Video> videos, int usingReward){

        Order order = Order.builder()
                .member(member)
                .totalPayAmount(videos.stream().mapToInt(Video::getPrice).sum() - usingReward)
                .remainRefundAmount(videos.stream().mapToInt(Video::getPrice).sum() - usingReward)
                .reward(usingReward)
                .remainRefundReward(usingReward)
                .orderStatus(OrderStatus.CANCELED)
                .orderVideos(new ArrayList<>())
                .build();

        for(Video video : videos){
            OrderVideo orderVideo = OrderVideo.createOrderVideo(order, video, video.getPrice());
            order.addOrderVideo(orderVideo);
        }

        return order;
    }
}