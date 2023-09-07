package com.server.domain.question.entity;

import com.server.domain.video.entity.Video;
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

class QuestionTest {

    @Test
    @DisplayName("question 을 생성한다.")
    void createQuestion() {
        //given
        int position = 1;
        String content = "content";
        String questionAnswer = "questionAnswer";
        String description = "description";
        Video video = Video.builder().build();
        List<String> selections = List.of("selection1", "selection2");

        //when
        Question question = Question.createQuestion(position, content, questionAnswer, description, selections, video);

        //then
        assertAll(
                () -> assertThat(question.getPosition()).isEqualTo(position),
                () -> assertThat(question.getContent()).isEqualTo(content),
                () -> assertThat(question.getQuestionAnswer()).isEqualTo(questionAnswer),
                () -> assertThat(question.getDescription()).isEqualTo(description),
                () -> assertThat(question.getSelections()).isEqualTo(selections),
                () -> assertThat(question.getVideo()).isEqualTo(video)
        );
    }

    @TestFactory
    @DisplayName("question 의 필드값을 받아 question 을 수정한다.")
    Collection<DynamicTest> update() {
        //given
        Question question = Question.builder().build();

        int position = 1;
        String content = "content";
        String questionAnswer = "questionAnswer";
        String description = "description";
        List<String> selections = List.of("selection1", "selection2");

        return List.of(
                dynamicTest("필드값을 수정한다.", ()-> {
                    //when
                    question.update(position, content, questionAnswer, description, selections);

                    //then
                    assertAll(
                            () -> assertThat(question.getPosition()).isEqualTo(position),
                            () -> assertThat(question.getContent()).isEqualTo(content),
                            () -> assertThat(question.getQuestionAnswer()).isEqualTo(questionAnswer),
                            () -> assertThat(question.getDescription()).isEqualTo(description),
                            () -> assertThat(question.getSelections()).isEqualTo(selections)
                    );

                }),
                dynamicTest("null 값이 들어오면 수정하지 않는다.", ()-> {
                    //when
                    question.update(null, null, null, null, null);

                    //then
                    assertAll(
                            () -> assertThat(question.getPosition()).isEqualTo(position),
                            () -> assertThat(question.getContent()).isEqualTo(content),
                            () -> assertThat(question.getQuestionAnswer()).isEqualTo(questionAnswer),
                            () -> assertThat(question.getDescription()).isEqualTo(description),
                            () -> assertThat(question.getSelections()).isEqualTo(selections)
                    );

                })
        );


    }

    @Test
    @DisplayName("question 의 reward 포인트를 조회한다.")
    void getRewardPoint() {
        //given
        Question question = Question.builder().build();

        //when
        int rewardPoint = question.getRewardPoint();

        //then
        assertThat(rewardPoint).isEqualTo(10);
    }
}