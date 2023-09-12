package com.server.domain.answer.entity;

import com.server.domain.member.entity.Member;
import com.server.domain.question.entity.Question;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

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
        answer.solveAnswer(correctAnswer);

        //then
        assertThat(answer.getAnswerStatus()).isEqualTo(AnswerStatus.CORRECT);
    }

    @TestFactory
    @DisplayName("answer 의 status 가 correct 인지 확인한다.")
    Collection<DynamicTest> isCorrect() {
        //given
        String correctAnswer = "correctAnswer";
        Question question = createQuestion(correctAnswer);
        Answer answer = createAnswer(question);

        return List.of(
                dynamicTest("문제를 틀리면 false 를 반환한다.", () -> {
                    //when
                    answer.solveAnswer("wrongAnswer");

                    //then
                    assertThat(answer.isCorrect()).isFalse();
                }),
                dynamicTest("문제를 맞추면 true 를 반환한다.", () -> {
                    //when
                    answer.solveAnswer(correctAnswer);

                    //then
                    assertThat(answer.isCorrect()).isTrue();
                })
        );
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