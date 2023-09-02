package com.server.domain.reward.service;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.question.entity.Question;
import com.server.domain.reward.entity.Reward;
import com.server.domain.reward.entity.RewardType;
import com.server.domain.video.entity.Video;
import com.server.global.exception.businessexception.orderexception.RewardNotEnoughException;
import com.server.global.testhelper.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class RewardServiceTest extends ServiceTest {

    @Autowired RewardService rewardService;

    @Test
    @DisplayName("비디오를 통해 리워드를 생성한다.")
    void createVideoReward() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Video video = createAndSaveVideo(channel);

        Member member = createAndSaveMember();

        //when
        rewardService.createVideoReward(video, member);

        //then
        Reward reward = rewardRepository.findAll().get(0);
        assertThat(reward.getMember()).isEqualTo(member);
        assertThat(reward.getVideo()).isEqualTo(video);
        assertThat(reward.getRewardType()).isEqualTo(RewardType.VIDEO);
        assertThat(reward.getRewardPoint()).isEqualTo((int) (video.getPrice() * rewardService.getVideoRewardPolicy()));
    }

    @Test
    @DisplayName("문제를 통해 리워드를 생성한다.")
    void createQuestionRewardIfNotPresent() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Video video = createAndSaveVideo(channel);
        Question question = createAndSaveQuestion(video);

        Member member = createAndSaveMember();

        //when
        rewardService.createQuestionRewardIfNotPresent(question, member);

        //then
        Reward reward = rewardRepository.findAll().get(0);
        assertThat(reward.getMember()).isEqualTo(member);
        assertThat(reward.getVideo()).isEqualTo(video);
        assertThat(reward.getQuestion()).isEqualTo(question);
        assertThat(reward.getRewardType()).isEqualTo(RewardType.QUIZ);
        assertThat(reward.getRewardPoint()).isEqualTo(rewardService.getQuestionRewardPolicy());
    }

    @Test
    @DisplayName("문제를 통해 리워드를 생성할 때 이미 리워드가 존재하면 생성하지 않는다.")
    void createQuestionRewardIfPresent() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video = createAndSaveVideo(channel);
        Question question = createAndSaveQuestion(video);

        Member member = createAndSaveMember();
        Reward reward = createAndSaveQuestionReward(member, question);

        //when
        rewardService.createQuestionRewardIfNotPresent(question, member);

        //then
        assertThat(rewardRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("문제를 통해 리워드를 생성할 때 이미 리워드가 존재하더라도 취소된 리워드면 생성한다.")
    void createQuestionRewardIfPresentButCanceled() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video = createAndSaveVideo(channel);
        Question question = createAndSaveQuestion(video);

        Member member = createAndSaveMember();
        Reward reward = createAndSaveQuestionReward(member, question);
        reward.cancelReward();

        //when
        rewardService.createQuestionRewardIfNotPresent(question, member);

        //then
        assertThat(rewardRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("문제 리스트를 통해 리워드를 생성한다.")
    void createQuestionRewardsIfNotPresent() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Video video = createAndSaveVideo(channel);
        Question question1 = createAndSaveQuestion(video);
        Question question2 = createAndSaveQuestion(video);
        List<Question> questions = List.of(question1, question2);

        Member member = createAndSaveMember();

        //when
        rewardService.createQuestionRewardsIfNotPresent(questions, member);

        //then
        List<Reward> reward = rewardRepository.findAll();
        assertThat(reward).hasSize(2)
                .extracting("question").containsOnly(question1, question2);
        assertThat(reward).extracting("rewardType").containsOnly(RewardType.QUIZ);
        assertThat(reward).extracting("rewardPoint").containsOnly(rewardService.getQuestionRewardPolicy());
    }

    @Test
    @DisplayName("문제 리스트를 통해 리워드를 생성할 때 이미 리워드가 존재하면 생성하지 않는다.")
    void createQuestionRewardsIfPresent() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Video video = createAndSaveVideo(channel);
        Question question1 = createAndSaveQuestion(video);
        Question question2 = createAndSaveQuestion(video);
        List<Question> questions = List.of(question1, question2);

        Member member = createAndSaveMember();

        Reward reward1 = createAndSaveQuestionReward(member, question1);
        Reward reward2 = createAndSaveQuestionReward(member, question2);

        //when
        rewardService.createQuestionRewardsIfNotPresent(questions, member);

        //then
        assertThat(rewardRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("문제 리스트를 통해 리워드를 생성할 때 이미 리워드가 존재하지만 취소된 리워드면 추가로 생성한다.")
    void createQuestionRewardsIfPresentButCanceled() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Video video = createAndSaveVideo(channel);
        Question question1 = createAndSaveQuestion(video);
        Question question2 = createAndSaveQuestion(video);
        List<Question> questions = List.of(question1, question2);

        Member member = createAndSaveMember();

        Reward reward1 = createAndSaveQuestionReward(member, question1);
        reward1.cancelReward();
        Reward reward2 = createAndSaveQuestionReward(member, question2);
        reward2.cancelReward();

        //when
        rewardService.createQuestionRewardsIfNotPresent(questions, member);

        //then
        assertThat(rewardRepository.findAll().size()).isEqualTo(4);
    }

    @Test
    @DisplayName("order 를 통해 리워드를 취소하고 member 의 reward 를 변경한다.")
    void cancelReward() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video = createAndSaveVideo(channel);
        Question question = createAndSaveQuestion(video);

        Member member = createAndSaveMember();
        int currentPoint = member.getReward();

        Order order = createAndSaveOrderWithPurchaseComplete(member, List.of(video), 100);
        Reward reward1 = createAndSaveVideoReward(member, video);
        Reward reward2 = createAndSaveQuestionReward(member, question);

        //when
        rewardService.cancelReward(order);

        //then
        assertThat(member.getReward()).isEqualTo(currentPoint + 100);
        assertThat(reward1.isCanceled()).isTrue();
        assertThat(reward2.isCanceled()).isTrue();
    }

    @Test
    @DisplayName("order 를 통해 리워드 취소 시 reward 가 부족하면 RewardNotEnoughException 예외가 발생한다.")
    void cancelRewardRewardNotEnoughException() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Video video = createAndSaveVideo(channel);
        Question question = createAndSaveQuestion(video);

        Member member = createAndSaveMember();

        Order order = createAndSaveOrderWithPurchaseComplete(member, List.of(video), 0);
        createAndSaveVideoReward(member, video);
        createAndSaveQuestionReward(member, question);

        member.minusReward(member.getReward()); // 리워드를 0 으로 만든다.

        //when & then
        assertThatThrownBy(() -> rewardService.cancelReward(order))
                .isInstanceOf(RewardNotEnoughException.class);
    }
}