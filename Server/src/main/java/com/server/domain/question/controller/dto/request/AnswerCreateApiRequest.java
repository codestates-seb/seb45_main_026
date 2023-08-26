package com.server.domain.question.controller.dto.request;

import com.server.domain.question.service.dto.request.AnswerCreateServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class AnswerCreateApiRequest {

    private String myAnswer;


    public AnswerCreateServiceRequest toServiceRequest(Long questionId) {
        return null;
    }
}
