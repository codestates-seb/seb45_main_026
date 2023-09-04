package com.server.domain.question.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class QuestionUpdateServiceRequest {

    private Long questionId;

    private Integer position;

    private String content;

    private String questionAnswer;

    private String description;

    private List<String> selections;
}
