package com.server.domain.question.controller.dto.request;

import com.server.domain.question.service.dto.request.QuestionUpdateServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class QuestionUpdateApiRequest {

    private int position;

    private String content;

    private String questionAnswer;

    private String description;

    private List<String> selections;


    public QuestionUpdateServiceRequest toServiceRequest(Long questionId) {

        return null;
    }
}
