package com.server.domain.question.service.dto.response;

import com.server.domain.answer.entity.AnswerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
@Builder
public class QuestionResponse {

    private Long questionId;

    private int position;

    private String content;

    private String myAnswer;

    private String questionAnswer;

    private AnswerStatus answerStatus;

    private String description;

    private List<String> selections;

}
