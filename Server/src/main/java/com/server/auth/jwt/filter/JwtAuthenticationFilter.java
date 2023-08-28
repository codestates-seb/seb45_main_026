package com.server.auth.jwt.filter;

import static com.server.auth.util.AuthConstant.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.auth.controller.dto.AuthApiRequest;
import com.server.auth.jwt.service.JwtProvider;

import lombok.SneakyThrows;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final JwtProvider jwtProvider;
	private final AuthenticationManager authenticationManager;

	public JwtAuthenticationFilter(JwtProvider jwtProvider, AuthenticationManager authenticationManager) {
		this.jwtProvider = jwtProvider;
		this.authenticationManager = authenticationManager;
	}

	@SneakyThrows
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

		checkRequestPOST(request);

		AuthApiRequest.Login loginDto = getLoginDtoFrom(request);

		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

		return authenticationManager.authenticate(authenticationToken);
	}

	private void checkRequestPOST(HttpServletRequest request) {
		if (!request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}
	}

	private AuthApiRequest.Login getLoginDtoFrom(HttpServletRequest request) throws IOException {
		return new ObjectMapper().readValue(request.getInputStream(), AuthApiRequest.Login.class);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain chain,
		Authentication authentication) throws ServletException, IOException {

		String accessToken = jwtProvider.createAccessToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
		String refreshToken = jwtProvider.createRefreshToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);

		response.setHeader(AUTHORIZATION, BEARER + " " + accessToken);
		response.setHeader(REFRESH, BEARER + " " + refreshToken);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("Authentication successful");
	}
}
