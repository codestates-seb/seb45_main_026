package com.server.domain.question.repository;

import com.server.domain.answer.entity.Answer;
import com.server.domain.answer.entity.AnswerStatus;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.question.entity.Question;
import com.server.domain.question.repository.dto.QuestionData;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.RepositoryTest;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

class QuestionRepositoryTest extends RepositoryTest {

    @Autowired QuestionRepository questionRepository;

    @Test
    @DisplayName("member Id 와 QuestionId 로 해당 Question 정보를 조회한다.")
    void findQuestionWithMemberAnswer() {
        //given
        Member member = createAndSaveMember();
        Member otherMember = createAndSaveMember();

        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);
        Order order = createAndSaveOrder(member, List.of(video));

        List<Question> questions = createAndSaveQuestions(video);
        Question question = questions.get(0);
        Answer answer = createAndSaveAnswer(member, question);// 1번 문제에 대한 답을 저장한다.
        Answer otherAnswer = createAndSaveAnswer(otherMember, question);// 다른 member 도 1번 문제에 대한 답을 저장한다.

        em.flush();
        em.clear();

        QuestionData questionData = questionRepository.findQuestionDataWithMemberAnswer(member.getMemberId(), question.getQuestionId()).orElseThrow();

        //then
        assertThat(questionData.getQuestionId()).isEqualTo(question.getQuestionId());
        assertThat(questionData.getPosition()).isEqualTo(question.getPosition());
        assertThat(questionData.getContent()).isEqualTo(question.getContent());
        assertThat(questionData.getQuestionAnswer()).isEqualTo(question.getQuestionAnswer());
        assertThat(questionData.getDescription()).isEqualTo(question.getDescription());
        assertThat(questionData.getAnswerId()).isEqualTo(answer.getAnswerId());
        assertThat(questionData.getMyAnswer()).isEqualTo(answer.getMyAnswer());
        assertThat(questionData.getAnswerStatus()).isEqualTo(answer.getAnswerStatus());
        assertThat(questionData.getSelections()).containsExactlyInAnyOrder("1", "2", "3", "4", "5");

        LocalDateTime truncatedExpected = questionData.getSolvedDate().truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime truncatedActual = answer.getModifiedDate().truncatedTo(ChronoUnit.MILLIS);
        assertThat(truncatedActual).isEqualTo(truncatedExpected);
    }

    @Test
    @DisplayName("member 가 Question 을 풀지 않아도 Question 정보를 조회할 수 있다.")
    void findQuestionWithMemberAnswerWithoutSolving() {
        //given
        Member member = createAndSaveMember();
        Member otherMember = createAndSaveMember();

        Channel channel = createAndSaveChannel(member);

        Video video = createAndSaveVideo(channel);
        Order order = createAndSaveOrder(member, List.of(video));

        List<Question> questions = createAndSaveQuestions(video);
        Question question = questions.get(0);
        Answer otherAnswer = createAndSaveAnswer(otherMember, question);// 다른 member 도 1번 문제에 대한 답을 저장한다.

        em.flush();
        em.clear();

        QuestionData questionData = questionRepository.findQuestionDataWithMemberAnswer(member.getMemberId(), question.getQuestionId()).orElseThrow();

        //then
        assertThat(questionData.getQuestionId()).isEqualTo(question.getQuestionId());
        assertThat(questionData.getPosition()).isEqualTo(question.getPosition());
        assertThat(questionData.getContent()).isEqualTo(question.getContent());
        assertThat(questionData.getQuestionAnswer()).isEqualTo(question.getQuestionAnswer());
        assertThat(questionData.getDescription()).isEqualTo(question.getDescription());
        assertThat(questionData.getMyAnswer()).isNull();
        assertThat(questionData.getAnswerStatus()).isNull();
        assertThat(questionData.getSolvedDate()).isNull();
        assertThat(questionData.getSelections()).containsExactlyInAnyOrder("1", "2", "3", "4", "5");
    }

    @Test
    @DisplayName("questionId 로 video 를 찾는다.")
    void findVideoWithQuestion() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video = createAndSaveVideo(channel);

        List<Question> questions = createAndSaveQuestions(video);
        Question question = questions.get(0);

        em.flush();
        em.clear();

        //when
        Video findVideo = questionRepository.findVideoByQuestionId(question.getQuestionId()).orElseThrow();

        //then
        assertThat(findVideo.getVideoId()).isEqualTo(video.getVideoId());

    }

    @Test
    @DisplayName("memberId 와 videoId 로 해당 video 의 모든 question 정보를 조회한다.")
    void findQuestionDatasWithMemberAnswerByVideoId() {
        //given
        Member member = createAndSaveMember();

        Channel channel = createAndSaveChannel(member);

        Video video = createAndSaveVideo(channel);
        Order order = createAndSaveOrder(member, List.of(video));

        List<Question> questions = createAndSaveQuestions(video);
        Question question = questions.get(0);
        Answer answer = createAndSaveAnswer(member, question); //한 문제는 품

        em.flush();
        em.clear();

        //when
        List<QuestionData> datas =
                questionRepository.findQuestionDatasWithMemberAnswerByVideoId(member.getMemberId(), video.getVideoId());

        //then
        assertThat(datas).hasSize(questions.size());
        datas.forEach(data -> {
            if(data.getAnswerId() != null){ //answer 가 존재하는 경우
                if(data.getAnswerId().equals(answer.getAnswerId())) {
                    assertThat(data.getAnswerStatus()).isEqualTo(answer.getAnswerStatus());
                    assertThat(data.getMyAnswer()).isEqualTo(answer.getMyAnswer());
                }
            }
            else { //answer 가 존재하지 않는 경우
                assertThat(data.getAnswerStatus()).isNull();
                assertThat(data.getMyAnswer()).isNull();
            }
        });
    }

    @Test
    @DisplayName("videoId 로 해당 video 의 모든 question 정보를 조회한다. video 도 초기화되어있다.")
    void findByVideoId() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);
        List<Question> questions = createAndSaveQuestions(video);

        em.flush();
        em.clear();

        //when
        List<Question> findQuestions = questionRepository.findQuestionsWithVideoByVideoId(video.getVideoId());

        //then
        assertThat(findQuestions).hasSize(questions.size())
                .extracting("questionId")
                .containsExactlyElementsOf(questions.stream().map(Question::getQuestionId).collect(Collectors.toList()));

        assertThat(Hibernate.isInitialized(findQuestions.get(0).getVideo())).isTrue();
    }

    private List<Question> createAndSaveQuestions(Video video) {

        List<Question> questions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {

            Question question = Question.builder()
                    .position(i)
                    .content("content" + i)
                    .questionAnswer(String.valueOf(i))
                    .selections(List.of("1", "2", "3", "4", "5"))
                    .video(video)
                    .build();

            em.persist(question);
            questions.add(question);
        }

        return questions;
    }

    protected Answer createAndSaveAnswer(Member member, Question question) {
        Answer answer = Answer.builder()
                .member(member)
                .question(question)
                .myAnswer("1")
                .answerStatus(AnswerStatus.WRONG)
                .build();

        em.persist(answer);

        return answer;
    }
}