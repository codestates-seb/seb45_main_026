package com.server.auth.jwt.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class MemberAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		/*
		1. 회원 엔티티에 방문일 추가해서 로그인할 때마다 1씩 올려주기?
		특정 방문일마다 보상 주기 같은거도 가능할듯
		근데 24시간에 한번만 올라가게 해야하는데 어떻게 할지는 생각해보기
		 */
	}
}
