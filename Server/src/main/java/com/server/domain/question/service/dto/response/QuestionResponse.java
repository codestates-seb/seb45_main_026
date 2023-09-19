package com.server.domain.question.service.dto.response;

import com.server.domain.answer.entity.AnswerStatus;
import com.server.domain.question.repository.dto.QuestionData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


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

    private boolean choice;

    private LocalDateTime solvedDate;

    public static QuestionResponse of(QuestionData questionData) {
        return QuestionResponse.builder()
                .questionId(questionData.getQuestionId())
                .position(questionData.getPosition())
                .content(questionData.getContent())
                .questionAnswer(questionData.getQuestionAnswer())
                .myAnswer(questionData.getMyAnswer())
                .answerStatus(questionData.getAnswerStatus())
                .description(questionData.getDescription())
                .selections(questionData.getSelections())
                .choice(!questionData.getSelections().isEmpty())
                .solvedDate(questionData.getSolvedDate())
                .build();
    }

    public static List<QuestionResponse> of(List<QuestionData> questionDatas) {
        return questionDatas.stream()
                .map(QuestionResponse::of)
                .collect(Collectors.toList());
    }

}
