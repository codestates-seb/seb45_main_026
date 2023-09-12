package com.server.auth.jwt.filter;

import static com.server.auth.util.AuthConstant.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.filter.OncePerRequestFilter;

import com.server.auth.jwt.service.JwtProvider;
import com.server.global.exception.businessexception.authexception.JwtNotValidException;
import com.server.global.testhelper.ServiceTest;

import io.jsonwebtoken.security.SecurityException;

public class RefreshFilterTest extends ServiceTest {
	@Mock
	private JwtProvider jwtProvider;

	@Test
	@DisplayName("리프래쉬 필터 요청시 유효한 토큰이면 액세스 토큰을 재발급 하는지 테스트")
	void refreshFilterSuccess() throws ServletException, IOException {
		// 필터 객체 생성
		JwtRefreshFilter jwtRefreshFilter = new JwtRefreshFilter(jwtProvider);

		// 가짜 요청
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addHeader(REFRESH, "Bearer mocked_refresh_token");
		request.setContentType("application/json");

		// 가짜 응답
		MockHttpServletResponse response = new MockHttpServletResponse();

		when(jwtProvider.refillAccessToken(Mockito.anyString(), Mockito.anyLong()))
			.thenReturn("mocked_refill_token");

		// 필터 호출
		jwtRefreshFilter.doFilterInternal(request, response, null);

		assertThat(response.getHeader(AUTHORIZATION)).isEqualTo(BEARER + "mocked_refill_token");
	}

	@Test
	@DisplayName("리프래쉬 필터 요청시 유효 하지 않은 토큰이면 예외가 발생하는지 테스트")
	void refreshFilterFailure() throws ServletException, IOException {
		// 필터 객체 생성
		JwtRefreshFilter jwtRefreshFilter = new JwtRefreshFilter(jwtProvider);

		// 가짜 요청
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.addHeader(REFRESH, "Bearer mocked_invalid_refresh_token");
		request.setContentType("application/json");

		// 가짜 응답
		MockHttpServletResponse response = new MockHttpServletResponse();

		Mockito.doThrow(new JwtNotValidException()).when(jwtProvider).validateToken(Mockito.anyString());

		// 필터 호출
		jwtRefreshFilter.doFilterInternal(request, response, null);

		assertThat(response.getStatus()).isEqualTo(401);
	}
}
