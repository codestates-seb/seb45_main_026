package com.server.domain.question.controller.dto.request;

import com.server.domain.question.service.dto.request.QuestionUpdateServiceRequest;
import com.server.global.validation.EachNotBlank;
import com.server.global.validation.OnlyNotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class QuestionUpdateApiRequest {

    @OnlyNotBlank(message = "{validation.question.content.notBlank}")
    private String content;

    @OnlyNotBlank(message = "{validation.question.questionAnswer.notBlank}")
    private String questionAnswer;

    private String description;

    @Size(min = 1, max = 4, message = "{validation.question.selections.size}")
    @EachNotBlank(message = "{validation.question.selections.eachNotBlank}")
    private List<String> selections;


    public QuestionUpdateServiceRequest toServiceRequest(Long questionId) {

        return QuestionUpdateServiceRequest.builder()
                .questionId(questionId)
                .content(content)
                .questionAnswer(questionAnswer)
                .description(description)
                .selections(selections)
                .build();
    }
}
