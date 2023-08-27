package com.server.domain.member.repository;

import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.dto.MemberVideoResponse;
import com.server.domain.order.entity.Order;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.RepositoryTest;
import org.assertj.core.api.Assertions;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class MemberRepositoryTest extends RepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Test
    @DisplayName("회원이 비디오를 구매한 적이 있는지 확인한다.")
    void checkMemberPurchaseVideo() {
        // given
        Member member = createAndSaveMember();
        Video video = createAndSaveVideo();
        Order order = createAndSaveOrder(member, List.of(video));

        em.flush();
        em.clear();

        // when
        Boolean isPurchased = memberRepository.checkMemberPurchaseVideo(member.getMemberId(), video.getVideoId());

        // then
        assertThat(isPurchased).isTrue();
    }

    @Test
    @DisplayName("회원이 비디오를 구매한 적이 있는지 확인한다. 없으면 false 를 반환한다.")
    void checkMemberPurchaseVideoFalse() {
        // given
        Member member = createAndSaveMember();
        Video video = createAndSaveVideo();

        em.flush();
        em.clear();

        // when
        Boolean isPurchased = memberRepository.checkMemberPurchaseVideo(member.getMemberId(), video.getVideoId());

        // then
        assertThat(isPurchased).isFalse();
    }

    @Test
    @DisplayName("회원이 주문을 했다가 취소한 경우에 구매한 적이 없다고 판단한다.")
    void checkMemberPurchaseVideoOrderCancel() {
        //given
        Member member = createAndSaveMember();
        Video video = createAndSaveVideo();
        Order order = createAndSaveOrder(member, List.of(video));

        order.deleteOrder();

        em.flush();
        em.clear();

        //when
        Boolean isPurchased = memberRepository.checkMemberPurchaseVideo(member.getMemberId(), video.getVideoId());

        //then
        assertThat(isPurchased).isFalse();
    }

    @Test
    @DisplayName("회원 id 로 회원이 구매한 비디오를 모두 조회한다.")
    void getMemberPurchaseVideo() {
        // given
        Member member = createAndSaveMember();
        Video video1 = createAndSaveVideo();
        Video video2 = createAndSaveVideo();
        Video video3 = createAndSaveVideo(); // 구매안한 비디오

        Order order = createAndSaveOrder(member, List.of(video1, video2));

        em.flush();
        em.clear();

        // when
        List<MemberVideoResponse> memberPurchaseVideo = memberRepository.getMemberPurchaseVideo(member.getMemberId());

        // then
        assertThat(memberPurchaseVideo).hasSize(2)
                .extracting("videoId").containsExactlyInAnyOrder(video1.getVideoId(), video2.getVideoId());

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

        em.persist(video);

        return video;
    }

    private Order createAndSaveOrder(Member member, List<Video> video) {

        Order order = Order.createOrder(member, video, 500);

        em.persist(order);

        return order;
    }
}