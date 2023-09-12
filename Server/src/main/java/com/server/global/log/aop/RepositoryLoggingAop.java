package com.server.global.log.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class RepositoryLoggingAop {

    @Pointcut("execution(* org.springframework.data.jpa.repository.JpaRepository.*(..)) " +
            "|| execution(* com.server.domain.*.repository..*RepositoryImpl.*(..))")
    public void repositoryLogging() {}

    @Around("repositoryLogging()")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {

        String classMethod = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        log.info("REPOSITORY : {} duration: {} ms ([{}])", classMethod, duration, Arrays.toString(args));

        return result;
    }
}
