package com.server.domain.question.service;

import com.server.domain.question.service.dto.response.QuestionResponse;
import com.server.domain.question.service.dto.request.AnswerCreateServiceRequest;
import com.server.domain.question.service.dto.request.QuestionUpdateServiceRequest;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {
    public QuestionResponse getQuestion(Long loninMemberId, Long questionId) {
        return null;
    }

    public void updateQuestion(Long loginMemberId, QuestionUpdateServiceRequest request) {

    }

    public void deleteQuestion(Long loginMemberId, Long questionId) {

    }

    public Long solveQuestion(Long loginMemberId, AnswerCreateServiceRequest serviceRequest) {
        return null;
    }
}
