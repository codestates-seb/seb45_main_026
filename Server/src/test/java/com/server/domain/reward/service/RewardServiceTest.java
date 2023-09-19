package com.server.domain.reward.service;

import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderVideo;
import com.server.domain.question.entity.Question;
import com.server.domain.reward.entity.*;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

class RewardServiceTest extends ServiceTest {

    @Autowired RewardService rewardService;

    @Test
    @DisplayName("비디오를 통해 리워드를 생성한다.")
    void createVideoReward() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());

        Member loginMember = createMemberWithChannel();

        //when
        rewardService.createRewardIfNotPresent(video, loginMember);

        //then
        VideoReward reward = (VideoReward) rewardRepository.findAll().get(0);

        assertThat(reward.getMember()).isEqualTo(loginMember);
        assertThat(reward.getVideo()).isEqualTo(video);
        assertThat(reward.getRewardType()).isEqualTo(RewardType.VIDEO);
        assertThat(reward.getRewardPoint()).isEqualTo(video.getRewardPoint());
    }

    @Test
    @DisplayName("비디오를 통해 리워드를 생성하면 member 의 리워드가 추가된다.")
    void createVideoRewardAddReward() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());

        Member loginMember = createMemberWithChannel();
        int beforeReward = loginMember.getReward();

        //when
        rewardService.createRewardIfNotPresent(video, loginMember);

        //then
        assertThat(loginMember.getReward()).isEqualTo(beforeReward + video.getRewardPoint());
    }

    @Test
    @DisplayName("문제를 통해 리워드를 생성한다.")
    void createQuestionRewardIfNotPresent() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        Question question = createAndSaveQuestion(video);

        Member loginMember = createMemberWithChannel();

        //when
        rewardService.createRewardIfNotPresent(question, loginMember);

        //then
        Reward reward = rewardRepository.findAll().get(0);

        assertThat(reward instanceof QuestionReward).isTrue();
        QuestionReward questionReward = (QuestionReward) reward;

        assertThat(questionReward.getMember()).isEqualTo(loginMember);
        assertThat(questionReward.getVideo()).isEqualTo(video);
        assertThat(questionReward.getQuestion()).isEqualTo(question);
        assertThat(questionReward.getRewardType()).isEqualTo(RewardType.QUIZ);
        assertThat(questionReward.getRewardPoint()).isEqualTo(question.getRewardPoint());
    }

    @Test
    @DisplayName("문제를 통해 리워드를 생성하면 member 의 리워드가 추가된다.")
    void createQuestionRewardIfNotPresentAddReward() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        Question question = createAndSaveQuestion(video);

        Member loginMember = createMemberWithChannel();
        int beforeReward = loginMember.getReward();

        //when
        rewardService.createRewardIfNotPresent(question, loginMember);

        //then
        assertThat(loginMember.getReward()).isEqualTo(beforeReward + question.getRewardPoint());
    }

    @Test
    @DisplayName("문제를 통해 리워드를 생성할 때 이미 리워드가 존재하면 생성하지 않는다.")
    void createQuestionRewardIfPresent() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        Question question = createAndSaveQuestion(video);

        Member loginMember = createAndSaveMember();
        createAndSaveReward(loginMember, question); // 이미 리워드가 존재하는 상태

        //when
        rewardService.createRewardIfNotPresent(question, loginMember);

        //then
        assertThat(rewardRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("문제를 통해 리워드를 생성할 때 이미 리워드가 존재하더라도 취소된 리워드면 생성한다.")
    void createQuestionRewardIfPresentButCanceled() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        Question question = createAndSaveQuestion(video);

        Member loginMember = createAndSaveMember();
        Reward reward = createAndSaveReward(loginMember, question);
        reward.cancelReward();

        //when
        rewardService.createRewardIfNotPresent(question, loginMember);

        //then
        assertThat(rewardRepository.findAll().size()).isEqualTo(2);
    }


    @Test
    @DisplayName("문제 리스트를 통해 리워드를 생성한다.")
    void createQuestionRewardsIfNotPresent() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
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
        assertThat(reward).extracting("rewardPoint").containsOnly(question1.getRewardPoint());
    }

    @Test
    @DisplayName("문제 리스트를 통해 리워드를 생성하면 member 의 리워드가 추가된다.")
    void createQuestionRewardsIfNotPresentAddReward() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        Question question1 = createAndSaveQuestion(video);
        Question question2 = createAndSaveQuestion(video);
        List<Question> questions = List.of(question1, question2);

        Member member = createMemberWithChannel();
        int beforeReward = member.getReward();

        //when
        rewardService.createQuestionRewardsIfNotPresent(questions, member);

        //then
        int totalReward = question1.getRewardPoint() + question2.getRewardPoint();

        assertThat(member.getReward()).isEqualTo(beforeReward + totalReward);
    }

    @TestFactory
    @DisplayName("문제 리스트를 통해 리워드를 생성할 때 이미 리워드가 존재하면 생성하지 않는다.")
    Collection<DynamicTest> createQuestionRewardsIfPresent() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        Question question1 = createAndSaveQuestion(video);
        Question question2 = createAndSaveQuestion(video);
        List<Question> questions = List.of(question1, question2);

        Member member = createAndSaveMember();

        return List.of(
                dynamicTest("최초 생성 시 리워드가 생성된다.", ()-> {
                    //when
                    rewardService.createQuestionRewardsIfNotPresent(questions, member);

                    //then
                    assertThat(rewardRepository.findAll().size()).isEqualTo(2);
                }),
                dynamicTest("두번째 생성 요청 시 리워드가 생성되지 않는다.", ()-> {
                    //when
                    rewardService.createQuestionRewardsIfNotPresent(questions, member);

                    //then
                    assertThat(rewardRepository.findAll().size()).isEqualTo(2);
                })
        );
    }

    @Test
    @DisplayName("문제 리스트를 통해 리워드를 생성할 때 이미 리워드가 존재하지만 취소된 리워드면 추가로 생성한다.")
    void createQuestionRewardsIfPresentButCanceled() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        Question question1 = createAndSaveQuestion(video);
        Question question2 = createAndSaveQuestion(video);
        List<Question> questions = List.of(question1, question2);

        Member loginMember = createMemberWithChannel();

        Reward reward1 = createAndSaveReward(loginMember, question1);
        reward1.cancelReward();
        Reward reward2 = createAndSaveReward(loginMember, question2);
        reward2.cancelReward();

        //when
        rewardService.createQuestionRewardsIfNotPresent(questions, loginMember);

        //then
        assertThat(rewardRepository.findAll().size()).isEqualTo(4);
    }

    @Test
    @DisplayName("order 를 통해 리워드를 취소하고 member 의 reward 를 변경한다.")
    void cancelReward() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        Question question = createAndSaveQuestion(video);

        Member loginMember = createMemberWithChannel();

        Reward reward1 = createAndSaveReward(loginMember, video);
        Reward reward2 = createAndSaveReward(loginMember, question);

        Order order = createAndSaveOrderWithPurchaseComplete(loginMember, List.of(video), 100);

        //when
        rewardService.cancelOrderReward(order);

        //then
        assertThat(reward1.isCanceled()).isTrue();
        assertThat(reward2.isCanceled()).isTrue();
    }

    @Test
    @DisplayName("order 를 통해 리워드를 취소하면 member Reward 적립이 취소되어 감소한다.")
    void cancelRewardMinusReward() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        Question question = createAndSaveQuestion(video);

        Member loginMember = createMemberWithChannel();

        createAndSaveReward(loginMember, video);
        createAndSaveReward(loginMember, question);

        Order order = createAndSaveOrderWithPurchaseComplete(loginMember, List.of(video), 100);

        int currentPoint = loginMember.getReward();

        //when
        rewardService.cancelOrderReward(order);

        //then
        int totalCancelReward = video.getRewardPoint() + question.getRewardPoint();

        assertThat(loginMember.getReward()).isEqualTo(currentPoint - totalCancelReward);
    }

    @Test
    @DisplayName("order 를 통해 리워드 취소 시 reward 가 부족하면 order 에서 차감해 사용한다.")
    void cancelRewardRewardNotEnoughException() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        Question question = createAndSaveQuestion(video);

        Member loginMember = createMemberWithChannel();

        Order order = createAndSaveOrderWithPurchaseComplete(loginMember, List.of(video), 100);
        createAndSaveReward(loginMember, video);
        createAndSaveReward(loginMember, question);

        loginMember.minusReward(loginMember.getReward()); // 리워드를 0 으로 만든다.

        int currentPoint = order.getRemainRefundReward();

        //when
        rewardService.cancelOrderReward(order);

        //then
        int totalCancelReward = video.getRewardPoint() + question.getRewardPoint();

        assertThat(order.getRemainRefundReward()).isEqualTo(currentPoint - totalCancelReward);
    }

    @Test
    @DisplayName("orderVideo 를 통해 해당 비디오에 관련된 리워드를 취소한다.")
    void cancelVideoReward() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        Question question = createAndSaveQuestion(video);

        Member member = createMemberWithChannel();

        Order order = createAndSaveOrderWithPurchaseComplete(member, List.of(video), 0);
        OrderVideo orderVideo = order.getOrderVideos().get(0);
        Reward reward1 = createAndSaveReward(member, video);
        Reward reward2 = createAndSaveReward(member, question);

        //when
        rewardService.cancelVideoReward(orderVideo);

        //then
        assertThat(reward1.isCanceled()).isTrue();
        assertThat(reward2.isCanceled()).isTrue();
    }

    @Test
    @DisplayName("orderVideo 를 통해 해당 비디오에 관련된 리워드를 취소하면 member 의 리워드가 감소한다.")
    void cancelVideoRewardMinusReward() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        Question question = createAndSaveQuestion(video);

        Member member = createMemberWithChannel();

        Order order = createAndSaveOrderWithPurchaseComplete(member, List.of(video), 0);
        OrderVideo orderVideo = order.getOrderVideos().get(0);
        createAndSaveReward(member, video);
        createAndSaveReward(member, question);

        int currentPoint = member.getReward();

        //when
        rewardService.cancelVideoReward(orderVideo);

        //then
        int totalCancelReward = video.getRewardPoint() + question.getRewardPoint();

        assertThat(member.getReward()).isEqualTo(currentPoint - totalCancelReward);
    }

    @Test
    @DisplayName("orderVideo 를 통해 해당 비디오에 관련된 리워드를 취소할 때 이미 취소된 리워드가 있으면 해당 리워드는 제외한다.")
    void cancelVideoRewardMinusRewardPartially() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        Question question = createAndSaveQuestion(video);

        Member loginMember = createMemberWithChannel();

        Order order = createAndSaveOrderWithPurchaseComplete(loginMember, List.of(video), 0);
        OrderVideo orderVideo = order.getOrderVideos().get(0);
        createAndSaveReward(loginMember, video);
        Reward canceledReward = createAndSaveReward(loginMember, question);
        canceledReward.cancelReward(); // question 리워드는 이미 취소된 상태

        int currentPoint = loginMember.getReward();

        //when
        rewardService.cancelVideoReward(orderVideo);

        //then
        assertThat(loginMember.getReward()).isEqualTo(currentPoint - video.getRewardPoint());
    }

    @Test
    @DisplayName("orderVideo 를 통해 비디오 리워드를 취소할 때 환불할 리워드가 부족하면 order 에서 차감해 사용한다.")
    void cancelVideoRewardEnoughInOrder() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        Question question = createAndSaveQuestion(video);

        Member loginMember = createMemberWithChannel();

        Order order = createAndSaveOrderWithPurchaseComplete(loginMember, List.of(video), 100);
        OrderVideo orderVideo = order.getOrderVideos().get(0);
        createAndSaveReward(loginMember, video);
        createAndSaveReward(loginMember, question);

        loginMember.minusReward(loginMember.getReward()); // 리워드 소멸

        //when
        rewardService.cancelVideoReward(orderVideo);

        //then
        int totalCancelReward = video.getRewardPoint() + question.getRewardPoint();
        assertThat(order.getRemainRefundReward()).isEqualTo(100 - totalCancelReward);
    }

    @Test
    @DisplayName("orderVideo 를 통해 비디오 리워드를 취소할 때 환불할 리워드가 일부 부족하면 order 에서 일부 차감해 사용한다.")
    void cancelVideoRewardEnoughInOrderPartially() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        Question question = createAndSaveQuestion(video);

        Member loginMember = createMemberWithChannel();

        int orderReward = 100;
        Order order = createAndSaveOrderWithPurchaseComplete(loginMember, List.of(video), orderReward);
        OrderVideo orderVideo = order.getOrderVideos().get(0);
        createAndSaveReward(loginMember, video);
        createAndSaveReward(loginMember, question);

        loginMember.minusReward(loginMember.getReward() - 10); // 10원만 남기고 리워드 소멸
        int beforeMemberReward = loginMember.getReward();

        //when
        rewardService.cancelVideoReward(orderVideo);

        //then
        int totalCancelReward = video.getRewardPoint() + question.getRewardPoint();
        int totalLackReward = totalCancelReward - beforeMemberReward;
        assertThat(order.getRemainRefundReward()).isEqualTo(orderReward - totalLackReward);
    }

    @Test
    @DisplayName("orderVideo 를 통해 비디오 리워드를 취소할 때 환불할 리워드가 order 에서도 부족하면 환불 금액에서 차감한다.")
    void cancelVideoRewardNotEnough() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        Question question = createAndSaveQuestion(video);

        Member member = createMemberWithChannel();

        Order order = createAndSaveOrderWithPurchaseComplete(member, List.of(video), 0);
        OrderVideo orderVideo = order.getOrderVideos().get(0);
        int orderPrice = order.getRemainRefundAmount();
        createAndSaveReward(member, video);
        createAndSaveReward(member, question);

        member.minusReward(member.getReward()); // 리워드 소멸

        //when
        rewardService.cancelVideoReward(orderVideo);

        //then
        int totalCancelReward = video.getRewardPoint() + question.getRewardPoint();
        assertThat(order.getRemainRefundAmount()).isEqualTo(orderPrice - totalCancelReward);
    }
}