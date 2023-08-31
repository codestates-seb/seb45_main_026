package com.server.domain.order.aop;

import com.server.domain.order.controller.dto.response.PaymentApiResponse;
import com.server.domain.order.service.dto.response.OrderResponse;
import com.server.domain.question.service.dto.response.QuestionResponse;
import com.server.global.reponse.ApiSingleResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.UUID;

@Aspect
//@Component
public class OrderStubAop {

    @Around("execution(* com.server.domain.order.controller.OrderController.createOrder(..))")
    public Object createOrder(ProceedingJoinPoint joinPoint) {

        OrderResponse response = OrderResponse.builder()
                .orderId(UUID.randomUUID().toString())
                .totalAmount(50000)
                .build();

        return ResponseEntity.ok(ApiSingleResponse.ok(response, "주문 요청 정보"));
    }

    @Around("execution(* com.server.domain.order.controller.OrderController.success(..))")
    public Object success(ProceedingJoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();
        Integer totalAmount = (Integer) args[2];

        PaymentApiResponse response = PaymentApiResponse.builder()
                .orderName("티셔츠 외 2건")
                .status("DONE")
                .totalAmount(totalAmount)
                .build();

        return ResponseEntity.ok(ApiSingleResponse.ok(response, "결제 결과"));
    }

    @Around("execution(* com.server.domain.order.controller.OrderController.cancelOrder(..))")
    public Object cancelOrder(ProceedingJoinPoint joinPoint) {

        return ResponseEntity.noContent().build();
    }
}
