package com.server.domain.member.repository;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.dto.MemberVideoData;
import com.server.domain.order.entity.Order;
import com.server.domain.subscribe.entity.Subscribe;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.RepositoryTest;
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
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);
        Order order = createAndSaveOrder(member, List.of(video));
        order.completeOrder();
        em.persist(order);

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
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

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
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);
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
    @DisplayName("videoId 목록으로 특정 멤버가 구매했는지 확인한다.")
    void checkMemberPurchaseVideos() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);
        Video video3 = createAndSaveVideo(channel);

        Member member = createAndSaveMember();
        createAndSaveOrderComplete(member, List.of(video1, video2)); //video1, video2 구매

        List<Long> videoIds = List.of(video1.getVideoId(), video2.getVideoId(), video3.getVideoId());

        em.flush();
        em.clear();

        //when (video1, 2, 3 을 구매했는지 확인)
        List<Boolean> isPurchased = memberRepository.checkMemberPurchaseVideos(member.getMemberId(), videoIds);

        //then
        assertThat(isPurchased).hasSize(3)
                .containsExactlyInAnyOrder(true, true, false);

    }

    @Test
    @DisplayName("회원 id 로 회원이 구매한 비디오를 모두 조회한다.")
    void getMemberPurchaseVideo() {
        // given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);
        Video video3 = createAndSaveVideo(channel); // 구매안한 비디오

        Order order = createAndSaveOrder(member, List.of(video1, video2));

        em.flush();
        em.clear();

        // when
        List<MemberVideoData> memberPurchaseVideo = memberRepository.getMemberPurchaseVideo(member.getMemberId());

        // then
        assertThat(memberPurchaseVideo).hasSize(2)
                .extracting("videoId").containsExactlyInAnyOrder(video1.getVideoId(), video2.getVideoId());
    }

    @Test
    @DisplayName("회원의 특정 채널 구독 여부를 리스트 형태로 조회한다.")
    void checkMemberSubscribeChannel() {
        //given
        Member member1 = createAndSaveMember();
        Member member2 = createAndSaveMember();
        Member member3 = createAndSaveMember();
        Channel channel1 = createAndSaveChannel(member1);
        Channel channel2 = createAndSaveChannel(member2);
        Channel channel3 = createAndSaveChannel(member3);

        Member member = createAndSaveMember(); // member1  은 channel1, 2 를 구독
        createAndSaveSubscribe(member, channel1);
        createAndSaveSubscribe(member, channel2);

        //구독을 확인할 memberId
        List<Long> memberIds = List.of(member1.getMemberId(), member2.getMemberId(), member3.getMemberId());

        em.flush();
        em.clear();

        //when
        List<Boolean> isSubscribed = memberRepository.checkMemberSubscribeChannel(member.getMemberId(), memberIds);

        //then
        assertThat(isSubscribed).hasSize(3)
                .containsExactly(true, true, false);
    }

    private void createAndSaveSubscribe(Member member, Channel channel) {
        Subscribe subscribe = Subscribe.builder()
                .member(member)
                .channel(channel)
                .build();

        em.persist(subscribe);
    }
}