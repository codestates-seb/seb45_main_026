package com.server.domain.question.aop;

import com.server.domain.question.service.dto.response.QuestionResponse;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;

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
                .myAnswer("myAnswer")
                .questionAnswer("questionAnswer")
                .answerStatus(null)
                .description("description")
                .selections(null)
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
        Object[] args = joinPoint.getArgs();
        Long questionId = (Long) args[0];

        URI uri = URI.create("/questions/" + questionId);

        return ResponseEntity.created(uri).build();
    }

}
