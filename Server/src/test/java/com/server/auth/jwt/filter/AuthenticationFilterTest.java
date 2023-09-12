package com.server.auth.jwt.filter;

import static com.server.auth.util.AuthConstant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.auth.controller.dto.AuthApiRequest;
import com.server.auth.jwt.service.JwtProvider;
import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Member;
import com.server.global.testhelper.ServiceTest;


public class AuthenticationFilterTest extends ServiceTest {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Mock
	private JwtProvider jwtProvider;
	@Mock
	private AuthenticationManager authenticationManager;
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this); // Mockito 어노테이션 초기화
	}

	@Test
	@DisplayName("로그인 요청 시 JwtAuthenticationFilter을 통과해 액세스 토큰과 리프래시 토큰을 전달 받는지 테스트")
	void authenticationFilterAttemptAuthenticationSuccess() throws ServletException, IOException {
		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtProvider, authenticationManager);

		// 로그인 요청 DTO
		AuthApiRequest.Login loginDto = new AuthApiRequest.Login();
		loginDto.setEmail("test@gmail.com");
		loginDto.setPassword("1q2w3e4r!");
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonRequest = objectMapper.writeValueAsString(loginDto);

		// 요청에 DTO를 포함
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.setContent(jsonRequest.getBytes());
		request.setContentType("application/json");

		// 로그인 성공 시 토큰이 담길 응답 객체
		HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(new MockHttpServletResponse());

		Authentication authentication = getNewAuthenticationToken();

		when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
		when(jwtProvider.createAccessToken(authentication, ACCESS_TOKEN_EXPIRE_TIME)).thenReturn("mocked_access_token");
		when(jwtProvider.createRefreshToken(authentication, REFRESH_TOKEN_EXPIRE_TIME)).thenReturn("mocked_refresh_token");

		// 인증 수행
		jwtAuthenticationFilter.attemptAuthentication(request, responseWrapper);
		jwtAuthenticationFilter.successfulAuthentication(request, responseWrapper, null, authentication);

		// 인증 성공 후에 응답의 헤더에서 액세스 토큰과 리프래시 토큰 가져오기
		String authorizationHeader = responseWrapper.getHeader(AUTHORIZATION);
		String refreshHeader = responseWrapper.getHeader(REFRESH);

		// 검증
		assertEquals("Bearer mocked_access_token", authorizationHeader);
		assertEquals("Bearer mocked_refresh_token", refreshHeader);
	}

	@Test
	@DisplayName("잘못된 로그인 요청시 실패하는지 테스트")
	void authenticationFilterAttemptAuthenticationFailure() throws ServletException, IOException {
		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtProvider, authenticationManager);

		// 로그인 요청 DTO
		AuthApiRequest.Login loginDto = new AuthApiRequest.Login();
		loginDto.setEmail("test@gmail.com");
		loginDto.setPassword("1q2w3e4r!ddsfs!");
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonRequest = objectMapper.writeValueAsString(loginDto);

		// 요청에 DTO를 포함
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.setContent(jsonRequest.getBytes());
		request.setContentType("application/json");

		// 로그인 성공 시 토큰이 담길 응답 객체
		HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(new MockHttpServletResponse());

		// 로그인 실패하게 설정
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
			.thenThrow(new BadCredentialsException("인증 실패"));

		// 인증 수행
		jwtAuthenticationFilter.attemptAuthentication(request, responseWrapper);

		// 인증 실패 후에 응답의 헤더에서 액세스 토큰과 리프래시 토큰 가져오기
		String authorizationHeader = responseWrapper.getHeader(AUTHORIZATION);
		String refreshHeader = responseWrapper.getHeader(REFRESH);

		// 토큰을 받지 않고 인증에 실패한지 검증
		assertNull(authorizationHeader);
		assertNull(refreshHeader);

		// 인증 실패에 대한 응답 검증
		assertEquals(HttpServletResponse.SC_UNAUTHORIZED, responseWrapper.getStatus());
	}

	private Member createAndSaveMemberWithEncodingPassword() {
		Member member = Member.builder()
			.email("test@gmail.com")
			.password(passwordEncoder.encode("1q2w3e4r!"))
			.nickname("test")
			.authority(Authority.ROLE_USER)
			.reward(1000)
			.imageFile("imageFile")
			.build();

		memberRepository.save(member);

		return member;
	}

	private UsernamePasswordAuthenticationToken getNewAuthenticationToken() {
		Member member = createAndSaveMemberWithEncodingPassword();

		UserDetails userDetails = getUserDetails(member);

		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}
}

