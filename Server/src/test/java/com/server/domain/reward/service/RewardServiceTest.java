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
    void createQuestionReward() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Video video = createAndSaveVideo(channel);
        Question question = createAndSaveQuestion(video);

        Member member = createAndSaveMember();

        //when
        rewardService.createQuestionReward(question, member);

        //then
        Reward reward = rewardRepository.findAll().get(0);
        assertThat(reward.getMember()).isEqualTo(member);
        assertThat(reward.getVideo()).isEqualTo(video);
        assertThat(reward.getQuestion()).isEqualTo(question);
        assertThat(reward.getRewardType()).isEqualTo(RewardType.QUIZ);
        assertThat(reward.getRewardPoint()).isEqualTo(rewardService.getQuestionRewardPolicy());
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

        Order order = createAndSaveOrderWithPurchaseComplete(member, List.of(video), 0);
        Reward reward1 = createAndSaveVideoReward(member, video);
        Reward reward2 = createAndSaveQuestionReward(member, question);

        //when
        rewardService.cancelReward(order);

        //then
        assertThat(member.getReward()).isEqualTo(currentPoint - reward1.getRewardPoint() - reward2.getRewardPoint());
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