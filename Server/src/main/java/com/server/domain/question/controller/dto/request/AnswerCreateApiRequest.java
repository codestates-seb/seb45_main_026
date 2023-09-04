package com.server.domain.question.controller.dto.request;

import com.server.domain.question.service.dto.request.AnswerCreateServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class AnswerCreateApiRequest {

    @NotBlank(message = "{validation.question.myAnswer}")
    private String myAnswer;

    public AnswerCreateServiceRequest toServiceRequest(Long questionId) {
        return AnswerCreateServiceRequest.builder()
                .questionId(questionId)
                .myAnswer(myAnswer)
                .build();
    }
}
