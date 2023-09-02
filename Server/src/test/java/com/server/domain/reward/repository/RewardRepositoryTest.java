package com.server.domain.reward.repository;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.question.entity.Question;
import com.server.domain.reward.entity.Reward;
import com.server.domain.reward.entity.RewardType;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.RepositoryTest;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

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
        Question question = createAndSaveQuestion(video);

        Member member = createAndSaveMember();

        Reward reward1 = createAndSaveVideoReward(member, video);
        Reward reward2 = createAndSaveQuestionReward(member, question);

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
        Question question = createAndSaveQuestion(video1);

        Member member = createAndSaveMember();

        Order order = createAndSaveOrderComplete(member, List.of(video1, video2));

        Reward reward1 = createAndSaveVideoReward(member, video1);
        Reward reward2 = createAndSaveQuestionReward(member, question);
        Reward reward3 = createAndSaveVideoReward(member, video2);

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

    @Test
    @DisplayName("member 와 question 으로 Reward 를 찾는다.")
    void findByQuestionAndMember() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);
        Question question = createAndSaveQuestion(video1);

        Member member = createAndSaveMember();

        Order order = createAndSaveOrderComplete(member, List.of(video1, video2));

        Reward reward = createAndSaveQuestionReward(member, question);

        em.flush();
        em.clear();

        //when
        Reward findReward = rewardRepository.findByQuestionAndMember(question, member).orElseThrow();

        //then
        assertThat(findReward.getRewardId()).isEqualTo(reward.getRewardId());
    }

    @Test
    @DisplayName("member 와 question 으로 Reward 를 찾을 때 취소되었으면 반환하지 않는다.")
    void findByQuestionAndMemberCanceled() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);
        Question question = createAndSaveQuestion(video1);

        Member member = createAndSaveMember();

        Order order = createAndSaveOrderComplete(member, List.of(video1, video2));

        Reward reward = createAndSaveQuestionReward(member, question);
        reward.cancelReward();

        em.flush();
        em.clear();

        //when
        Optional<Reward> findReward = rewardRepository.findByQuestionAndMember(question, member);

        //then
        assertThat(findReward.isPresent()).isFalse();
    }

    @Test
    @DisplayName("member 와 Question List 로 모든 Reward 를 찾는다.")
    void findByQuestionsAndMember() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video = createAndSaveVideo(channel);
        Question question1 = createAndSaveQuestion(video);
        Question question2 = createAndSaveQuestion(video);
        Question question3 = createAndSaveQuestion(video);
        List<Question> questions = List.of(question1, question2, question3);

        Member member = createAndSaveMember();

        Order order = createAndSaveOrderComplete(member, List.of(video));

        Reward reward1 = createAndSaveQuestionReward(member, question1);
        Reward reward2 = createAndSaveQuestionReward(member, question2);
        Reward reward3 = createAndSaveQuestionReward(member, question3);

        em.flush();
        em.clear();

        //when
        List<Reward> findRewards = rewardRepository.findByQuestionsAndMember(questions, member);

        //then
        assertThat(findRewards).hasSize(3)
                .extracting("rewardId")
                .containsExactlyInAnyOrder(reward1.getRewardId(), reward2.getRewardId(), reward3.getRewardId());
    }

    @Test
    @DisplayName("member 와 Question List 로 모든 Reward 를 찾는다. reward 가 취소되었으면 반환하지 않는다.")
    void findByQuestionsAndMemberCanceled() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video = createAndSaveVideo(channel);
        Question question1 = createAndSaveQuestion(video);
        Question question2 = createAndSaveQuestion(video);
        Question question3 = createAndSaveQuestion(video);
        List<Question> questions = List.of(question1, question2, question3);

        Member member = createAndSaveMember();

        Order order = createAndSaveOrderComplete(member, List.of(video));

        Reward reward1 = createAndSaveQuestionReward(member, question1);
        reward1.cancelReward();
        Reward reward2 = createAndSaveQuestionReward(member, question2);
        reward2.cancelReward();
        Reward reward3 = createAndSaveQuestionReward(member, question3);
        reward3.cancelReward();

        em.flush();
        em.clear();

        //when
        List<Reward> findRewards = rewardRepository.findByQuestionsAndMember(questions, member);

        //then
        assertThat(findRewards).isEmpty();
    }
}