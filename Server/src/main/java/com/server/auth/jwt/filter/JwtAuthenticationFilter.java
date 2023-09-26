package com.server.auth.jwt.filter;

import static com.server.auth.util.AuthConstant.*;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.server.auth.jwt.service.CustomUserDetails;
import com.server.global.exception.businessexception.memberexception.MemberBlockedException;
import com.server.module.redis.service.RedisService;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.auth.controller.dto.AuthApiRequest;
import com.server.auth.jwt.service.JwtProvider;
import com.server.auth.util.AuthUtil;
import com.server.global.exception.businessexception.BusinessException;
import com.server.global.exception.businessexception.authexception.LoginEmailNullException;
import com.server.global.exception.businessexception.authexception.LoginEmailValidException;
import com.server.global.exception.businessexception.authexception.LoginPasswordNullException;
import com.server.global.exception.businessexception.authexception.LoginPasswordSizeException;
import com.server.global.exception.businessexception.authexception.LoginPasswordValidException;
import com.server.global.exception.businessexception.memberexception.MemberBadCredentialsException;
import com.server.global.exception.businessexception.memberexception.MemberDisabledException;

import lombok.SneakyThrows;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final JwtProvider jwtProvider;
	private final AuthenticationManager authenticationManager;
	private final RedisService redisService;

	public JwtAuthenticationFilter(JwtProvider jwtProvider, AuthenticationManager authenticationManager, RedisService redisService) {
		this.jwtProvider = jwtProvider;
		this.authenticationManager = authenticationManager;
		this.redisService = redisService;
	}

	@SneakyThrows
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

		checkRequestPOST(request);

		try {
			AuthApiRequest.Login loginDto = getLoginDtoFrom(request);

			UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());


			Authentication authenticate = authenticationManager.authenticate(authenticationToken);

			CustomUserDetails principal = (CustomUserDetails) authenticate.getPrincipal();

			Long memberId = principal.getMemberId();

			if(redisService.isExist(memberId.toString())) {

				String reason = redisService.getData(memberId.toString());

				throw new MemberBlockedException(reason);
			}

			return authenticate;

		} catch (BadCredentialsException badCredentialsException) {
			AuthUtil.setResponse(response, new MemberBadCredentialsException());
			return null;
		} catch (DisabledException disabledException) {
			AuthUtil.setResponse(response, new MemberDisabledException());
			return null;
		} catch (BusinessException businessException) {
			AuthUtil.setResponse(response, businessException);
			return null;
		} catch (Exception exception) {
			AuthUtil.setResponse(response);
			return null;
		}
	}

	private void checkRequestPOST(HttpServletRequest request) {
		if (!request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}
	}

	private AuthApiRequest.Login getLoginDtoFrom(HttpServletRequest request) throws IOException {
		AuthApiRequest.Login login = new ObjectMapper().readValue(request.getInputStream(), AuthApiRequest.Login.class);

		isValidLoginDto(login);

		return login;
	}

	private static void isValidLoginDto(AuthApiRequest.Login login) {
		String email = login.getEmail();
		String password = login.getPassword();

		if (email == null) {
			throw new LoginEmailNullException();
		} else if (!Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email)) {
			throw new LoginEmailValidException();
		} else if (password == null) {
			throw new LoginPasswordNullException();
		} else if (password.length() < 9 || password.length() > 20) {
			throw new LoginPasswordSizeException();
		} else if (!Pattern.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$", password)) {
			throw new LoginPasswordValidException();
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain chain,
		Authentication authentication) throws ServletException, IOException {

		String accessToken = jwtProvider.createAccessToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
		String refreshToken = jwtProvider.createRefreshToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);

		response.setHeader(AUTHORIZATION, BEARER + accessToken);
		response.setHeader(REFRESH, BEARER + refreshToken);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("Authentication successful");
	}
}
