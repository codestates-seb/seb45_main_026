package com.server.auth.jwt.service;

import static org.junit.jupiter.api.Assertions.*;

import java.security.Key;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.server.domain.member.entity.Member;
import com.server.global.exception.businessexception.authexception.JwtExpiredException;
import com.server.global.exception.businessexception.authexception.JwtNotValidException;
import com.server.global.testhelper.ServiceTest;

import io.jsonwebtoken.Claims;

public class JwtServiceTest extends ServiceTest {
	@Autowired
	private JwtProvider jwtProvider;
	@Autowired
	private JpaUserDetailsService jpaUserDetailsService;

	@Test
	@DisplayName("시크릿키가 정상적으로 생성되는지 테스트 한다.")
	void secretKeyInitialization() {
		Key secretKey = jwtProvider.getSecretKey();
		assertNotNull(secretKey);
	}

	@Test
	@DisplayName("액세스 토큰이 정상적으로 생성되는지 테스트 한다.")
	void createAccessToken() {
		UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken();

		String accessToken =
			jwtProvider.createAccessToken(authenticationToken, 3600L);

		assertNotNull(accessToken);
	}

	@Test
	@DisplayName("리프래시 토큰이 정상적으로 생성되는지 테스트 한다.")
	void createRefreshToken() {
		UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken();

		String refreshToken =
			jwtProvider.createRefreshToken(authenticationToken, 3600);

		assertNotNull(refreshToken);
	}

	@Test
	@DisplayName("리프래시 토큰으로 액세스 토큰이 정상적으로 생성되는지 테스트 한다.")
	void refillAccessToken() {
		UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken();

		String refreshToken =
			jwtProvider.createRefreshToken(authenticationToken, 3600);

		String accessToken =
			jwtProvider.refillAccessToken(refreshToken, 3600);

		assertNotNull(accessToken);
	}

	@TestFactory
	@DisplayName("Claims 조회 및 검증이 정상적으로 수행되는지 테스트 한다.")
	Collection<DynamicTest> getAndValidateClaims() throws InterruptedException {

		UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken();

		String validAccessToken =
			jwtProvider.createAccessToken(authenticationToken, 3600L);

		String invalidAccessToken = "easjldksj8ou32984rjsjekdn8923uaklsdjjfdsfj29083isdjfhksdj329nsfdsd";

		String expiredAccessToken =
			jwtProvider.createAccessToken(authenticationToken, 1L);
		Thread.sleep(1000);

		return List.of(
			DynamicTest.dynamicTest(
				"유효한 토큰인 경우에 Claims가 예외 없이 조회 된다.",
				() -> {
					Claims claims = jwtProvider.getClaims(validAccessToken);

					assertNotNull(claims);
				}
			),
			DynamicTest.dynamicTest(
				"유효한 토큰인 경우에 Claims 검증이 예외 없이 통과 된다",
				() -> {
					assertDoesNotThrow(() -> jwtProvider.validateToken(validAccessToken));
				}
			),
			DynamicTest.dynamicTest(
				"유효 하지 않은 토큰인 경우에 Claims 조회시 예외가 발생 한다",
				() -> {
					assertThrows(JwtNotValidException.class, () -> jwtProvider.getClaims(invalidAccessToken));
				}
			),
			DynamicTest.dynamicTest(
				"유효 하지 않은 토큰인 경우에 Claims 검증시 예외가 발생 한다",
				() -> {
					assertThrows(JwtNotValidException.class, () -> jwtProvider.validateToken(invalidAccessToken));
				}
			),
			DynamicTest.dynamicTest(
				"만료된 토큰인 경우에 Claims 조회시 예외가 발생 한다",
				() -> {
					assertThrows(JwtExpiredException.class, () -> jwtProvider.getClaims(expiredAccessToken));
				}
			),
			DynamicTest.dynamicTest(
				"만료된 토큰인 경우에 Claims 검증시 예외가 발생 한다",
				() -> {
					assertThrows(JwtExpiredException.class, () -> jwtProvider.validateToken(expiredAccessToken));
				}
			)
		);
	}

	@TestFactory
	@DisplayName("UserDetails 조회 테스트")
	Collection<DynamicTest> loadUserByUsername() {
		Member member = createAndSaveMember();

		String invalidEmail = "invalid@email.com";

		return List.of(
				DynamicTest.dynamicTest(
					"존재 하는 회원인 경우",
					() -> {
						UserDetails userDetails = jpaUserDetailsService.loadUserByUsername(member.getEmail());

						assertNotNull(userDetails);
					}
				),
			DynamicTest.dynamicTest(
				"존재 하지 않는 회원인 경우",
				() -> {
					assertThrows(UsernameNotFoundException.class, () -> jpaUserDetailsService.loadUserByUsername(invalidEmail));

				}
			)
		);
	}
}
