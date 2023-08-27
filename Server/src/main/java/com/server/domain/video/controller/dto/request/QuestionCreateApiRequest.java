package com.server.domain.video.controller.dto.request;

import com.server.domain.question.service.dto.request.QuestionCreateServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Getter
public class QuestionCreateApiRequest {
    private Integer position;
    private String content;
    private String questionAnswer;
    private String description;
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
