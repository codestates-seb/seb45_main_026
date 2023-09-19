package com.server.auth.oauth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.HttpClientErrorException;

import com.server.auth.controller.dto.AuthApiRequest;
import com.server.domain.member.entity.Member;
import com.server.global.exception.businessexception.authexception.OAuthCodeRequestException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.testhelper.ServiceTest;

public class OAuthServiceTest extends ServiceTest {

	@Autowired
	private OAuthService oAuthService;

	@Test
	@DisplayName("OAuth 로그인 성공 (신규 회원인 경우)")
	void loginSuccessNew() {
		OAuthProvider provider = OAuthProvider.GOOGLE;
		String code = "abcde12345";

		Map<String, String> response = new HashMap<>();
		response.put("access_token", "mocked_access_token");

		ResponseEntity<Map<String, String>> responseEntity =
			new ResponseEntity<>(response, HttpStatus.OK);

		when(restTemplate.exchange(
			anyString(),
			any(HttpMethod.class),
			any(HttpEntity.class),
			any(ParameterizedTypeReference.class)
		))
			.thenReturn(responseEntity);

		OAuth2User oAuth2User = new DefaultOAuth2User(
			Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
			Collections.singletonMap("email", "test@gmail.com"),
			"email"
		);

		given(defaultOAuth2UserService.loadUser(any(OAuth2UserRequest.class)))
			.willReturn(oAuth2User);

		assertThat(memberRepository.findAll().size()).isEqualTo(0);

		AuthApiRequest.Token token = oAuthService.login(provider, code);

		assertDoesNotThrow(
			() -> memberRepository.findById(token.getMemberId())
				.orElseThrow(MemberNotFoundException::new));
		assertThat(token.getAccessToken()).isNotNull();
		assertThat(token.getRefreshToken()).isNotNull();
	}

	@Test
	@DisplayName("OAuth 로그인 성공 (기존 회원인 경우)")
	void loginSuccessExist() {
		Member member = createAndSaveMember();

		OAuthProvider provider = OAuthProvider.GOOGLE;
		String code = "abcde12345";

		Map<String, String> response = new HashMap<>();
		response.put("access_token", "mocked_access_token");

		ResponseEntity<Map<String, String>> responseEntity =
			new ResponseEntity<>(response, HttpStatus.OK);

		when(restTemplate.exchange(
			anyString(),
			any(HttpMethod.class),
			any(HttpEntity.class),
			any(ParameterizedTypeReference.class)
		))
			.thenReturn(responseEntity);

		OAuth2User oAuth2User = new DefaultOAuth2User(
			Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
			Collections.singletonMap("email", "test@gmail.com"),
			"email"
		);

		given(defaultOAuth2UserService.loadUser(any(OAuth2UserRequest.class)))
			.willReturn(oAuth2User);

		assertThat(memberRepository.findAll().size()).isEqualTo(1);

		AuthApiRequest.Token token = oAuthService.login(provider, code);

		Optional<Member> optional = memberRepository.findById(token.getMemberId());

		assertDoesNotThrow(() -> optional.orElseThrow(MemberNotFoundException::new));
		assertThat(optional.orElseThrow().getMemberId()).isEqualTo(member.getMemberId());
		assertThat(token.getAccessToken()).isNotNull();
		assertThat(token.getRefreshToken()).isNotNull();
	}

	@Test
	@DisplayName("코드가 잘못된 경우 OAuth 로그인이 실패한다")
	void loginFailure() {
		Member member = createAndSaveMember();

		OAuthProvider provider = OAuthProvider.GOOGLE;
		String code = "abcde12345";

		when(restTemplate.exchange(
			anyString(),
			any(HttpMethod.class),
			any(HttpEntity.class),
			any(ParameterizedTypeReference.class)
		))
			.thenThrow(HttpClientErrorException.BadRequest.class);

		assertThrows(
			OAuthCodeRequestException.class,
			() -> oAuthService.login(provider, code)
		);
	}
}
