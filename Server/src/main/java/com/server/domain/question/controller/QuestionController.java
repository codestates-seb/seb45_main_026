package com.server.domain.question.controller;

import com.server.domain.question.controller.dto.request.AnswerCreateApiRequest;
import com.server.domain.question.service.dto.response.QuestionResponse;
import com.server.domain.question.controller.dto.request.QuestionUpdateApiRequest;
import com.server.domain.question.service.QuestionService;
import com.server.global.annotation.LoginId;
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
            @PathVariable("question-id") @Positive Long questionId,
            @LoginId Long loginMemberId) {

        QuestionResponse questionResponse = questionService.getQuestion(loginMemberId, questionId);

        return ResponseEntity.ok(ApiSingleResponse.ok(questionResponse, "질문 조회 성공"));
    }

    @PatchMapping("/{question-id}")
    public ResponseEntity<Void> updateQuestion(
            @PathVariable("question-id") @Positive Long questionId,
            @RequestBody @Valid QuestionUpdateApiRequest request,
            @LoginId Long loginMemberId) {

        questionService.updateQuestion(loginMemberId, request.toServiceRequest(questionId));

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{question-id}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable("question-id") @Positive Long questionId,
            @LoginId Long loginMemberId) {

        questionService.deleteQuestion(loginMemberId, questionId);

        return ResponseEntity.noContent().build();
    }

    //질문 풀기
    @PostMapping("/{question-id}/answers")
    public ResponseEntity<ApiSingleResponse<Boolean>> solveQuestion(
            @PathVariable("question-id") @Positive Long questionId,
            @RequestBody @Valid AnswerCreateApiRequest request,
            @LoginId Long loginMemberId) {

        Boolean result = questionService.solveQuestion(loginMemberId, request.toServiceRequest(questionId));

        return ResponseEntity.ok(ApiSingleResponse.ok(result, "문제 제출 성공"));
    }
}
