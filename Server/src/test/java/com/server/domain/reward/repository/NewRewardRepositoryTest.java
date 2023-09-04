package com.server.domain.reward.repository;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.question.entity.Question;
import com.server.domain.reward.entity.NewReward;
import com.server.domain.reward.entity.Rewardable;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.RepositoryTest;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NewRewardRepositoryTest extends RepositoryTest {

    @Autowired
    NewRewardRepository newRewardRepository;

    protected NewReward createAndSaveReward(Member member, Rewardable rewardable) {

        NewReward reward = NewReward.createReward(10, member, rewardable);

        em.persist(reward);

        return reward;
    }

    @Test
    @DisplayName("주문번호로 reward 리스트를 찾는다. reward 의 member 는 초기화되어있다.")
    void findByOrderId() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);
        Question question = createAndSaveQuestion(video1);

        Member member = createAndSaveMember();

        Order order = createAndSaveOrderComplete(member, List.of(video1, video2));

        NewReward reward1 = createAndSaveReward(member, video1);
        NewReward reward2 = createAndSaveReward(member, question);
        NewReward reward3 = createAndSaveReward(member, video2);

        em.flush();
        em.clear();

        //when
        List<NewReward> rewards = newRewardRepository.findByOrderId(order.getOrderId());

        //then
        assertThat(rewards).hasSize(3)
                .extracting("rewardId")
                .containsExactlyInAnyOrder(reward1.getRewardId(), reward2.getRewardId(), reward3.getRewardId());

        for (NewReward reward : rewards) {
            assertThat(Hibernate.isInitialized(reward.getMember())).isTrue();
        }
    }
}