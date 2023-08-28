package com.server.domain.member.aop;

import java.time.LocalDateTime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.server.domain.member.service.dto.response.ProfileResponse;
import com.server.global.reponse.ApiSingleResponse;

@Aspect
@Component
public class MemberStubAop {

	@Around("execution(* com.server.domain.member.controller.MemberController.getMember(..))")
	public ResponseEntity<ApiSingleResponse<ProfileResponse>> getMember(ProceedingJoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs(); // 파라미터로 전달 받은 값들 0 = memberId, 1 = loginId
		Long memberId = (Long) args[0];

		ProfileResponse profileResponse = ProfileResponse.builder()
			.memberId(memberId)
			.email("stub@email.com")
			.nickname("stubName")
			.imageUrl("https://s3_url")
			.reward(777)
			.createdDate(LocalDateTime.now())
			.build();

		return ResponseEntity.ok(ApiSingleResponse.ok(profileResponse, "프로필 조회 성공"));
	}
}
