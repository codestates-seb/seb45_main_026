package com.server.domain.reward.repository;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.question.entity.Question;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reward.entity.NewReward;
import com.server.domain.reward.entity.QuestionReward;
import com.server.domain.reward.entity.Reward;
import com.server.domain.reward.entity.Rewardable;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.RepositoryTest;
import org.hibernate.Hibernate;
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
    @DisplayName("주문번호로 reward 리스트를 찾는다. reward 의 member 는 초기화되어있다.")
    void findByOrderId() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);
        Question question = createAndSaveQuestion(video1);

        Member member = createAndSaveMember();
        Channel memberChannel = createAndSaveChannel(member);

        Reply reply = createAndSaveReply(member, video1);

        Order order = createAndSaveOrderComplete(member, List.of(video1, video2));

        NewReward reward1 = createAndSaveReward(member, video1);
        NewReward reward2 = createAndSaveReward(member, question);
        NewReward reward3 = createAndSaveReward(member, video2);
        NewReward reward4 = createAndSaveReward(member, reply);


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

        for (NewReward reward : rewards) {
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

        NewReward reward = createAndSaveReward(member, question);

        em.flush();
        em.clear();

        //when
        NewReward findReward = newRewardRepository.findByQuestionAndMember(question, member).orElseThrow();

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

        NewReward reward = createAndSaveReward(member, question);
        reward.cancelReward();

        em.flush();
        em.clear();

        //when
        Optional<QuestionReward> findReward = newRewardRepository.findByQuestionAndMember(question, member);

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

        NewReward reward1 = createAndSaveReward(member, question1);
        NewReward reward2 = createAndSaveReward(member, question2);
        NewReward reward3 = createAndSaveReward(member, question3);

        em.flush();
        em.clear();

        //when
        List<QuestionReward> findRewards = newRewardRepository.findByQuestionsAndMember(questions, member);

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