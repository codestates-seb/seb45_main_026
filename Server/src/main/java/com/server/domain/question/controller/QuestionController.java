package com.server.domain.question.controller;

import com.server.domain.question.controller.dto.request.AnswerCreateApiRequest;
import com.server.domain.question.service.dto.response.QuestionResponse;
import com.server.domain.question.controller.dto.request.QuestionUpdateApiRequest;
import com.server.domain.question.service.QuestionService;
import com.server.global.reponse.ApiSingleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;

@RequestMapping("/questions")
@RestController
@Validated
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/{question-id}") //개별 질문 조회 사용 x
    public ResponseEntity<ApiSingleResponse<QuestionResponse>> getQuestion(
            @PathVariable("question-id") @Positive Long questionId) {

        Long loginMemberId = 1L;

        QuestionResponse questionResponse = questionService.getQuestion(loginMemberId, questionId);

        return ResponseEntity.ok(ApiSingleResponse.ok(questionResponse, "질문 조회 성공"));
    }

    @PatchMapping("/{question-id}")
    public ResponseEntity<Void> updateQuestion(
            @PathVariable("question-id") @Positive Long questionId,
            @RequestBody @Valid QuestionUpdateApiRequest request) {
        //질문 수정 + 선택지 수정 포함
        Long loginMemberId = 1L;

        questionService.updateQuestion(loginMemberId, request.toServiceRequest(questionId));

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{question-id}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable("question-id") @Positive Long questionId) {
        //질문 삭제
        Long loginMemberId = 1L;

        questionService.deleteQuestion(loginMemberId, questionId);

        return ResponseEntity.noContent().build();
    }

    //질문 풀기
    @PostMapping("/{question-id}/answers")
    public ResponseEntity<Void> solveQuestion(
            @PathVariable("question-id") @Positive Long questionId,
            @RequestBody @Valid AnswerCreateApiRequest request) {

        Long loginMemberId = 1L;

        questionService.solveQuestion(loginMemberId, request.toServiceRequest(questionId));

        URI uri = URI.create("/questions/" + questionId);

        return ResponseEntity.created(uri).build();
    }
}
