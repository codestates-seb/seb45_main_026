package com.server.domain.question.service;

import com.server.domain.member.repository.MemberRepository;
import com.server.domain.question.entity.Question;
import com.server.domain.question.repository.QuestionRepository;
import com.server.domain.question.repository.dto.QuestionData;
import com.server.domain.question.service.dto.response.QuestionResponse;
import com.server.domain.question.service.dto.request.AnswerCreateServiceRequest;
import com.server.domain.question.service.dto.request.QuestionUpdateServiceRequest;
import com.server.domain.video.entity.Video;
import com.server.global.exception.businessexception.questionexception.QuestionNotFoundException;
import com.server.global.exception.businessexception.videoexception.VideoAccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;

    public QuestionService(QuestionRepository questionRepository, MemberRepository memberRepository) {
        this.questionRepository = questionRepository;
        this.memberRepository = memberRepository;
    }

    public QuestionResponse getQuestion(Long loginMemberId, Long questionId) {

        checkPurchased(loginMemberId, questionId);

        QuestionData questionData = getQuestionData(loginMemberId, questionId);

        return QuestionResponse.of(questionData);
    }

    private QuestionData getQuestionData(Long loginMemberId, Long questionId) {
        return questionRepository.findQuestionDataWithMemberAnswer(loginMemberId, questionId)
                .orElseThrow(QuestionNotFoundException::new);
    }

    public void updateQuestion(Long loginMemberId, QuestionUpdateServiceRequest request) {

        checkAuthority(loginMemberId, request.getQuestionId());

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

    public void deleteQuestion(Long loginMemberId, Long questionId) {
        checkAuthority(loginMemberId, questionId);

        questionRepository.deleteById(questionId);
    }

    public Boolean solveQuestion(Long loginMemberId, AnswerCreateServiceRequest request) {

        checkPurchased(loginMemberId, request.getQuestionId());

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(QuestionNotFoundException::new);

        //todo : 푼 기록이 있는지 확인
        //todo : 푼 기록이 없다면 푼 기록 추가

        if(question.getQuestionAnswer().equals(request.getMyAnswer())) {
            //todo : 정답일 경우 Answer 에 정답 처리
        };
        return false;
    }

    private void checkPurchased(Long loginMemberId, Long questionId) {
        Video video = questionRepository.findVideoByQuestionId(questionId)
                .orElseThrow(QuestionNotFoundException::new);

        Boolean isPurchased = memberRepository.checkMemberPurchaseVideo(loginMemberId, video.getVideoId());
        if(!isPurchased) throw new VideoAccessDeniedException();
    }

    private void checkAuthority(Long loginMemberId, Long questionId) {
        Video video = questionRepository.findVideoByQuestionId(questionId)
                .orElseThrow(QuestionNotFoundException::new);

        if(!video.getChannel().getMember().getMemberId().equals(loginMemberId))
            throw new VideoAccessDeniedException();
    }
}
