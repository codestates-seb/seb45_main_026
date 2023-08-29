package com.server.domain.question.service;

import com.server.domain.answer.entity.Answer;
import com.server.domain.answer.entity.AnswerStatus;
import com.server.domain.answer.repository.AnswerRepository;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.question.entity.Question;
import com.server.domain.question.repository.QuestionRepository;
import com.server.domain.question.service.dto.request.AnswerCreateServiceRequest;
import com.server.domain.question.service.dto.request.QuestionCreateServiceRequest;
import com.server.domain.question.service.dto.request.QuestionUpdateServiceRequest;
import com.server.domain.question.service.dto.response.QuestionResponse;
import com.server.domain.video.entity.Video;
import com.server.global.exception.businessexception.answerexception.AnswerCountException;
import com.server.global.exception.businessexception.questionexception.QuestionNotFoundException;
import com.server.global.exception.businessexception.videoexception.VideoAccessDeniedException;
import com.server.global.exception.businessexception.videoexception.VideoNotFoundException;
import com.server.global.exception.businessexception.videoexception.VideoNotPurchasedException;
import com.server.global.testhelper.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.tuple;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class QuestionServiceTest extends ServiceTest {

    @Autowired AnswerRepository answerRepository;
    @Autowired QuestionService questionService;
    @Autowired QuestionRepository questionRepository;

    @Test
    @DisplayName("개별 문제에 대한 정보를 조회한다. (아직 답변하지 않은 상태)")
    void getQuestion() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSavePurchasedVideo(member, channel);

        List<Question> questions = createAndSaveQuestions(video);
        Question question = questions.get(0);

        //when
        QuestionResponse response = questionService.getQuestion(member.getMemberId(), question.getQuestionId());

        //then
        assertThat(response.getQuestionId()).isEqualTo(question.getQuestionId());
        assertThat(response.getPosition()).isEqualTo(question.getPosition());
        assertThat(response.getContent()).isEqualTo(question.getContent());
        assertThat(response.getSelections()).isEqualTo(question.getSelections());
        assertThat(response.getQuestionAnswer()).isEqualTo(question.getQuestionAnswer());
        assertThat(response.getDescription()).isEqualTo(question.getDescription());
        assertThat(response.getMyAnswer()).isNull();
        assertThat(response.getAnswerStatus()).isNull();
        assertThat(response.getSolvedDate()).isNull();
    }

    @Test
    @DisplayName("해당 문제가 속한 비디오를 구매하지 않으면 문제를 조회할 수 없다. (VideoNotPurchasedException)")
    void getQuestionVideoNotPurchasedException() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        List<Question> questions = createAndSaveQuestions(video);
        Question question = questions.get(0);

        //when & then
        assertThatThrownBy(() ->
                questionService.getQuestion(member.getMemberId(), question.getQuestionId()))
                .isInstanceOf(VideoNotPurchasedException.class);
    }

    @Test
    @DisplayName("해당 Question 이 존재하지 않으면 QuestionNotFoundException 가 발생하고 문제를 조회할 수 없다.")
    void getQuestionNotFoundException() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSavePurchasedVideo(member, channel);

        List<Question> questions = createAndSaveQuestions(video);

        //when & then
        assertThatThrownBy(() ->
                questionService.getQuestion(member.getMemberId(), 9999L)) // 존재하지 않는 questionId
                .isInstanceOf(QuestionNotFoundException.class);
    }

    @Test
    @DisplayName("Video 에 대한 모든 Question 을 조회한다.")
    void getQuestions() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSavePurchasedVideo(member, channel);

        List<Question> questions = createAndSaveQuestions(video);

        //when
        List<QuestionResponse> response = questionService.getQuestions(member.getMemberId(), video.getVideoId());

        //then
        assertThat(response).hasSize(questions.size())
                .extracting("questionId", Long.class)
                .containsExactlyInAnyOrderElementsOf(questions.stream()
                        .map(Question::getQuestionId)
                        .collect(Collectors.toList()));
    }

    @Test
    @DisplayName("해당 Video 를 구매하지 않으면 비디오의 Question 를 조회할 수 없다. (VideoNotPurchasedException)")
    void getQuestionsVideoNotPurchasedException() {
        //given
        Member channelOwner = createAndSaveMember();
        Channel channel = createAndSaveChannel(channelOwner);
        Video video = createAndSavePurchasedVideo(channelOwner, channel);

        Member member = createAndSaveMember(); // 비디오를 구매하지 않은 멤버

        List<Question> questions = createAndSaveQuestions(video);

        //when & then
        assertThatThrownBy(() ->
                questionService.getQuestions(member.getMemberId(), video.getVideoId()))
                .isInstanceOf(VideoNotPurchasedException.class);
    }

    @Test
    @DisplayName("해당 Video 에 Question 이 존재하지 않으면 빈 리스트를 반환한다.")
    void getQuestionsEmpty() {
        //given
        Member channelOwner = createAndSaveMember();
        Channel channel = createAndSaveChannel(channelOwner);
        Video video = createAndSavePurchasedVideo(channelOwner, channel);

        Member member = createAndSaveMember();
        createAndSaveOrderWithPurchase(member, List.of(video), 0);

        //when
        List<QuestionResponse> response = questionService.getQuestions(member.getMemberId(), video.getVideoId());

        //then
        assertThat(response).isEmpty();
    }

    @Test
    @DisplayName("Video 에 대한 Question 조회 시 video 가 없으면 VideoNotFoundException 이 발생한다.")
    void getQuestionsVideoNotFoundException() {
        //given
        Member channelOwner = createAndSaveMember();
        Channel channel = createAndSaveChannel(channelOwner);
        Video video = createAndSavePurchasedVideo(channelOwner, channel);

        Member member = createAndSaveMember();
        createAndSaveOrderWithPurchase(member, List.of(video), 0);

        List<Question> questions = createAndSaveQuestions(video);

        //when & then
        assertThatThrownBy(() -> // 존재하지 않는 videoId
                questionService.getQuestions(member.getMemberId(), video.getVideoId() + 999L))
                .isInstanceOf(VideoNotFoundException.class);
    }

    @Test
    @DisplayName("position, content, questionAnswer, description, selections 을 리스트로 입력받아 Question 을 생성한다.")
    void createQuestions() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        List<QuestionCreateServiceRequest> requests = List.of(
                QuestionCreateServiceRequest.builder()
                        .position(1)
                        .content("content1")
                        .questionAnswer("1")
                        .description("description1")
                        .selections(List.of("1", "2", "3", "4", "5"))
                        .build(),
                QuestionCreateServiceRequest.builder()
                        .position(2)
                        .content("content2")
                        .questionAnswer("2")
                        .description("description2")
                        .selections(List.of("1", "2", "3", "4", "5"))
                        .build()
        );

        //when
        List<Long> questions = questionService.createQuestions(member.getMemberId(), video.getVideoId(), requests);

        //then
        assertThat(questionRepository.findAllById(questions)).hasSize(2)
                .extracting("position", "content", "questionAnswer", "description")
                .containsExactlyInAnyOrder(
                        tuple(1, "content1", "1", "description1"),
                        tuple(2, "content2", "2", "description2")
                );
    }

    @Test
    @DisplayName("해당 Video 의 소유자 가 아니면 Question 을 생성할 수 없다. (VideoAccessDeniedException)")
    void createQuestionsVideoAccessDeniedException() {
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video = createAndSaveVideo(channel);

        Member member = createAndSaveMember(); // 비디오의 소유자가 아닌 멤버

        List<QuestionCreateServiceRequest> requests = List.of(
                QuestionCreateServiceRequest.builder()
                        .position(1)
                        .content("content1")
                        .questionAnswer("1")
                        .description("description1")
                        .selections(List.of("1", "2", "3", "4", "5"))
                        .build()
        );

        //when & then
        assertThatThrownBy(() ->
                questionService.createQuestions(member.getMemberId(), video.getVideoId(), requests))
                .isInstanceOf(VideoAccessDeniedException.class);
    }

    @Test
    @DisplayName("Question 생성 시 해당 Video 가 존재하지 않으면 VideoNotFoundException 이 발생한다.")
    void createQuestionsVideoNotFoundException() {
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video = createAndSaveVideo(channel);

        List<QuestionCreateServiceRequest> requests = List.of(
                QuestionCreateServiceRequest.builder()
                        .position(1)
                        .content("content1")
                        .questionAnswer("1")
                        .description("description1")
                        .selections(List.of("1", "2", "3", "4", "5"))
                        .build()
        );

        //when & then
        assertThatThrownBy(() -> // 존재하지 않는 videoId
                questionService.createQuestions(owner.getMemberId(), video.getVideoId() + 999L, requests))
                .isInstanceOf(VideoNotFoundException.class);
    }

    @Test
    @DisplayName("Question 의 position, content, questionAnswer, description, selections 를 수정한다.")
    void updateQuestion() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSavePurchasedVideo(member, channel);

        List<Question> questions = createAndSaveQuestions(video);
        Question question = questions.get(0);

        QuestionUpdateServiceRequest request = QuestionUpdateServiceRequest.builder()
                .questionId(question.getQuestionId())
                .position(6)
                .content("update content")
                .questionAnswer("10")
                .description("update description")
                .selections(List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"))
                .build();

        //when
        questionService.updateQuestion(member.getMemberId(), request);

        //then
        Question updatedQuestion = questionRepository.findById(question.getQuestionId()).orElseThrow();
        assertThat(updatedQuestion.getPosition()).isEqualTo(request.getPosition());
        assertThat(updatedQuestion.getContent()).isEqualTo(request.getContent());
        assertThat(updatedQuestion.getQuestionAnswer()).isEqualTo(request.getQuestionAnswer());
        assertThat(updatedQuestion.getDescription()).isEqualTo(request.getDescription());
        assertThat(updatedQuestion.getSelections())
                .containsExactlyInAnyOrder("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    }

    @Test
    @DisplayName("Question 의 해당 Video 를 구매했더라도 Question 을 수정할 수 없다.")
    void updateQuestionAccessDeniedException() {
        //given
        Member member = createAndSaveMember(); // 동영상 주인
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        Member othermember = createAndSaveMember(); // 구매한 사람

        createAndSaveOrder(othermember, List.of(video), 0);

        List<Question> questions = createAndSaveQuestions(video);
        Question question = questions.get(0);

        QuestionUpdateServiceRequest request = QuestionUpdateServiceRequest.builder()
                .questionId(question.getQuestionId())
                .position(6)
                .content("update content")
                .questionAnswer("10")
                .description("update description")
                .selections(List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"))
                .build();

        //when & then
        assertThatThrownBy(() ->
                questionService.updateQuestion(othermember.getMemberId(), request)) // 동영상 주인이 아닌 사람이 수정 요구
                .isInstanceOf(VideoAccessDeniedException.class);

        //then (변경되지 않았음을 확인)
        Question updatedQuestion = questionRepository.findById(question.getQuestionId()).orElseThrow();
        assertThat(updatedQuestion.getPosition()).isNotEqualTo(request.getPosition());
        assertThat(updatedQuestion.getContent()).isNotEqualTo(request.getContent());
        assertThat(updatedQuestion.getQuestionAnswer()).isNotEqualTo(request.getQuestionAnswer());
        assertThat(updatedQuestion.getDescription()).isNotEqualTo(request.getDescription());
        assertThat(updatedQuestion.getSelections()).isNotEqualTo(request.getSelections());
    }

    @Test
    @DisplayName("Question 수정 시 questionId 가 존재하지 않으면 수정할 수 없다.")
    void updateQuestionQuestionNotFoundException() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSavePurchasedVideo(member, channel);

        List<Question> questions = createAndSaveQuestions(video);
        Question question = questions.get(0);

        QuestionUpdateServiceRequest request = QuestionUpdateServiceRequest.builder()
                .questionId(question.getQuestionId() + 999L) // 존재하지 않는 questionId
                .position(6)
                .content("update content")
                .questionAnswer("10")
                .description("update description")
                .selections(List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"))
                .build();

        //when & then
        assertThatThrownBy(() ->
                questionService.updateQuestion(member.getMemberId(), request))
                .isInstanceOf(QuestionNotFoundException.class);

        //then (변경되지 않았음을 확인)
        Question updatedQuestion = questionRepository.findById(question.getQuestionId()).orElseThrow();
        assertThat(updatedQuestion.getPosition()).isNotEqualTo(request.getPosition());
        assertThat(updatedQuestion.getContent()).isNotEqualTo(request.getContent());
        assertThat(updatedQuestion.getQuestionAnswer()).isNotEqualTo(request.getQuestionAnswer());
        assertThat(updatedQuestion.getDescription()).isNotEqualTo(request.getDescription());
        assertThat(updatedQuestion.getSelections()).isNotEqualTo(request.getSelections());

    }

    @Test
    @DisplayName("Video 를 올린 회원이 해당 Video 의 Question 을 삭제한다.")
    void deleteQuestion() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSavePurchasedVideo(member, channel);

        List<Question> questions = createAndSaveQuestions(video);
        Question question = questions.get(0);

        //when
        questionService.deleteQuestion(member.getMemberId(), question.getQuestionId());

        //then
        assertThat(questionRepository.findById(question.getQuestionId())).isEmpty();
    }

    @Test
    @DisplayName("Video 를 올린 회원이 아니면 해당 Video 의 Question 을 삭제할 수 없다.")
    void deleteQuestionVideoAccessDeniedException() {
        //given
        Member member = createAndSaveMember();
        Member otherMember = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSavePurchasedVideo(member, channel);

        createAndSaveOrder(otherMember, List.of(video), 0); // 다른 회원이 구매

        List<Question> questions = createAndSaveQuestions(video);
        Question question = questions.get(0);

        //when & then
        assertThatThrownBy(() ->
                questionService.deleteQuestion(otherMember.getMemberId(), question.getQuestionId())) // 다른 회원이 삭제 요구
                .isInstanceOf(VideoAccessDeniedException.class);

        //then (삭제되지 않았음을 확인)
        assertThat(questionRepository.findById(question.getQuestionId())).isPresent();
    }

    @Test
    @DisplayName("Question 삭제 요구 시 questionId 가 유효하지 않으면 QuestionNotFoundException 이 발생한다.")
    void deleteQuestionQuestionNotFoundException() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSavePurchasedVideo(member, channel);

        List<Question> questions = createAndSaveQuestions(video);
        Question question = questions.get(0);

        //when & then
        assertThatThrownBy(() ->
                questionService.deleteQuestion(member.getMemberId(), question.getQuestionId() + 999L)) // 존재하지 않는 questionId
                .isInstanceOf(QuestionNotFoundException.class);

        //then (삭제되지 않았음을 확인)
        assertThat(questionRepository.findById(question.getQuestionId())).isPresent();

    }

    @TestFactory
    @DisplayName("myAnswer 을 통해 Question 을 풀고, 정답을 맞춘다.")
    Collection<DynamicTest> solveQuestion() {
        //given
        Member channelOwner = createAndSaveMember();
        Channel channel = createAndSaveChannel(channelOwner);
        Video video = createAndSavePurchasedVideo(channelOwner, channel);

        List<Question> questions = createAndSaveQuestions(video);
        Question question = questions.get(0);

        Member member = createAndSaveMember();
        createAndSaveOrderWithPurchase(member, List.of(video), 0); // member 가 구매

        return List.of(
                dynamicTest("처음에는 정답을 맞추지 못해 false 를 반환받는다.", ()-> {

                    //given
                    AnswerCreateServiceRequest request = AnswerCreateServiceRequest.builder()
                            .questionId(question.getQuestionId())
                            .myAnswer("2")
                            .build();

                    //when
                    Boolean result = questionService.solveQuestion(member.getMemberId(), request);

                    //then
                    assertThat(result).isFalse();
                }),
                dynamicTest("두 번째는 정답을 맞추고 true 를 반환받는다.", ()-> {
                    //given
                    AnswerCreateServiceRequest request = AnswerCreateServiceRequest.builder()
                            .questionId(question.getQuestionId())
                            .myAnswer(question.getQuestionAnswer())
                            .build();

                    //when
                    Boolean result = questionService.solveQuestion(member.getMemberId(), request);

                    //then
                    assertThat(result).isTrue();
                }),
                dynamicTest("answer 는 하나만 저장되고 마지막으로 푼 결과가 저장된다.", ()-> {
                    //when & then
                    assertThat(answerRepository.count()).isEqualTo(1);
                    Answer answer = answerRepository.findAll().get(0);
                    assertThat(answer.getAnswerStatus()).isEqualTo(AnswerStatus.CORRECT);
                    assertThat(answer.getMyAnswer()).isEqualTo(question.getQuestionAnswer());
                })
        );
    }

    @Test
    @DisplayName("강의를 구매하지 않았으면 Question 을 풀 수 없다.")
    void solveAnswerVideoNotPurchasedException() {
        //given
        Member channelOwner = createAndSaveMember();
        Channel channel = createAndSaveChannel(channelOwner);
        Video video = createAndSavePurchasedVideo(channelOwner, channel);

        List<Question> questions = createAndSaveQuestions(video);
        Question question = questions.get(0);

        Member member = createAndSaveMember(); // member 는 강의를 구매하지 않음

        AnswerCreateServiceRequest request = AnswerCreateServiceRequest.builder()
                .questionId(question.getQuestionId())
                .myAnswer(question.getQuestionAnswer())
                .build();

        //when & then
        assertThatThrownBy(() ->
                questionService.solveQuestion(member.getMemberId(), request))
                .isInstanceOf(VideoNotPurchasedException.class);
    }

    @Test
    @DisplayName("Question 을 풀 때 questionId 가 유효하지 않으면 QuestionNotFoundException 이 발생한다.")
    void solveQuestionQuestionNotFoundException() {
        //given
        Member channelOwner = createAndSaveMember();
        Channel channel = createAndSaveChannel(channelOwner);
        Video video = createAndSavePurchasedVideo(channelOwner, channel);

        List<Question> questions = createAndSaveQuestions(video);
        Question question = questions.get(0);

        Member member = createAndSaveMember();
        createAndSaveOrderWithPurchase(member, List.of(video), 0); // member 가 구매

        AnswerCreateServiceRequest request = AnswerCreateServiceRequest.builder()
                .questionId(question.getQuestionId() + 999L) // 존재하지 않는 questionId
                .myAnswer(question.getQuestionAnswer())
                .build();

        //then
        assertThatThrownBy(() ->
                questionService.solveQuestion(member.getMemberId(), request))
                .isInstanceOf(QuestionNotFoundException.class);
    }

    @Test
    @DisplayName("Video 에 있는 모든 Question 을 푼다.")
    void solveQuestions() {
        //given
        Member channelOwner = createAndSaveMember();
        Channel channel = createAndSaveChannel(channelOwner);
        Video video = createAndSavePurchasedVideo(channelOwner, channel);

        List<Question> questions = createAndSaveQuestions(video);

        Member member = createAndSaveMember();
        createAndSaveOrderWithPurchase(member, List.of(video), 0); // member 가 구매

        List<String> myAnswer = questions.stream()
                .map(Question::getQuestionAnswer)
                .collect(Collectors.toList());

        myAnswer.set(0, "111"); // 첫 번째 문제는 틀린 답을 입력

        //when
        List<Boolean> results = questionService.solveQuestions(member.getMemberId(), video.getVideoId(), myAnswer);

        //then
        assertThat(results).hasSize(5)
                .containsExactly(false, true, true, true, true);
    }

    @Test
    @DisplayName("Video 를 구매하지 않으면 Question 을 풀 수 없다. (VideoNotPurchasedException)")
    void solveQuestionsVideoAccessDeniedException() {
        //given
        Member channelOwner = createAndSaveMember();
        Channel channel = createAndSaveChannel(channelOwner);
        Video video = createAndSavePurchasedVideo(channelOwner, channel);

        List<Question> questions = createAndSaveQuestions(video);

        Member member = createAndSaveMember(); // 구매하지 않은 member

        List<String> myAnswer = questions.stream()
                .map(Question::getQuestionAnswer)
                .collect(Collectors.toList());

        myAnswer.set(0, "111"); // 첫 번째 문제는 틀린 답을 입력

        //when & then
        assertThatThrownBy(() ->
                questionService.solveQuestions(member.getMemberId(), video.getVideoId(), myAnswer))
                .isInstanceOf(VideoNotPurchasedException.class);

        //then (answer 는 하나도 저장되지 않는다.)
        assertThat(answerRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Video 에 있는 Question 을 풀 때 question 개수와 입력한 myAnswer 개수가 다르면 AnswerCountException 이 발생한다.")
    void solveQuestionsAnswerCountException() {
        //given
        Member channelOwner = createAndSaveMember();
        Channel channel = createAndSaveChannel(channelOwner);
        Video video = createAndSavePurchasedVideo(channelOwner, channel);

        List<Question> questions = createAndSaveQuestions(video);

        Member member = createAndSaveMember();
        createAndSaveOrderWithPurchase(member, List.of(video), 0); // member 가 구매

        List<String> myAnswer = questions.stream()
                .map(Question::getQuestionAnswer)
                .collect(Collectors.toList());

        myAnswer.remove(0); // myAnswer 개수가 question 개수보다 1개 적음

        //when & then
        assertThatThrownBy(() ->
                questionService.solveQuestions(member.getMemberId(), video.getVideoId(), myAnswer))
                .isInstanceOf(AnswerCountException.class);

        //then (answer 는 하나도 저장되지 않는다.)
        assertThat(answerRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("존재하지 않는 Video 의 Question 을 풀려고 하면 VideoNotFoundException 이 발생한다.")
    void solveQuestionsVideoNotFoundException() {
        //given
        Member channelOwner = createAndSaveMember();
        Channel channel = createAndSaveChannel(channelOwner);
        Video video = createAndSavePurchasedVideo(channelOwner, channel);

        List<Question> questions = createAndSaveQuestions(video);

        Member member = createAndSaveMember();
        createAndSaveOrderWithPurchase(member, List.of(video), 0); // member 가 구매

        List<String> myAnswer = questions.stream()
                .map(Question::getQuestionAnswer)
                .collect(Collectors.toList());

        //when & then
        assertThatThrownBy(() -> // 존재하지 않는 videoId
                questionService.solveQuestions(member.getMemberId(), video.getVideoId() + 999L, myAnswer))
                .isInstanceOf(VideoNotFoundException.class);

        //then (answer 는 하나도 저장되지 않는다.)
        assertThat(answerRepository.count()).isEqualTo(0);
    }

    private List<Question> createAndSaveQuestions(Video video) {

        List<Question> questions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            questions.add(Question.builder()
                    .position(i)
                    .content("content" + i)
                    .questionAnswer(String.valueOf(i))
                    .selections(List.of("1", "2", "3", "4", "5"))
                    .video(video)
                    .build());
        }

        questionRepository.saveAll(questions);

        return questions;

    }
}