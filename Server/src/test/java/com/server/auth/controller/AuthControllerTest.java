package com.server.auth.controller;

import static com.server.auth.util.AuthConstant.*;
import static com.server.global.testhelper.ControllerTest.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.auth.controller.dto.AuthApiRequest;
import com.server.auth.jwt.service.CustomUserDetails;
import com.server.auth.jwt.service.JwtProvider;
import com.server.auth.oauth.service.OAuthProvider;
import com.server.auth.oauth.service.OAuthService;
import com.server.auth.service.AuthService;
import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.member.service.MemberService;
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
	private JwtProvider provider;
	@Autowired
	private MessageSource messageSource;
	private BeanDescriptor beanDescriptor;
	private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	private Validator validator = factory.getValidator();

	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("로컬 로그인 성공 테스트")
	void localLogin() throws Exception {
		//given
		Member member = createMember("test@email.com", "qwer1234!");

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

		setConstraintClass(AuthApiRequest.Login.class);

		//restdocs
		actions.andDo(
			document("auth/login",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("email").description("로그인 이메일").attributes(getConstraint("email")),
					fieldWithPath("password").description("로그인 비밀번호").attributes(getConstraint("password"))
				),
				responseHeaders(
					headerWithName("Authorization").description("액세스 토큰"),
					headerWithName("Refresh").description("리프래시 토큰")
				)
			)
		);
	}

	@TestFactory
	@DisplayName("로컬 로그인 Validation 테스트")
	Collection<DynamicTest> localLoginValidation() throws Exception {
		//given
		Member member = createMember("test@email.com", "qwer1234!");

		return List.of(
			DynamicTest.dynamicTest(
				"이메일을 입력하지 않은 경우",
				() -> {
					AuthApiRequest.Login login = new AuthApiRequest.Login();
					login.setPassword("qwer1234!");

					String content = objectMapper.writeValueAsString(login);

					//when
					ResultActions actions = mockMvc.perform(
						post("/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.status").value("LOGIN-400"))
						.andExpect(jsonPath("$.message").value("이메일을 입력해주세요."));
				}
			),
			DynamicTest.dynamicTest(
				"비밀번호를 입력하지 않은 경우",
				() -> {
					AuthApiRequest.Login login = new AuthApiRequest.Login();
					login.setEmail("test@email.com");

					String content = objectMapper.writeValueAsString(login);

					//when
					ResultActions actions = mockMvc.perform(
						post("/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.status").value("LOGIN-400"))
						.andExpect(jsonPath("$.message").value("패스워드를 입력해주세요."));
				}
			),
			DynamicTest.dynamicTest(
				"이메일 형식이 잘못된 경우",
				() -> {
					AuthApiRequest.Login login = new AuthApiRequest.Login();
					login.setEmail("testemail.com");
					login.setPassword("qwer1234!");

					String content = objectMapper.writeValueAsString(login);

					//when
					ResultActions actions = mockMvc.perform(
						post("/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.status").value("LOGIN-400"))
						.andExpect(jsonPath("$.message").value("이메일 형식을 맞춰주세요. (example@email.com)"));
				}
			),
			DynamicTest.dynamicTest(
				"패스워드 형식이 잘못된 경우",
				() -> {
					AuthApiRequest.Login login = new AuthApiRequest.Login();
					login.setEmail("test@email.com");
					login.setPassword("qwer12345");

					String content = objectMapper.writeValueAsString(login);

					//when
					ResultActions actions = mockMvc.perform(
						post("/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.status").value("LOGIN-400"))
						.andExpect(jsonPath("$.message").value("문자, 숫자, 특수문자로 이루어진 9~20자를 입력하세요."));
				}
			),
			DynamicTest.dynamicTest(
				"패스워드의 길이가 8글자 이하인 경우",
				() -> {
					AuthApiRequest.Login login = new AuthApiRequest.Login();
					login.setEmail("test@email.com");
					login.setPassword("qwer123!");

					String content = objectMapper.writeValueAsString(login);

					//when
					ResultActions actions = mockMvc.perform(
						post("/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.status").value("LOGIN-400"))
						.andExpect(jsonPath("$.message").value("패스워드의 길이는 최소 9자 최대 20자를 만족해야합니다."));
				}
			),
			DynamicTest.dynamicTest(
				"패스워드의 길이가 21글자 이상인 경우",
				() -> {
					AuthApiRequest.Login login = new AuthApiRequest.Login();
					login.setEmail("test@email.com");
					login.setPassword("qwerqwerqwer12341234!");

					String content = objectMapper.writeValueAsString(login);

					//when
					ResultActions actions = mockMvc.perform(
						post("/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.status").value("LOGIN-400"))
						.andExpect(jsonPath("$.message").value("패스워드의 길이는 최소 9자 최대 20자를 만족해야합니다."));
				}
			)
		);
	}

	@Test
	@DisplayName("OAuth2 로그인 성공 테스트")
	void googleLogin() throws Exception {
		//given
		String code = "ABCDEFG1234567";
		Long memberId = 1L;

		AuthApiRequest.OAuth oAuth = new AuthApiRequest.OAuth(OAuthProvider.GOOGLE, code);
		String content = objectMapper.writeValueAsString(oAuth);

		AuthApiRequest.Token token = new AuthApiRequest.Token("Bearer aaa.bbb.ccc", "Bearer ddd.eee.fff", memberId);

		given(oAuthService.login(any(OAuthProvider.class), anyString())).willReturn(token);

		//when
		ResultActions actions = mockMvc.perform(
			post("/auth/oauth")
				.contentType(MediaType.APPLICATION_JSON)
				.content(content)
		);

		//then
		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(header().exists("Authorization"))
			.andExpect(header().exists("Refresh"));

		setConstraintClass(AuthApiRequest.OAuth.class);

		actions
			.andDo(
				document(
					"auth/oauth",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestFields(
						fieldWithPath("provider").description(generateLinkCode(OAuthProvider.class)).attributes(getConstraint("provider")),
						fieldWithPath("code").description("OAuth 인증 코드").attributes(getConstraint("code"))
					),
					responseHeaders(
						headerWithName("Authorization").description("accessToken"),
						headerWithName("Refresh").description("refreshToken")
					)
				)
			);
	}

	@TestFactory
	@DisplayName("OAuth2 로그인 Validation 테스트")
	Collection<DynamicTest> googleLoginValidation() throws Exception {
		//given
		AuthApiRequest.Token token = new AuthApiRequest.Token("Bearer aaa.bbb.ccc", "Bearer ddd.eee.fff", 1L);

		given(oAuthService.login(any(OAuthProvider.class), anyString())).willReturn(token);

		return List.of(
			DynamicTest.dynamicTest(
				"코드가 Null인 경우",
				() -> {
					String code = null;

					AuthApiRequest.OAuth oAuth = new AuthApiRequest.OAuth(OAuthProvider.GOOGLE, code);
					String content = objectMapper.writeValueAsString(oAuth);

					ResultActions actions = mockMvc.perform(
						post("/auth/oauth")
							.contentType(MediaType.APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("code"))
						.andExpect(jsonPath("$.data[0].value").value("null"))
						.andExpect(jsonPath("$.data[0].reason").value("코드를 다시 확인해주세요."));
				}
			),
			DynamicTest.dynamicTest(
				"Provider가 Null인 경우",
				() -> {
					String code = "ABCDEFGHI1234567";

					AuthApiRequest.OAuth oAuth = new AuthApiRequest.OAuth(null, code);
					String content = objectMapper.writeValueAsString(oAuth);

					ResultActions actions = mockMvc.perform(
						post("/auth/oauth")
							.contentType(MediaType.APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("provider"))
						.andExpect(jsonPath("$.data[0].value").value("null"))
						.andExpect(jsonPath("$.data[0].reason").value("GOOGLE, KAKAO, GITHUB 로그인만 지원합니다."));
				}
			)
		);
	}

	@Test
	@DisplayName("리프래쉬 토큰으로 액세스 토큰 재발급 테스트")
	void refreshTokenToAccessToken() throws Exception {
		Member member = createMember("refresh@email.com", "qwer1234!");

		String refresh = getRefreshToken(member);

		ResultActions actions = mockMvc.perform(
			post("/auth/refresh")
				.header(REFRESH, BEARER + refresh)
				.contentType(MediaType.APPLICATION_JSON)
		);

		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(header().exists("Authorization"));

		actions
			.andDo(
				document(
					"auth/refresh",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestHeaders(
						headerWithName("Refresh").description("refreshToken")
					),
					responseHeaders(
						headerWithName("Authorization").description("accessToken")
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

		setConstraintClass(AuthApiRequest.Send.class);

		signup
			.andDo(
				document(
					"auth/signup/email",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestFields(
						fieldWithPath("email").description("이메일").attributes(getConstraint("email"))
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
					"auth/password/email",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestFields(
						fieldWithPath("email").description("이메일").attributes(getConstraint("email"))
					)
				)
			);
	}

	@TestFactory
	@DisplayName("이메일 전송 성공 테스트")
	Collection<DynamicTest> sendEmailValidation() throws Exception {

		return List.of(
			DynamicTest.dynamicTest(
				"이메일이 null인 경우",
				() -> {
					//given
					AuthApiRequest.Send send = new AuthApiRequest.Send(null);
					String content = objectMapper.writeValueAsString(send);

					//when
					ResultActions signup = mockMvc.perform(
						post("/auth/signup/email")
							.contentType(MediaType.APPLICATION_JSON)
							.content(content)
					);

					signup
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("email"))
						.andExpect(jsonPath("$.data[0].value").value("null"))
						.andExpect(jsonPath("$.data[0].reason").value("입력값을 다시 확인해주세요."));
				}
			),
			DynamicTest.dynamicTest(
				"이메일 형식이 잘못된 경우",
				() -> {
					//given
					AuthApiRequest.Send send = new AuthApiRequest.Send("testemail.com");
					String content = objectMapper.writeValueAsString(send);

					//when
					ResultActions signup = mockMvc.perform(
						post("/auth/signup/email")
							.contentType(MediaType.APPLICATION_JSON)
							.content(content)
					);

					signup
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("email"))
						.andExpect(jsonPath("$.data[0].value").value("testemail.com"))
						.andExpect(jsonPath("$.data[0].reason").value("이메일 형식을 맞춰주세요. (example@email.com)"));
				}
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

		setConstraintClass(AuthApiRequest.Confirm.class);

		signup
			.andDo(print())
			.andExpect(status().isNoContent())
			.andDo(
				document(
				"auth/signup/confirm",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestFields(
						fieldWithPath("email").description("이메일").attributes(getConstraint("email")),
						fieldWithPath("code").description("이메일로 받은 인증번호").attributes(getConstraint("code"))
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
					"auth/password/confirm",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestFields(
						fieldWithPath("email").description("이메일").attributes(getConstraint("email")),
						fieldWithPath("code").description("이메일로 받은 인증번호").attributes(getConstraint("code"))
					)
				)
			);
	}

	@TestFactory
	@DisplayName("이메일 인증 Validation 테스트")
	Collection<DynamicTest> confirmEmailValidation() throws Exception {

		return List.of(
			DynamicTest.dynamicTest(
				"이메일 형식이 잘못된 경우",
				() -> {
					//given
					AuthApiRequest.Confirm confirm = new AuthApiRequest.Confirm(
						"confirmemail.com",
						"code1234"
					);

					String content = objectMapper.writeValueAsString(confirm);

					//when
					ResultActions signup = mockMvc.perform(
						post("/auth/signup/confirm")
							.contentType(MediaType.APPLICATION_JSON)
							.content(content)
					);

					signup
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("email"))
						.andExpect(jsonPath("$.data[0].value").value("confirmemail.com"))
						.andExpect(jsonPath("$.data[0].reason").value("이메일 형식을 맞춰주세요. (example@email.com)"));
				}
			),
			DynamicTest.dynamicTest(
				"이메일 형식이 잘못된 경우",
				() -> {
					//given
					AuthApiRequest.Confirm confirm = new AuthApiRequest.Confirm(
						"confirmemail.com",
						"code1234"
					);

					String content = objectMapper.writeValueAsString(confirm);

					//when
					ResultActions signup = mockMvc.perform(
						post("/auth/signup/confirm")
							.contentType(MediaType.APPLICATION_JSON)
							.content(content)
					);

					signup
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("email"))
						.andExpect(jsonPath("$.data[0].value").value("confirmemail.com"))
						.andExpect(jsonPath("$.data[0].reason").value("이메일 형식을 맞춰주세요. (example@email.com)"));
				}
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

		setConstraintClass(AuthApiRequest.SignUp.class);

		actions
			.andDo(
				document(
					"auth/signup",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestFields(
						fieldWithPath("email").description("이메일").attributes(getConstraint("email")),
						fieldWithPath("password").description("패스워드").attributes(getConstraint("password")),
						fieldWithPath("nickname").description("닉네임(기본 채널명)").attributes(getConstraint("nickname"))
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

		setConstraintClass(AuthApiRequest.Reset.class);

		actions
			.andDo(
				document(
					"auth/password",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestFields(
						fieldWithPath("email").description("이메일").attributes(getConstraint("email")),
						fieldWithPath("password").description("변경할 패스워드").attributes(getConstraint("password"))
					)
				)
			);
	}

	private Member createMember(String email, String password) {
		Member member = Member.builder()
			.email(email)
			.nickname("test")
			.password(passwordEncoder.encode(password))
			.authority(Authority.ROLE_USER)
			.build();

		return memberRepository.save(member);
	}

	private void setConstraintClass(Class<?> clazz){
		this.beanDescriptor = validator.getConstraintsForClass(clazz);
	}

	private Attributes.Attribute getConstraint(String value){
		assert(beanDescriptor != null) : "constraint 설정이 되어있지 않습니다. setConstraintClass() 를 통해 설정해주세요 ";

		PropertyDescriptor propertyDescriptor = beanDescriptor.getConstraintsForProperty(value);

		StringBuilder sb = new StringBuilder();

		if(propertyDescriptor == null){
			return new Attributes.Attribute("constraints", sb.toString());
		}

		Set<ConstraintDescriptor<?>> constraintDescriptors = propertyDescriptor.getConstraintDescriptors();

		for (ConstraintDescriptor<?> constraintDescriptor : constraintDescriptors) {

			String type = constraintDescriptor.getAnnotation().annotationType().getSimpleName();

			String message = (String) constraintDescriptor.getAttributes().get("message");
			Integer min = (Integer) constraintDescriptor.getAttributes().get("min");
			Integer max = (Integer) constraintDescriptor.getAttributes().get("max");

			String actualMessage = getActualMessage(message, min, max);

			sb.append(" [");
			sb.append(type);
			sb.append(" : ");
			sb.append(actualMessage);
			sb.append("] ");
		}

		return new Attributes.Attribute("constraints", sb.toString());
	}

	private String getActualMessage(String messageKey, Integer min, Integer max) {
		String actualMessageKey = messageKey.replace("{", "").replace("}", "");

		String message = messageSource.getMessage(actualMessageKey, null, Locale.getDefault());

		if(min == null || max == null){
			return message;
		}

		return message.replace("{min}", min.toString()).replace("{max}", max.toString());
	}

	private String getRefreshToken(Member member) {
		UserDetails userDetails = getUserDetails(member);

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
			userDetails, null, userDetails.getAuthorities());

		return provider.createRefreshToken(authenticationToken, 1000000);
	}

	private UserDetails getUserDetails(Member member) {
		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getAuthority().toString());

		return new CustomUserDetails(
			member.getMemberId(),
			String.valueOf(member.getEmail()),
			member.getPassword(),
			Collections.singleton(grantedAuthority)
		);
	}
}
