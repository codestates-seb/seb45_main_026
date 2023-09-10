package com.server.domain.reward.repository;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderVideo;
import com.server.domain.question.entity.Question;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reward.entity.NewReward;
import com.server.domain.reward.entity.QuestionReward;
import com.server.domain.reward.entity.Rewardable;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class NewRewardRepositoryTest extends RepositoryTest {

    @Autowired
    NewRewardRepository newRewardRepository;

    @Test
    @DisplayName("주문번호로 reward 리스트를 찾는다.")
    void findByOrderId() {
        //given
        Member owner = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());
        Question question = createAndSaveQuestion(video1);

        Member loginMember = createMemberWithChannel();

        Order order = createAndSaveOrderComplete(loginMember, List.of(video1, video2));

        Reply reply = createAndSaveReply(loginMember, video1);

        NewReward reward1 = createAndSaveReward(loginMember, video1);
        NewReward reward2 = createAndSaveReward(loginMember, question);
        NewReward reward3 = createAndSaveReward(loginMember, video2);
        NewReward reward4 = createAndSaveReward(loginMember, reply);

        em.flush();
        em.clear();

        //when
        List<NewReward> rewards = newRewardRepository.findByOrderId(order.getOrderId());

        //then
        assertThat(rewards).hasSize(4)
                .extracting("rewardId")
                .containsExactlyInAnyOrder(
                        reward1.getRewardId(),
                        reward2.getRewardId(),
                        reward3.getRewardId(),
                        reward4.getRewardId()
                );
    }

    @Test
    @DisplayName("주문번호로 reward 리스트를 찾을 때 취소된 비디오의 내역은 찾지 않는다.")
    void findByOrderIdWithCanceledVideo() {
        //given
        Member owner = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());
        Question question = createAndSaveQuestion(video1);

        Member loginMember = createMemberWithChannel();

        Order order = createAndSaveOrderComplete(loginMember, List.of(video1, video2));
        order.cancelVideoOrder(getOrderVideo(order, video2)); //video2 취소

        Reply reply = createAndSaveReply(loginMember, video1);

        NewReward reward1 = createAndSaveReward(loginMember, video1);
        NewReward reward2 = createAndSaveReward(loginMember, question);
        NewReward reward3 = createAndSaveReward(loginMember, video2);
        NewReward reward4 = createAndSaveReward(loginMember, reply);

        em.flush();
        em.clear();

        //when
        List<NewReward> rewards = newRewardRepository.findByOrderId(order.getOrderId());

        //then
        assertThat(rewards).hasSize(3)
                .extracting("rewardId")
                .containsExactlyInAnyOrder(
                        reward1.getRewardId(),
                        reward2.getRewardId(),
                        reward4.getRewardId()
                );
    }

    private OrderVideo getOrderVideo(Order order, Video video) {
        return order.getOrderVideos().stream()
                .filter(ov -> ov.getVideo().getVideoId().equals(video.getVideoId()))
                .findFirst()
                .orElseThrow();
    }

    @Test
    @DisplayName("member 와 question 으로 Reward 를 찾는다.")
    void findByQuestionAndMember() {
        //given
        Member owner = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());
        Question question = createAndSaveQuestion(video1);

        Member loginMember = createMemberWithChannel();

        createAndSaveOrderComplete(loginMember, List.of(video1, video2));

        NewReward reward = createAndSaveReward(loginMember, question);

        em.flush();
        em.clear();

        //when
        NewReward findReward = newRewardRepository.findByQuestionAndMember(question, loginMember).orElseThrow();

        //then
        assertThat(findReward.getRewardId()).isEqualTo(reward.getRewardId());
    }

    @Test
    @DisplayName("member 와 question 으로 Reward 를 찾을 때 Reward 가 취소되었으면 찾지 않는다.")
    void findByQuestionAndMemberQuestionRewardCanceled() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        Question question = createAndSaveQuestion(video);

        Member loginMember = createMemberWithChannel();

        createAndSaveOrderComplete(loginMember, List.of(video));

        NewReward reward = createAndSaveReward(loginMember, question);
        reward.cancelReward();

        em.flush();
        em.clear();

        //when
        Optional<QuestionReward> findReward = newRewardRepository.findByQuestionAndMember(question, loginMember);

        //then
        assertThat(findReward.isPresent()).isFalse();
    }

    @Test
    @DisplayName("member 와 Question List 로 모든 Reward 를 찾는다.")
    void findByQuestionsAndMember() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        Question question1 = createAndSaveQuestion(video);
        Question question2 = createAndSaveQuestion(video);
        Question question3 = createAndSaveQuestion(video);
        List<Question> questions = List.of(question1, question2, question3);

        Member loginMember = createMemberWithChannel();

        createAndSaveOrderComplete(loginMember, List.of(video));

        NewReward reward1 = createAndSaveReward(loginMember, question1);
        NewReward reward2 = createAndSaveReward(loginMember, question2);
        NewReward reward3 = createAndSaveReward(loginMember, question3);

        em.flush();
        em.clear();

        //when
        List<QuestionReward> findRewards = newRewardRepository.findByQuestionsAndMember(questions, loginMember);

        //then
        assertThat(findRewards).hasSize(3)
                .extracting("rewardId")
                .containsExactlyInAnyOrder(reward1.getRewardId(), reward2.getRewardId(), reward3.getRewardId());
    }

    @Test
    @DisplayName("member 와 Question List 로 모든 Reward 를 찾는다. reward 가 취소되었으면 반환하지 않는다.")
    void findByQuestionsAndMemberCanceled() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        Question question1 = createAndSaveQuestion(video);
        Question question2 = createAndSaveQuestion(video);
        Question question3 = createAndSaveQuestion(video);
        List<Question> questions = List.of(question1, question2, question3);

        Member member = createMemberWithChannel();

        createAndSaveOrderComplete(member, List.of(video));

        NewReward reward1 = createAndSaveReward(member, question1);
        reward1.cancelReward();
        NewReward reward2 = createAndSaveReward(member, question2);
        reward2.cancelReward();
        NewReward reward3 = createAndSaveReward(member, question3);
        reward3.cancelReward();

        em.flush();
        em.clear();

        //when
        List<QuestionReward> findRewards = newRewardRepository.findByQuestionsAndMember(questions, member);

        //then
        assertThat(findRewards).isEmpty();
    }
    
    @Test
    @DisplayName("videoId, memberId 를 통해 해당 video 를 통해 얻은 모든 Reward 를 확인한다.")
    void findByMemberAndVideoId() {
        //given
        Member owner = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());
        Question question = createAndSaveQuestion(video1);

        Member member = createMemberWithChannel();

        Reply reply = createAndSaveReply(member, video1);

        createAndSaveOrderComplete(member, List.of(video1, video2));

        NewReward reward1 = createAndSaveReward(member, video1);
        NewReward reward2 = createAndSaveReward(member, question);
        NewReward reward3 = createAndSaveReward(member, video2);
        NewReward reward4 = createAndSaveReward(member, reply);


        em.flush();
        em.clear();

        //when
        List<NewReward> rewards = newRewardRepository.findByMemberAndVideoId(member.getMemberId(), video1.getVideoId());

        //then
        assertThat(rewards).hasSize(3)
                .extracting("rewardId")
                .containsExactlyInAnyOrder(
                        reward1.getRewardId(),
                        reward2.getRewardId(),
                        reward4.getRewardId()
                );
    }

    @Test
    @DisplayName("videoId, memberId 를 통해 해당 video 를 통해 얻은 모든 Reward 를 확인할 때 취소된 리워드는 반환하지 않는다.")
    void findByMemberAndVideoIdExceptCanceledReward() {
        //given
        Member owner = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());
        Question question = createAndSaveQuestion(video1);

        Member member = createMemberWithChannel();

        Reply reply = createAndSaveReply(member, video1);

        createAndSaveOrderComplete(member, List.of(video1, video2));

        NewReward reward1 = createAndSaveReward(member, video1);
        NewReward reward2 = createAndSaveReward(member, question);
        NewReward reward3 = createAndSaveReward(member, video2);
        NewReward reward4 = createAndSaveReward(member, reply);
        reward4.cancelReward(); //취소된 리워드


        em.flush();
        em.clear();

        //when
        List<NewReward> rewards = newRewardRepository.findByMemberAndVideoId(member.getMemberId(), video1.getVideoId());

        //then
        assertThat(rewards).hasSize(2)
                .extracting("rewardId")
                .containsExactlyInAnyOrder(
                        reward1.getRewardId(),
                        reward2.getRewardId()
                );
    }

    protected NewReward createAndSaveReward(Member member, Rewardable rewardable) {

        NewReward reward = NewReward.createReward(10, member, rewardable);

        em.persist(reward);

        return reward;
    }

    protected Reply createAndSaveReply(Member member, Video video) {
        Reply reply = Reply.builder()
                .content("content")
                .star(5)
                .member(member)
                .video(video)
                .build();

        em.persist(reply);

        return reply;
    }
}