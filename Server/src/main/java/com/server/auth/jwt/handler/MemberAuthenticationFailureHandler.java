package com.server.auth.jwt.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class MemberAuthenticationFailureHandler implements AuthenticationFailureHandler {
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException {

		/*
		1. 로그인이 실패할 때마다 레디스에 해당 이메일을 키로 가진 값에 +1 을 해주고
		5가 될 때마다 만료시간을 정해 그 시간까지 로그인하지 못하게 하는 기능 같은 것도 가능할지도
		 */
	}
}
