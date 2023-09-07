package com.server.domain.answer.entity;

import com.server.domain.member.entity.Member;
import com.server.domain.question.entity.Question;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class AnswerTest {

    @Test
    @DisplayName("myAnswer, member, question 을 받아 answer 를 생성한다.")
    void createAnswer() {
        //given
        String myAnswer = "myAnswer";
        Member member = createMember();
        Question question = createQuestion("answer");

        //when
        Answer answer = Answer.createAnswer(myAnswer, member, question);

        //then
        assertThat(answer.getAnswerStatus()).isEqualTo(AnswerStatus.WRONG);
        assertThat(answer.getMyAnswer()).isEqualTo(myAnswer);
        assertThat(answer.getMember()).isEqualTo(member);
        assertThat(answer.getQuestion()).isEqualTo(question);
    }

    @Test
    @DisplayName("myAnswer, questionAnswer 를 받아 정답을 맞춘다.")
    void solveAnswer() {
        //given
        String correctAnswer = "correctAnswer";
        Question question = createQuestion(correctAnswer);
        Answer answer = createAnswer(question);

        //when
        boolean result = answer.solveAnswer(correctAnswer);

        //then
        assertThat(result).isTrue();
        assertThat(answer.getAnswerStatus()).isEqualTo(AnswerStatus.CORRECT);
    }

    private Answer createAnswer(Question question) {
        return Answer.builder()
                .answerId(1L)
                .answerStatus(AnswerStatus.WRONG)
                .myAnswer("answer")
                .question(question)
                .member(createMember())
                .build();
    }

    private Member createMember() {
        return Member.builder()
                .memberId(1L)
                .build();
    }

    private Question createQuestion(String questionAnswer) {
        return Question.builder()
                .questionId(1L)
                .questionAnswer(questionAnswer)
                .build();
    }
}