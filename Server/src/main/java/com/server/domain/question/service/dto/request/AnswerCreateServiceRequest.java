package com.server.domain.question.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class AnswerCreateServiceRequest {

    private Long questionId;

    private String myAnswer;
}
