package com.server.domain.order.entity;

import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

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

        //when
        Order order = Order.createOrder(member, List.of(video1, video2), usingReward);

        //then
        assertThat(order.getPrice()).isEqualTo(video1.getPrice() + video2.getPrice() - usingReward);
        assertThat(order.getMember()).isEqualTo(member);
        assertThat(order.getOrderVideos()).hasSize(2)
                .extracting("video").containsExactly(video1, video2);
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
    @DisplayName("order 를 환불하면 member 의 reward 가 다시 적립된다.")
    void refund() {
        //given
        int reward = 1000;
        int usingReward = 500;
        Member member = createMember(reward);
        Video video1 = createVideo("title1");
        Video video2 = createVideo("title2");

        Order order = createOrder(member, List.of(video1, video2), usingReward);

        //when
        order.refund();

        //then
        assertThat(member.getReward()).isEqualTo(reward + usingReward);
    }

    private Member createMember() {

        return Member.builder()
                .email("email")
                .password("password")
                .reward(1000)
                .build();
    }

    private Member createMember(int reward) {

        return Member.builder()
                .email("email")
                .password("password")
                .reward(reward)
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
                .reward(usingReward)
                .orderStatus(OrderStatus.ORDERED)
                .orderVideos(new ArrayList<>())
                .build();

        for(Video video : videos){
            OrderVideo orderVideo = OrderVideo.createOrderVideo(order, video, video.getPrice());
            order.addOrderVideo(orderVideo);
        }

        return order;
    }
}