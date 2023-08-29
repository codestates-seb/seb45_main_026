package com.server.auth;

import static com.server.global.testhelper.ControllerTest.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.auth.controller.dto.AuthApiRequest;
import com.server.auth.jwt.service.JwtProvider;
import com.server.auth.oauth.service.OAuthProvider;
import com.server.auth.oauth.service.OAuthService;
import com.server.auth.service.AuthService;
import com.server.auth.service.dto.AuthServiceRequest;
import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.member.service.MemberService;
import com.server.global.testhelper.ControllerTest;
import com.server.module.email.service.MailService;

// @Import(AopConfiguration.class)
// @EnableAspectJAutoProxy
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Transactional
@SpringBootTest // 시큐리티를 사용하기 위해서 통합 테스트 사용해야 함 (다른 방법 나중에 찾아보기)
@ExtendWith({RestDocumentationExtension.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 테스트 인스턴스 생명주기 클래스에 맞추기
@ActiveProfiles("local")
public class AuthControllerTest {

	@MockBean
	private AuthService authService;
	@MockBean
	private OAuthService oAuthService;
	@MockBean
	private MailService mailService;
	@MockBean
	private MemberService memberService;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MemberRepository memberRepository;

	// @BeforeAll
	// void addMember() {
	// 	Member member = Member.createMember(
	// 		"test@email.com",
	// 		passwordEncoder.encode("qwer1234!"),
	// 		"testname"
	// 	);
	//
	// 	memberRepository.save(member);
	// }

	@Test
	@DisplayName("로컬 로그인 성공 테스트")
	void localLogin() throws Exception {
		//given
		AuthApiRequest.Login login = new AuthApiRequest.Login("test@email.com", "qwer1234!");

		String content = objectMapper.writeValueAsString(login);

		//when
		ResultActions actions = mockMvc.perform(
			post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(content)
		);

		//then
		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(header().exists("Authorization"))
			.andExpect(header().exists("Refresh"));

		//restdocs
		actions.andDo(
			document("auth/login",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("email").description("로그인 이메일"),
					fieldWithPath("password").description("로그인 비밀번호")
				),
				responseHeaders(
					headerWithName("Authorization").description("액세스 토큰"),
					headerWithName("Refresh").description("리프래시 토큰")
				)
			)
		);
	}

	@Test
	@DisplayName("OAuth2 로그인 성공 테스트")
	void googleLogin() throws Exception {
		//given
		String provider = "GOOGLE";
		String code = "ABCDEFG1234567";
		Long memberId = 1L;

		AuthApiRequest.Token token = new AuthApiRequest.Token("Bearer aaa.bbb.ccc", "Bearer ddd.eee.fff", memberId);

		given(oAuthService.login(any(OAuthProvider.class), anyString())).willReturn(token);

		//when
		ResultActions actions = mockMvc.perform(
			get("/auth/oauth")
				.param("provider", provider)
				.param("code", code)
		);

		//then
		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(header().exists("Authorization"))
			.andExpect(header().exists("Refresh"));

		actions
			.andDo(
				document(
					"/auth/oauth",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestParameters(
						parameterWithName("provider").description(generateLinkCode(OAuthProvider.class)),
						parameterWithName("code").description("OAuth 인증 코드")
					),
					responseHeaders(
						headerWithName("Authorization").description("accessToken"),
						headerWithName("Refresh").description("refreshToken")
					)
				)
			);
	}

	@Test
	@DisplayName("이메일 전송 성공 테스트")
	void sendEmail() throws Exception {
		//given
		AuthApiRequest.Send send = new AuthApiRequest.Send("abcd@email.com");
		String content = objectMapper.writeValueAsString(send);

		//when
		ResultActions signup = mockMvc.perform(
			post("/auth/signup/email")
				.contentType(MediaType.APPLICATION_JSON)
				.content(content)
		);

		//then
		signup
			.andDo(print())
			.andExpect(status().isNoContent());

		signup
			.andDo(
				document(
					"/auth/signup/email",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestFields(
						fieldWithPath("email").description("이메일")
					)
				)
			);

		ResultActions password = mockMvc.perform(
			post("/auth/password/email")
				.contentType(MediaType.APPLICATION_JSON)
				.content(content)
		);

		password
			.andDo(print())
			.andExpect(status().isNoContent());

		password
			.andDo(
				document(
					"/auth/password/email",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestFields(
						fieldWithPath("email").description("이메일")
					)
				)
			);
	}

	@Test
	@DisplayName("이메일 인증 성공 테스트")
	void confirmEmail() throws Exception {
		//given
		AuthApiRequest.Confirm confirm = new AuthApiRequest.Confirm(
			"confirm@email.com",
			"code1234"
		);

		String content = objectMapper.writeValueAsString(confirm);

		//wheb
		ResultActions signup = mockMvc.perform(
			post("/auth/signup/confirm")
				.contentType(MediaType.APPLICATION_JSON)
				.content(content)
		);

		signup
			.andDo(print())
			.andExpect(status().isNoContent())
			.andDo(
				document(
				"/auth/signup/confirm",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestFields(
						fieldWithPath("email").description("이메일"),
						fieldWithPath("code").description("이메일로 받은 인증번호")
					)
				)
			);

		ResultActions password = mockMvc.perform(
			post("/auth/password/confirm")
				.contentType(MediaType.APPLICATION_JSON)
				.content(content)
		);

		password
			.andDo(print())
			.andExpect(status().isNoContent())
			.andDo(
				document(
					"/auth/password/confirm",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestFields(
						fieldWithPath("email").description("이메일"),
						fieldWithPath("code").description("이메일로 받은 인증번호")
					)
				)
			);
	}

	@Test
	@DisplayName("회원가입 성공")
	void signup() throws Exception {
		//given
		AuthApiRequest.SignUp signUp = new AuthApiRequest.SignUp(
			"coding@joa.com",
			"asdf1234!",
			"당근"
		);

		String content = objectMapper.writeValueAsString(signUp);

		//when
		ResultActions actions = mockMvc.perform(
			post("/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(content)
		);

		actions
			.andDo(print())
			.andExpect(status().isCreated());

		actions
			.andDo(
				document(
					"/auth/signup",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestFields(
						fieldWithPath("email").description("이메일"),
						fieldWithPath("password").description("패스워드"),
						fieldWithPath("nickname").description("닉네임(기본 채널명)")
					)
				)
			);
	}

	@Test
	@DisplayName("패스워드 초기화")
	void updatePassword() throws Exception {
		//given
		AuthApiRequest.Reset reset = new AuthApiRequest.Reset(
			"coding@joa.com",
			"asdf1234!"
		);

		String content = objectMapper.writeValueAsString(reset);

		//when
		ResultActions actions = mockMvc.perform(
			patch("/auth/password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(content)
		);

		//then
		actions
			.andDo(print())
			.andExpect(status().isNoContent());

		actions
			.andDo(
				document(
					"/auth/password",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestFields(
						fieldWithPath("email").description("이메일"),
						fieldWithPath("password").description("변경할 패스워드")
					)
				)
			);
	}

	private Member createMember(String email, String password) {
		return Member.builder()
			.email(email)
			.nickname("test")
			.password(password)
			.authority(Authority.ROLE_USER)
			.build();
	}
}
