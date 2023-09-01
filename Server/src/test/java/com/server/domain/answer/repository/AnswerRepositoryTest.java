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
}