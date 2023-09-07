package com.server.domain.answer.repository;

import com.server.domain.answer.entity.Answer;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.question.entity.Question;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.RepositoryTest;
import org.assertj.core.api.Assertions;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class AnswerRepositoryTest extends RepositoryTest {

    @Autowired AnswerRepository answerRepository;

    @Test
    @DisplayName("memberId, questionId 를 받아 answer 를 조회한다. member 와 question 은 초기화되어있다.")
    void findByMemberIdAndQuestionId() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video = createAndSaveVideo(channel);
        Question question = createAndSaveQuestion(video);

        Answer answer = createAndSaveAnswer(member, question);

        em.flush();
        em.clear();

        //when
        Answer findAnswer =
                answerRepository.findByMemberIdAndQuestionId(
                        member.getMemberId(),
                        question.getQuestionId()
        ).orElseThrow();

        //then
        assertThat(findAnswer.getAnswerId()).isEqualTo(answer.getAnswerId());
        assertThat(Hibernate.isInitialized(findAnswer.getMember())).isTrue();
        assertThat(Hibernate.isInitialized(findAnswer.getQuestion())).isTrue();
    }

    @Test
    @DisplayName("memberId, questionIds 를 받아 답변한 answer 목록을 조회한다. member 와 Question 은 초기화되어있다.")
    void findByMemberIdAndQuestionIds() {
        //given
        Member member = createMemberWithChannel();

        Video video = createAndSaveVideo(member.getChannel());
        Question question1 = createAndSaveQuestion(video);
        Question question2 = createAndSaveQuestion(video);
        Question question3 = createAndSaveQuestion(video);
        Question question4 = createAndSaveQuestion(video);

        Answer answer1 = createAndSaveAnswer(member, question1);
        Answer answer2 = createAndSaveAnswer(member, question2);
        Answer answer3 = createAndSaveAnswer(member, question3);

        List<Long> questionIds = List.of(
                question1.getQuestionId(),
                question2.getQuestionId(),
                question3.getQuestionId(),
                question4.getQuestionId()
        );

        em.flush();
        em.clear();

        //when
        List<Answer> findAnswers =
                answerRepository.findByMemberIdAndQuestionIds(
                        member.getMemberId(),
                        questionIds
                );
        //then
        assertThat(findAnswers).hasSize(3)
                .extracting("answerId")
                .containsExactlyInAnyOrder(
                        answer1.getAnswerId(),
                        answer2.getAnswerId(),
                        answer3.getAnswerId()
                );

        assertThat(Hibernate.isInitialized(findAnswers.get(0).getMember())).isTrue();
        assertThat(Hibernate.isInitialized(findAnswers.get(0).getQuestion())).isTrue();
    }
}