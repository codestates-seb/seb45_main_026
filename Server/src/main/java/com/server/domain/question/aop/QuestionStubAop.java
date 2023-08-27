package com.server.domain.question.aop;

import com.server.domain.answer.entity.AnswerStatus;
import com.server.domain.question.service.dto.response.QuestionResponse;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Aspect
@Component
public class QuestionStubAop {

    @Around("execution(* com.server.domain.question.controller.QuestionController.getQuestion(..))")
    public Object getQuestion(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long questionId = (Long) args[0];

        QuestionResponse questionResponse = QuestionResponse.builder()
                .questionId(questionId)
                .position(1)
                .content("content")
                .myAnswer("1")
                .questionAnswer("2")
                .answerStatus(AnswerStatus.WRONG)
                .description("description")
                .selections(List.of("selection1", "selection2", "selection3"))
                .solvedDate(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(ApiSingleResponse.ok(questionResponse, "질문 조회 성공"));
    }

    @Around("execution(* com.server.domain.question.controller.QuestionController.updateQuestion(..))")
    public Object updateQuestion(ProceedingJoinPoint joinPoint) {


        return ResponseEntity.noContent().build();
    }

    @Around("execution(* com.server.domain.question.controller.QuestionController.deleteQuestion(..))")
    public Object deleteQuestion(ProceedingJoinPoint joinPoint) {

        return ResponseEntity.noContent().build();
    }

    @Around("execution(* com.server.domain.question.controller.QuestionController.solveQuestion(..))")
    public Object solveQuestion(ProceedingJoinPoint joinPoint) {

        return ResponseEntity.ok(ApiSingleResponse.ok(true, "문제 제출 성공"));
    }

}
