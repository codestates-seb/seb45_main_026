package com.server.domain.video.controller.dto.request;

import com.server.domain.question.service.dto.request.QuestionCreateServiceRequest;
import com.server.global.validation.EachNotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Getter
public class QuestionCreateApiRequest {
    @Positive(message = "{validation.positive}")
    @NotNull(message = "{validation.question.position}")
    private Integer position;
    @NotBlank(message = "{validation.question.content}")
    private String content;
    @NotBlank(message = "{validation.question.questionAnswer}")
    private String questionAnswer;
    private String description;
    @EachNotBlank(message = "{validation.question.selections.eachNotBlank}")
    @Size(min = 1, max = 4, message = "{validation.question.selections.size}")
    private List<String> selections;

    public QuestionCreateServiceRequest toServiceRequest() {
        return QuestionCreateServiceRequest.builder()
                .position(position)
                .content(content)
                .questionAnswer(questionAnswer)
                .description(description)
                .selections(selections)
                .build();
    }

    public static List<QuestionCreateServiceRequest> toServiceRequests(List<QuestionCreateApiRequest> requests) {
        return requests.stream()
                .map(QuestionCreateApiRequest::toServiceRequest)
                .collect(Collectors.toList());
    }
}
