package com.server.domain.order.entity;

import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
}