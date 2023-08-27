package com.server.domain.video.aop;

import com.server.domain.answer.entity.AnswerStatus;
import com.server.domain.question.service.dto.response.QuestionResponse;
import com.server.domain.video.controller.dto.request.AnswersCreateApiRequest;
import com.server.domain.video.controller.dto.request.QuestionCreateApiRequest;
import com.server.global.reponse.ApiSingleResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.http.HttpStatus.CREATED;

@Aspect
@Component
public class VideoStubAop {

    @Around("execution(* com.server.domain.video.controller.VideoController.getQuestions(..))")
    public Object getQuestions(ProceedingJoinPoint joinPoint) {

        List<QuestionResponse> questions = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            QuestionResponse questionResponse = QuestionResponse.builder()
                    .questionId((long) i)
                    .position(i)
                    .content("content" + i)
                    .myAnswer("2")
                    .questionAnswer(String.valueOf(i))
                    .answerStatus(AnswerStatus.WRONG)
                    .description("description")
                    .selections(List.of("selection1", "selection2", "selection3"))
                    .solvedDate(LocalDateTime.now())
                    .build();

            questions.add(questionResponse);
        }

        return ResponseEntity.ok(ApiSingleResponse.ok(questions, "질문 목록 조회 성공"));
    }

    @Around("execution(* com.server.domain.video.controller.VideoController.solveQuestions(..))")
    public Object solveQuestions(ProceedingJoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();
        AnswersCreateApiRequest request = (AnswersCreateApiRequest) args[1];

        List<Boolean> answers = new ArrayList<>();

        for (int i = 0; i < request.getMyAnswers().size(); i++) {
            if(i % 2 == 0) {
                answers.add(true);
            } else {
                answers.add(false);
            }
        }

        return ResponseEntity.ok(ApiSingleResponse.ok(answers, "문제 제출 성공"));
    }

    @Around("execution(* com.server.domain.video.controller.VideoController.createQuestions(..))")
    public Object createQuestions(ProceedingJoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();
        Long videoId = (Long) args[0];
        List<QuestionCreateApiRequest> requests = (List<QuestionCreateApiRequest>) args[1];

        List<Long> questionIds = IntStream.range(1, requests.size() + 1)
                .mapToObj(Long::valueOf)
                .collect(Collectors.toList());



        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/videos/" + videoId + "/questions");

        return new ResponseEntity<>(
                ApiSingleResponse.of(questionIds, CREATED, "질문 생성 성공"),
                headers,
                CREATED);
    }


}
