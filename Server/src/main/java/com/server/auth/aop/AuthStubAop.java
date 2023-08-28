package com.server.auth.aop;

import static com.server.auth.util.AuthConstant.*;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthStubAop {
	@Around("execution(* com.server.auth.controller.AuthController.oauth(..))")
	public ResponseEntity<Void> oauth(ProceedingJoinPoint pjp) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(AUTHORIZATION, BEARER + " " + "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkYTlkYWNAZ21haWwuY29tIiwiaWQiOjEsImF1dGgiOiJST0xFX1VTRVIiLCJleHAiOjE2OTMxMzgzODF9.jJoeYVybHPom1aUJZhLBJ2v1yc0kYZ6Wq15tDuIMU67VKB_ogqxc30E1TQibuI0F-qg20gBtmxA5LxaRPT-c1w");
		headers.add(REFRESH, BEARER + " " + "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkYTlkYWNAZ21haWwuY29tIiwiaWQiOjEsImV4cCI6MTY5NDI3MjQ4Nn0.TDg6ZiSOCKtzkTkrXaue1JxNKHkcD2dlucojVmz9iR0dSQQqKFjXtmLVAUB7wMsubADd39Mrp6empmoEvFl38A");
		return ResponseEntity.ok().headers(headers).build();
	}

	@Around("execution(* com.server.auth.controller.AuthController.updatePassword(..))")
	public ResponseEntity<Void> updatePassword(ProceedingJoinPoint pjp) {
		return ResponseEntity.noContent().build();
	}
}
