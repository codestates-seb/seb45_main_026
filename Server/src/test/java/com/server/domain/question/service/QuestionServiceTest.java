package com.server.domain.question.service;

import com.server.domain.answer.entity.AnswerStatus;
import com.server.domain.member.entity.Member;
import com.server.domain.question.entity.Question;
import com.server.domain.question.repository.QuestionRepository;
import com.server.domain.question.service.dto.response.QuestionResponse;
import com.server.domain.video.entity.Video;
import com.server.global.exception.businessexception.questionexception.QuestionNotFoundException;
import com.server.global.exception.businessexception.videoexception.VideoAccessDeniedException;
import com.server.global.testhelper.ServiceTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class QuestionServiceTest extends ServiceTest {

    @Autowired QuestionRepository questionRepository;
    @Autowired QuestionService questionService;

    @Test
    @DisplayName("개별 문제에 대한 정보를 조회한다. (아직 답변하지 않은 상태)")
    void getQuestion() {
//        //given
//        Member member = createAndSaveMember();
//        Video video = createAndSavePurchasedVideo(member);
//
//        List<Question> questions = createAndSaveQuestions(video);
//        Question question = questions.get(0);
//
//        //when
//        QuestionResponse response = questionService.getQuestion(member.getMemberId(), question.getQuestionId());
//
//        //then
//        assertThat(response.getQuestionId()).isEqualTo(question.getQuestionId());
//        assertThat(response.getPosition()).isEqualTo(question.getPosition());
//        assertThat(response.getContent()).isEqualTo(question.getContent());
//        assertThat(response.getSelections()).isEqualTo(question.getSelections());
//        assertThat(response.getQuestionAnswer()).isEqualTo(question.getQuestionAnswer());
//        assertThat(response.getDescription()).isEqualTo(question.getDescription());
//        assertThat(response.getMyAnswer()).isNull();
//        assertThat(response.getAnswerStatus()).isNull();
//        assertThat(response.getSolvedDate()).isNull();
    }

    @Test
    @DisplayName("해당 문제가 속한 비디오를 구매하지 않으면 문제를 조회할 수 없다. (VideoAccessDeniedException)")
    void getQuestionVideoAccessDeniedException() {
//        //given
//        Member member = createAndSaveMember();
//        Video video = createAndSaveVideo();
//
//        List<Question> questions = createAndSaveQuestions(video);
//        Question question = questions.get(0);
//
//        //when & then
//        assertThatThrownBy(() ->
//                questionService.getQuestion(member.getMemberId(), question.getQuestionId()))
//                .isInstanceOf(VideoAccessDeniedException.class);
    }

    @Test
    @DisplayName("해당 문제가 존재하지 않으면 QuestionNotFoundException 가 발생하고 문제를 조회할 수 없다.")
    void getQuestionNotFoundException() {
        //given
        Member member = createAndSaveMember();
        Video video = createAndSavePurchasedVideo(member);

        List<Question> questions = createAndSaveQuestions(video);

        //when & then
        assertThatThrownBy(() ->
                questionService.getQuestion(member.getMemberId(), 9999L)) // 존재하지 않는 questionId
                .isInstanceOf(QuestionNotFoundException.class);

    }

    @Test
    void updateQuestion() {
    }

    @Test
    void deleteQuestion() {
    }

    @Test
    void solveQuestion() {
    }

    private List<Question> createAndSaveQuestions(Video video) {

        List<Question> questions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            questions.add(Question.builder()
                    .position(i)
                    .content("content" + i)
                    .questionAnswer(String.valueOf(i))
                    .video(video)
                    .build());
        }

        questionRepository.saveAll(questions);

        return questions;

    }
}