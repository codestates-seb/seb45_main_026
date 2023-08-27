package com.server.domain.video.controller;

import com.server.domain.question.service.QuestionService;
import com.server.domain.question.service.dto.response.QuestionResponse;
import com.server.domain.video.controller.dto.request.AnswersCreateApiRequest;
import com.server.domain.video.controller.dto.request.QuestionCreateApiRequest;
import com.server.global.annotation.LoginId;
import com.server.global.reponse.ApiSingleResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequestMapping("/videos")
@RestController
@Validated
public class VideoController {

    private final QuestionService questionService;

    public VideoController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/{video-id}/questions")
    public ResponseEntity<ApiSingleResponse<List<QuestionResponse>>> getQuestions(
                             @PathVariable("video-id") Long videoId,
                             @LoginId Long loginMemberId) {

        List<QuestionResponse> questions = questionService.getQuestions(loginMemberId, videoId);

        return ResponseEntity.ok(ApiSingleResponse.ok(questions, "질문 목록 조회 성공"));
    }

    @PostMapping("/{video-id}/answers")
    public ResponseEntity<ApiSingleResponse<List<Boolean>>> solveQuestions(
            @PathVariable("video-id") Long videoId,
            @RequestBody AnswersCreateApiRequest request,
            @LoginId Long loginMemberId) {

        List<Boolean> answers = questionService.solveQuestions(loginMemberId, videoId, request.getMyAnswers());

        return ResponseEntity.ok(ApiSingleResponse.ok(answers, "문제 제출 성공"));
    }

    @PostMapping("/{video-id}/questions")
    public ResponseEntity<ApiSingleResponse<List<Long>>> createQuestions(
            @PathVariable("video-id") Long videoId,
            @RequestBody List<QuestionCreateApiRequest> requests,
            @LoginId Long loginMemberId) {


        List<Long> questionIds = questionService.createQuestions(
                loginMemberId,
                videoId,
                QuestionCreateApiRequest.toServiceRequests(requests));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/videos/" + videoId + "/questions");

        return new ResponseEntity<>(
                ApiSingleResponse.of(questionIds, CREATED, "질문 생성 성공"),
                headers,
                CREATED);
    }
}
