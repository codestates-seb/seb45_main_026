package com.server.domain.reward.repository;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.reward.entity.Reward;
import com.server.domain.reward.entity.RewardType;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.RepositoryTest;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class RewardRepositoryTest extends RepositoryTest {

    @Autowired RewardRepository rewardRepository;

    @Test
    @DisplayName("member 와 video 로 reward 리스트를 찾는다.")
    void findByMemberAndVideo() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video = createAndSaveVideo(channel);

        Member member = createAndSaveMember();

        Reward reward1 = createAndSaveVideoReward(member, video, RewardType.VIDEO);
        Reward reward2 = createAndSaveVideoReward(member, video, RewardType.QUIZ);

        em.flush();
        em.clear();

        //when
        List<Reward> rewards = rewardRepository.findByMemberAndVideo(member, video);

        //then
        assertThat(rewards).hasSize(2)
                .extracting("rewardId")
                .containsExactlyInAnyOrder(reward1.getRewardId(), reward2.getRewardId());
    }

    @Test
    @DisplayName("주문번호로 reward 리스트를 찾는다. reward 의 member 는 초기화되어있다.")
    void findByOrderId() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        Member member = createAndSaveMember();

        Order order = createAndSaveOrderComplete(member, List.of(video1, video2));

        Reward reward1 = createAndSaveVideoReward(member, video1, RewardType.VIDEO);
        Reward reward2 = createAndSaveVideoReward(member, video1, RewardType.QUIZ);
        Reward reward3 = createAndSaveVideoReward(member, video2, RewardType.VIDEO);

        em.flush();
        em.clear();

        //when
        List<Reward> rewards = rewardRepository.findByOrderId(order.getOrderId());

        //then
        assertThat(rewards).hasSize(3)
                .extracting("rewardId")
                .containsExactlyInAnyOrder(reward1.getRewardId(), reward2.getRewardId(), reward3.getRewardId());

        for (Reward reward : rewards) {
            assertThat(Hibernate.isInitialized(reward.getMember())).isTrue();
        }
    }
}