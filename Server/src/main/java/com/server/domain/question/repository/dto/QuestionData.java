package com.server.domain.question.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.server.domain.answer.entity.AnswerStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class QuestionData {

    private Long questionId;

    private int position;

    private String content;

    private String questionAnswer;

    private Long answerId;

    private String myAnswer;

    private AnswerStatus answerStatus;

    private List<String> selections = new ArrayList<>();

    private String description;

    private LocalDateTime solvedDate;

    @QueryProjection
    public QuestionData(Long questionId, int position, String content, String questionAnswer, Long answerId, String myAnswer, AnswerStatus answerStatus, String description, LocalDateTime solvedDate) {
        this.questionId = questionId;
        this.position = position;
        this.content = content;
        this.questionAnswer = questionAnswer;
        this.answerId = answerId;
        this.myAnswer = myAnswer;
        this.answerStatus = answerStatus;
        this.description = description;
        this.solvedDate = solvedDate;
    }

    public void setSelections(List<String> selections) {
        this.selections = selections;
    }
}
