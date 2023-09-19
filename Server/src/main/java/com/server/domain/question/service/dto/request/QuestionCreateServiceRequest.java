package com.server.domain.question.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class QuestionCreateServiceRequest {
    private String content;
    private String questionAnswer;
    private String description;
    private List<String> selections;
}
