package com.server.intergration;

import static com.server.auth.util.AuthConstant.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Objects;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.server.auth.controller.dto.AuthApiRequest;
import com.server.auth.util.AuthConstant;
import com.server.domain.member.entity.Member;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthIntegrationTest extends IntegrationTest {

	/*
	 jobs:
	  build-and-test:
		runs-on: ubuntu-22.04

		services:
		  redis:
			image: redis:latest
			ports:
			  - 6379:6379
	 */
	private static final String NOT_EXIST_MEMBER_EMAIL = "itprometheustest@gmail.com";
	private static final String NOT_EXIST_MEMBER_PASSWORD = "notexist1234!";
	private static final String EXIST_MEMBER_EMAIL = "test@gmail.com";
	private static final String EXIST_MEMBER_PASSWORD = "exist12345!";
	private static final String LOGIN_MEMBER_EMAIL = "login@gmail.com";
	private static final String LOGIN_MEMBER_PASSWORD = "login12345!";
	private static final String CODE = "asdfasjf912fds!";

	@BeforeAll
	void init() {
		createAndSaveMemberWithEmailPassword(EXIST_MEMBER_EMAIL, EXIST_MEMBER_PASSWORD);
		createAndSaveMemberWithEmailPassword(LOGIN_MEMBER_EMAIL, LOGIN_MEMBER_PASSWORD);
	}

	@Test
	@DisplayName("회원가입을 위한 이메일 인증번호 전송 API")
	void sendEmailForSignup() throws Exception {
		// given
		AuthApiRequest.Send request = new AuthApiRequest.Send(NOT_EXIST_MEMBER_EMAIL);

		String content = objectMapper.writeValueAsString(request);

		doNothing().when(redisService).setExpire(anyString(), anyString(), anyInt());

		// when
		ResultActions signup = mockMvc.perform(
			post("/auth/signup/email")
				.contentType(MediaType.APPLICATION_JSON)
				.content(content)
		);

		// then
		signup
			.andDo(print())
			.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("비밀번호 찾기를 위한 이메일 인증번호 전송 API")
	void sendEmailForPassword() throws Exception {
		// given
		AuthApiRequest.Send request = new AuthApiRequest.Send(EXIST_MEMBER_EMAIL);

		String content = objectMapper.writeValueAsString(request);

		doNothing().when(redisService).setExpire(anyString(), anyString(), anyInt());

		// when
		ResultActions findPassword = mockMvc.perform(
			post("/auth/password/email")
				.contentType(MediaType.APPLICATION_JSON)
				.content(content)
		);

		// then
		findPassword
			.andDo(print())
			.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("회원가입 이메일 인증 확인 API")
	void confirmEmailForSignUp() throws Exception {
		// given
		AuthApiRequest.Confirm confirmRequest = new AuthApiRequest.Confirm(
			NOT_EXIST_MEMBER_EMAIL, CODE
		);

		given(redisService.getData(anyString())).willReturn(NOT_EXIST_MEMBER_EMAIL);
		doNothing().when(redisService).setExpire(anyString(), anyString(), anyInt());

		// when
		ResultActions confirm = mockMvc.perform(
			post("/auth/signup/confirm")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(confirmRequest))
		);

		confirm
			.andDo(print())
			.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("비밀번호 찾기 이메일 인증 확인 API")
	void confirmEmailForPassword() throws Exception {
		// given
		AuthApiRequest.Confirm confirmRequest = new AuthApiRequest.Confirm(
			EXIST_MEMBER_EMAIL, CODE
		);

		given(redisService.getData(anyString())).willReturn(EXIST_MEMBER_EMAIL);
		doNothing().when(redisService).setExpire(anyString(), anyString(), anyInt());


		// when
		ResultActions confirm = mockMvc.perform(
			post("/auth/password/confirm")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(confirmRequest))
		);

		// then
		confirm
			.andDo(print())
			.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("회원가입 API")
	void signup() throws Exception {
		// given
		AuthApiRequest.SignUp signUp = new AuthApiRequest.SignUp(
			NOT_EXIST_MEMBER_EMAIL,
			NOT_EXIST_MEMBER_PASSWORD,
			"당근당근당근"
		);

		given(redisService.getData(anyString())).willReturn("true");
		doNothing().when(redisService).deleteData(anyString());


		// when
		ResultActions actions = mockMvc.perform(
			post("/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signUp))
		);

		// then
		actions
			.andDo(print())
			.andExpect(status().isCreated());

		Member member = memberRepository.findByEmail(NOT_EXIST_MEMBER_EMAIL).orElseThrow();

		assertThat(member.getNickname()).isEqualTo("당근당근당근");
		assertThat(member.getEmail()).isEqualTo(NOT_EXIST_MEMBER_EMAIL);
		assertThat(passwordEncoder.matches(NOT_EXIST_MEMBER_PASSWORD, member.getPassword()))
			.isTrue();
	}

	@Test
	@DisplayName("비밀번호 찾기 이메일 인증 확인 API")
	void updatePassword() throws Exception {
		// given
		given(redisService.getData(anyString())).willReturn("true");
		doNothing().when(redisService).deleteData(anyString());

		String newPassword = "newpw12345!";

		AuthApiRequest.Reset reset = new AuthApiRequest.Reset(
			EXIST_MEMBER_EMAIL,
			newPassword
		);

		// when
		ResultActions actions = mockMvc.perform(
			patch("/auth/password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reset))
		);

		// then
		actions
			.andDo(print())
			.andExpect(status().isNoContent());

		Member member = memberRepository.findByEmail(EXIST_MEMBER_EMAIL).orElseThrow();

		assertThat(passwordEncoder.matches(EXIST_MEMBER_PASSWORD, member.getPassword())).isFalse();
		assertThat(passwordEncoder.matches(newPassword, member.getPassword())).isTrue();
	}

	@Test
	@DisplayName("로컬 로그인 API")
	void localLogin() throws Exception {
		// given
		AuthApiRequest.Login login = new AuthApiRequest.Login(
			LOGIN_MEMBER_EMAIL,
			LOGIN_MEMBER_PASSWORD
		);

		String content = objectMapper.writeValueAsString(login);

		// when
		ResultActions actions = mockMvc.perform(
			post(LOGIN_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(content)
		);

		// then
		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(header().exists("Authorization"))
			.andExpect(header().exists("Refresh"));

		String access = actions.andReturn().getResponse().getHeader("Authorization");
		String refresh = actions.andReturn().getResponse().getHeader("Refresh");

		assertThat(access.contains(BEARER)).isTrue();
		assertThat(refresh.contains(BEARER)).isTrue();
	}

	@Test
	@DisplayName("리프래시 토큰을 사용한 액세스 토큰 재발급 API")
	void refresh() throws Exception {
		// given
		String refreshToken = BEARER + createRefreshToken(
			memberRepository.findByEmail(LOGIN_MEMBER_EMAIL).orElseThrow(),
			360000
		);

		// when
		ResultActions actions = mockMvc.perform(
			post(REFRESH_URL)
				.header(REFRESH, refreshToken)
		);

		// then
		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(header().exists("Authorization"));

		String access = actions.andReturn().getResponse().getHeader("Authorization");

		assertThat(access.contains(BEARER)).isTrue();
	}
}
