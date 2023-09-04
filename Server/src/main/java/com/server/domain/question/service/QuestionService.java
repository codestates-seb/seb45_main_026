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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        checkQuestionPurchased(loginMemberId, questionId);

        QuestionData questionData = getQuestionData(loginMemberId, questionId);

        return QuestionResponse.of(questionData);
    }

    public List<QuestionResponse> getQuestions(Long loginMemberId, Long videoId) {

        checkVideoPurchased(loginMemberId, videoId);

        List<QuestionData> questionDatas = getQuestionDatas(loginMemberId, videoId);

        return QuestionResponse.of(questionDatas);
    }

    @Transactional
    public List<Long> createQuestions(Long loginMemberId,
                                      Long videoId,
                                      List<QuestionCreateServiceRequest> requests) {

        Video video = verifedVideo(loginMemberId, videoId);

        return requests.stream()
                .map(request -> {
                    Question question = Question.createQuestion(
                            request.getPosition(),
                            request.getContent(),
                            request.getQuestionAnswer(),
                            request.getDescription(),
                            request.getSelections(),
                            video
                    );
                    return questionRepository.save(question).getQuestionId();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateQuestion(Long loginMemberId, QuestionUpdateServiceRequest request) {

        checkQuestionAuthority(loginMemberId, request.getQuestionId());

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(QuestionNotFoundException::new);

        question.update(
                request.getPosition(),
                request.getContent(),
                request.getQuestionAnswer(),
                request.getDescription(),
                request.getSelections()
        );
    }

    @Transactional
    public void deleteQuestion(Long loginMemberId, Long questionId) {
        checkQuestionAuthority(loginMemberId, questionId);

        questionRepository.deleteById(questionId);
    }

    @Transactional
    public List<Boolean> solveQuestions(Long loginMemberId, Long videoId, List<String> myAnswers) {

        checkVideoPurchased(loginMemberId, videoId);

        List<Question> questions = questionRepository.findQuestionsWithVideoByVideoId(videoId);

        if(questions.size() != myAnswers.size()) {
            throw new AnswerCountException();
        }
        List<Answer> answers = createOrGetAnswersInPositionOrder(loginMemberId, questions);

        List<Boolean> results = solveQuestions(myAnswers, answers);

        List<Question> toGetRewards = correctQuestionsFrom(answers);

        rewardService.createQuestionRewardsIfNotPresent(toGetRewards, verifiedMember(loginMemberId));

        return results;
    }

    private List<Boolean> solveQuestions(List<String> myAnswers, List<Answer> answers) {
        return IntStream.range(0, myAnswers.size())
                .mapToObj(index -> answers.get(index).solveAnswer(myAnswers.get(index)))
                .collect(Collectors.toList());
    }

    private List<Question> correctQuestionsFrom(List<Answer> answers) {
        return answers.stream()
                .filter(answer -> answer.getAnswerStatus().equals(AnswerStatus.CORRECT))
                .map(Answer::getQuestion)
                .collect(Collectors.toList());
    }

    @Transactional
    public Boolean solveQuestion(Long loginMemberId, AnswerCreateServiceRequest request) {

        checkQuestionPurchased(loginMemberId, request.getQuestionId());

        Answer answer = createOrGetAnswer(loginMemberId, request.getQuestionId());

        boolean result = answer.solveAnswer(request.getMyAnswer());

        if(result) {
            getReward(answer);
        }

        return result;
    }

    private void getReward(Answer answer) {
        rewardService.createRewardIfNotPresent(answer.getQuestion(), answer.getMember());
    }

    private List<Answer> createOrGetAnswersInPositionOrder(Long loginMemberId, List<Question> questions) {

        Member member = verifiedMember(loginMemberId);

        return questions.stream().map(question ->
                getAnswer(member.getMemberId(), question.getQuestionId())
                .orElseGet(() -> createAnswer(member, question)))
                .sorted(Comparator.comparingInt(answer -> answer.getQuestion().getPosition()))
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

    private void checkQuestionPurchased(Long loginMemberId, Long questionId) {
        Video video = questionRepository.findVideoByQuestionId(questionId)
                .orElseThrow(QuestionNotFoundException::new);

        Boolean isPurchased = memberRepository.checkMemberPurchaseVideo(loginMemberId, video.getVideoId());
        if(!isPurchased) throw new VideoNotPurchasedException();
    }

    private void checkVideoPurchased(Long loginMemberId, Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(VideoNotFoundException::new);

        Boolean isPurchased = memberRepository.checkMemberPurchaseVideo(loginMemberId, video.getVideoId());
        if(!isPurchased) throw new VideoNotPurchasedException();
    }

    private void checkQuestionAuthority(Long loginMemberId, Long questionId) {
        Video video = questionRepository.findVideoByQuestionId(questionId)
                .orElseThrow(QuestionNotFoundException::new);

        if(!video.getChannel().getMember().getMemberId().equals(loginMemberId))
            throw new VideoAccessDeniedException();
    }

    private Video verifedVideo(Long loginMemberId, Long videoId) {
        Video video = videoRepository.findVideoWithMember(videoId)
                .orElseThrow(VideoNotFoundException::new);

        if(!video.getChannel().getMember().getMemberId().equals(loginMemberId))
            throw new VideoAccessDeniedException();

        return video;
    }

    private QuestionData getQuestionData(Long loginMemberId, Long questionId) {
        return questionRepository.findQuestionDataWithMemberAnswer(loginMemberId, questionId)
                .orElseThrow(QuestionNotFoundException::new);
    }

    private List<QuestionData> getQuestionDatas(Long loginMemberId, Long videoId) {
        List<QuestionData> datas = questionRepository.findQuestionDatasWithMemberAnswerByVideoId(loginMemberId, videoId);
        return datas.stream().sorted(Comparator.comparingInt(QuestionData::getPosition)).collect(Collectors.toList());
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
