package com.server.domain.question.service;

import com.server.domain.answer.entity.Answer;
import com.server.domain.answer.entity.AnswerStatus;
import com.server.domain.answer.repository.AnswerRepository;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.question.entity.Question;
import com.server.domain.question.repository.QuestionRepository;
import com.server.domain.question.repository.dto.QuestionData;
import com.server.domain.question.service.dto.request.QuestionCreateServiceRequest;
import com.server.domain.question.service.dto.response.QuestionResponse;
import com.server.domain.question.service.dto.request.AnswerCreateServiceRequest;
import com.server.domain.question.service.dto.request.QuestionUpdateServiceRequest;
import com.server.domain.reward.service.RewardService;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import com.server.global.exception.businessexception.answerexception.AnswerCountException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.questionexception.QuestionNotFoundException;
import com.server.global.exception.businessexception.videoexception.VideoAccessDeniedException;
import com.server.global.exception.businessexception.videoexception.VideoNotFoundException;
import com.server.global.exception.businessexception.videoexception.VideoNotPurchasedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Comparator.*;

@Service
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;
    private final AnswerRepository answerRepository;
    private final VideoRepository videoRepository;
    private final RewardService rewardService;

    public QuestionService(QuestionRepository questionRepository,
                           MemberRepository memberRepository,
                           AnswerRepository answerRepository,
                           VideoRepository videoRepository,
                           RewardService rewardService) {
        this.questionRepository = questionRepository;
        this.memberRepository = memberRepository;
        this.answerRepository = answerRepository;
        this.videoRepository = videoRepository;
        this.rewardService = rewardService;
    }

    public QuestionResponse getQuestion(Long loginMemberId, Long questionId) {

        checkQuestionGetAuthority(loginMemberId, questionId);

        QuestionData questionData = getQuestionData(loginMemberId, questionId);

        return QuestionResponse.of(questionData);
    }

    public List<QuestionResponse> getQuestions(Long loginMemberId, Long videoId) {

        checkVideoGetAuthority(loginMemberId, videoId);

        List<QuestionData> questionDatas = getQuestionDatas(loginMemberId, videoId);

        return QuestionResponse.of(questionDatas);
    }

    @Transactional
    public List<Long> createQuestions(Long loginMemberId,
                                      Long videoId,
                                      List<QuestionCreateServiceRequest> requests) {

        Video video = checkVideoModifyingAuthority(loginMemberId, videoId);

        List<Question> createdQuestions = createQuestions(video, requests);

        return createdQuestions.stream()
                .map(Question::getQuestionId)
                .collect(Collectors.toList());
    }

    private List<Question> createQuestions(Video video, List<QuestionCreateServiceRequest> requests) {

        int questionStartPosition = video.getQuestions().size() + 1;
        int questionLastPosition = questionStartPosition + requests.size();

        return IntStream.range(questionStartPosition, questionLastPosition)
                .mapToObj(index -> createQuestion(video, requests.get(index - questionStartPosition), index))
                .collect(Collectors.toList());
    }

    private Question createQuestion(Video video, QuestionCreateServiceRequest request, int position) {
        Question question = Question.createQuestion(
                position,
                request.getContent(),
                request.getQuestionAnswer(),
                request.getDescription(),
                request.getSelections(),
                video
        );
        return questionRepository.save(question);
    }

    @Transactional
    public void updateQuestion(Long loginMemberId, QuestionUpdateServiceRequest request) {

        Question question = checkQuestionModifyingAuthority(loginMemberId, request.getQuestionId());

        question.update(
                request.getContent(),
                request.getQuestionAnswer(),
                request.getDescription(),
                request.getSelections()
        );
    }

    @Transactional
    public void deleteQuestion(Long loginMemberId, Long questionId) {

        Question question = checkQuestionModifyingAuthority(loginMemberId, questionId);

        question.sortExceptThis();

        questionRepository.deleteById(questionId);
    }

    @Transactional
    public List<Boolean> solveQuestions(Long loginMemberId, Long videoId, List<String> myAnswers) {

        checkVideoGetAuthority(loginMemberId, videoId);

        List<Question> questions = questionRepository.findQuestionsWithVideoByVideoId(videoId);

        if(questions.size() != myAnswers.size()) {
            throw new AnswerCountException();
        }

        List<Answer> answers = createOrGetAnswers(loginMemberId, questions);

        solveQuestions(myAnswers, answers);

        getRewards(loginMemberId, answers);

        return answers.stream()
                .map(Answer::getAnswerStatus)
                .map(answerStatus -> answerStatus.equals(AnswerStatus.CORRECT))
                .collect(Collectors.toList());
    }

    @Transactional
    public Boolean solveQuestion(Long loginMemberId, AnswerCreateServiceRequest request) {

        checkQuestionGetAuthority(loginMemberId, request.getQuestionId());

        Answer myAnswer = createOrGetAnswer(loginMemberId, request.getQuestionId());

        myAnswer.solveAnswer(request.getMyAnswer());

        getReward(myAnswer);

        return myAnswer.isCorrect();
    }

    private void solveQuestions(List<String> myAnswers, List<Answer> answers) {

        IntStream.range(0, myAnswers.size())
                .forEach(i -> answers.get(i).solveAnswer(myAnswers.get(i))
        );
    }

    private List<Question> correctQuestionsFrom(List<Answer> answers) {
        return answers.stream()
                .filter(Answer::isCorrect)
                .map(Answer::getQuestion)
                .collect(Collectors.toList());
    }

    private void getRewards(Long loginMemberId, List<Answer> answers) {

        List<Question> correctAnsweredQuestions = correctQuestionsFrom(answers);

        rewardService.createQuestionRewardsIfNotPresent(correctAnsweredQuestions, verifiedMember(loginMemberId));
    }

    private void getReward(Answer answer) {

        if(answer.isCorrect())
            rewardService.createRewardIfNotPresent(answer.getQuestion(), answer.getMember());
    }

    private List<Long> getCreatedAnswerQuestionIds(List<Answer> answers) {
        return answers.stream()
                .map(answer -> answer.getQuestion().getQuestionId())
                .collect(Collectors.toList());
    }

    private List<Long> getQuestionIds(List<Question> questions) {
        return questions.stream()
                .map(Question::getQuestionId)
                .collect(Collectors.toList());
    }

    private List<Answer> createOrGetAnswers(Long loginMemberId, List<Question> questions) {

        Member member = verifiedMember(loginMemberId);

        List<Answer> answers = answerRepository.findByMemberIdAndQuestionIds(
                member.getMemberId(),
                getQuestionIds(questions));

        List<Long> createdAnswerQuestionIds = getCreatedAnswerQuestionIds(answers);

        return questions.stream().map(question ->
                        createdAnswerQuestionIds.contains(question.getQuestionId()) ?
                                findAnswerInQuestion(answers, question) : createAnswer(member, question))
                .sorted(comparingInt(answer -> answer.getQuestion().getPosition()))
                .collect(Collectors.toList());
    }

    private Answer createOrGetAnswer(Long memberId, Long questionId) {

        return getAnswer(memberId, questionId)
                .orElseGet(() -> {

                    Member member = verifiedMember(memberId);
                    Question question = verifiedQuestion(questionId);

                    return createAnswer(member, question);
                });
    }

    private Answer findAnswerInQuestion(List<Answer> answers, Question question) {
        return answers.stream()
                .filter(answer -> answer.getQuestion().equals(question))
                .findFirst().orElseThrow(QuestionNotFoundException::new);
    }

    private Optional<Answer> getAnswer(Long memberId, Long questionId) {
        return answerRepository.findByMemberIdAndQuestionId(memberId, questionId);
    }

    private Answer createAnswer(Member member, Question question) {
        Answer answer = Answer.createAnswer(
                null,
                member,
                question
        );
        return answerRepository.save(answer);
    }

    private void checkQuestionGetAuthority(Long loginMemberId, Long questionId) {
        Video video = questionRepository.findVideoByQuestionId(questionId)
                .orElseThrow(QuestionNotFoundException::new);

        if(video.isOwnedBy(loginMemberId)) return;

        Boolean isPurchased = memberRepository.checkMemberPurchaseVideo(loginMemberId, video.getVideoId());
        if(!isPurchased) throw new VideoNotPurchasedException();
    }

    private void checkVideoGetAuthority(Long loginMemberId, Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(VideoNotFoundException::new);

        if(video.isOwnedBy(loginMemberId)) return;

        Boolean isPurchased = memberRepository.checkMemberPurchaseVideo(loginMemberId, video.getVideoId());
        if(!isPurchased) throw new VideoNotPurchasedException();
    }

    private Question checkQuestionModifyingAuthority(Long loginMemberId, Long questionId) {

        Video video = questionRepository.findVideoByQuestionId(questionId)
                .orElseThrow(QuestionNotFoundException::new);

        if(!video.isOwnedBy(loginMemberId))
            throw new VideoAccessDeniedException();

        return video.getQuestions().stream()
                .filter(question -> question.getQuestionId().equals(questionId))
                .findFirst()
                .orElseThrow(QuestionNotFoundException::new);
    }

    private QuestionData getQuestionData(Long loginMemberId, Long questionId) {
        return questionRepository.findQuestionDataWithMemberAnswer(loginMemberId, questionId)
                .orElseThrow(QuestionNotFoundException::new);
    }

    private List<QuestionData> getQuestionDatas(Long loginMemberId, Long videoId) {
        List<QuestionData> datas = questionRepository.findQuestionDatasWithMemberAnswerByVideoId(loginMemberId, videoId);
        return datas.stream().sorted(comparingInt(QuestionData::getPosition)).collect(Collectors.toList());
    }

    private Video checkVideoModifyingAuthority(Long loginMemberId, Long videoId) {
        Video video = questionRepository.findVideoWIthMemberAndQuestions(videoId)
                .orElseThrow(VideoNotFoundException::new);

        if(!video.getMemberId().equals(loginMemberId))
            throw new VideoAccessDeniedException();

        return video;
    }

    private Member verifiedMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    private Question verifiedQuestion(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(QuestionNotFoundException::new);
    }
}
