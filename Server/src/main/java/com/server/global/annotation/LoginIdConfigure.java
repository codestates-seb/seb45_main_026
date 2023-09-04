package com.server.global.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.server.auth.jwt.service.CustomUserDetails;

public class LoginIdConfigure implements HandlerMethodArgumentResolver {
	@Override
	public boolean supportsParameter(MethodParameter parameter) {

		boolean hasLoginAccountIdAnnotation = parameter.hasParameterAnnotation(LoginId.class);
		boolean hasLongType = Long.class.isAssignableFrom(parameter.getParameterType());

		return hasLoginAccountIdAnnotation && hasLongType;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		// 테스트 시 등록된 회원이 아무도 없을 경우 널포인터 예외 방지를 위해 추가
		if(SecurityContextHolder.getContext().getAuthentication() == null) {
			return -1L;
		}

		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal == "anonymousUser") {
			return -1L;
		}

		CustomUserDetails customUserDetails = (CustomUserDetails) principal;
		return customUserDetails.getMemberId();

	}

}
