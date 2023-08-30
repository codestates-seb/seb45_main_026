package com.server.global.testhelper;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import com.server.domain.announcement.controller.AnnouncementController;
import com.server.domain.announcement.service.AnnouncementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.auth.controller.AuthController;
import com.server.auth.jwt.service.CustomUserDetails;
import com.server.auth.oauth.service.OAuthService;
import com.server.auth.service.AuthService;
import com.server.domain.channel.controller.ChannelController;
import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.service.ChannelService;
import com.server.domain.member.controller.MemberController;
import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Member;
import com.server.domain.member.service.MemberService;
import com.server.domain.order.controller.OrderController;
import com.server.domain.order.service.OrderService;
import com.server.domain.question.controller.QuestionController;
import com.server.domain.question.service.QuestionService;
import com.server.domain.reply.controller.ReplyController;
import com.server.domain.reply.service.ReplyService;
import com.server.domain.video.controller.VideoController;
import com.server.domain.video.service.VideoService;
import com.server.global.common.CommonController;
import com.server.module.email.service.MailService;
import com.server.module.s3.service.AwsService;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest({
	AuthController.class,
	ChannelController.class,
	CommonController.class,
	MemberController.class,
	QuestionController.class,
	OrderController.class,
	ReplyController.class,
	VideoController.class,
	AnnouncementController.class
})
@ExtendWith({RestDocumentationExtension.class})
@ActiveProfiles("local")
public class ControllerTest {
	// 서비스
	@MockBean
	protected ChannelService channelService;
	@MockBean
	protected AwsService awsService;
	@MockBean
	protected AuthService authService;
	@MockBean
	protected OAuthService oAuthService;
	@MockBean
	protected MailService mailService;
	@MockBean
	protected MemberService memberService;
	@MockBean
	protected OrderService orderService;
	@MockBean
	protected QuestionService questionService;
	@MockBean
	protected ReplyService replyService;
	@MockBean
	protected VideoService videoService;
	@MockBean
	protected AnnouncementService announcementService;

	// 컨트롤러 테스트에 필요한 것들
	@Autowired
	protected MockMvc mockMvc;
	@Autowired protected ObjectMapper objectMapper; // json으로 변환
	@Autowired private MessageSource messageSource; //

	// 문서 생성 및 제어 기능을 제공하는 클래스
	protected RestDocumentationResultHandler documentHandler;
	// BeanDescriptor에 할당하기 위한 Validator 객체를 생성
	private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	private Validator validator = factory.getValidator(); // 제약 조건 정보를 가진 객체
	// 제약 조건을 포함하고 있는 객체
	private BeanDescriptor beanDescriptor;

	protected final String AUTHORIZATION = "Authorization";
	protected final String TOKEN = "Bearer GBJFIU4BFDASUK41KVR1.FDSKAVF123KVA167FVK.F12B3Y4AVFD6I7";

	// 공통 전처리
	@BeforeEach
	void setUp(WebApplicationContext context,
		final RestDocumentationContextProvider restDocumentation,
		TestInfo testInfo) {


		// 현재 테스트 클래스명을 가져와서 ControllerTest 부분을 지우고 실제 클래스명을 얻음
		String className = testInfo.getTestClass().orElseThrow().getSimpleName()
			.replace("ControllerTest", "").toLowerCase();
		String methodName = testInfo.getTestMethod().orElseThrow().getName().toLowerCase();

		documentHandler = document(
			// 클래스명으로 디렉터리를 생성하고 메서드명.adoc 파일 생성
			className + "/" + methodName,
			// 문서 포맷을 이쁘게 설정
			preprocessRequest(prettyPrint()),
			preprocessResponse(prettyPrint())
		);

		DefaultMockMvcBuilder mockMvcBuilder = webAppContextSetup(context) // mockMvc 초기화
			.apply(documentationConfiguration(restDocumentation)) // 문서화 설정 적용
			.addFilters(new CharacterEncodingFilter("UTF-8", true)); // 인코딩


		//validation 은 문서화하지 않음
		if(!methodName.contains("validation")){
			mockMvcBuilder.alwaysDo(documentHandler);
		}

		mockMvc = mockMvcBuilder.build();
	}

	// beanDescriptor에 제약 조건을 가진 validator 객체를 할당
	// 이 메서드를 사용해야 특정 클래스의 제약 조건을 가져올 수 있음 (중요!)
	protected void setConstraintClass(Class<?> clazz){
		this.beanDescriptor = validator.getConstraintsForClass(clazz);
	}

	// 제약 조건을 가져오는 메서드
	protected Attributes.Attribute getConstraint(String value){
		// 제약 조건을 가지고 있는 beanDescriptor를 사용
		assert(beanDescriptor != null) : "constraint 설정이 되어있지 않습니다. setConstraintClass() 를 통해 설정해주세요 ";

		// 주어진 속성(value)의 제약 조건 검색
		PropertyDescriptor propertyDescriptor = beanDescriptor.getConstraintsForProperty(value);

		StringBuilder sb = new StringBuilder();

		if(propertyDescriptor == null){
			return new Attributes.Attribute("constraints", sb.toString());
		}

		// PropertyDescriptor에 저장한 제약 조건들을 Set<ConstraintDescriptor<?>>에 저장
		Set<ConstraintDescriptor<?>> constraintDescriptors = propertyDescriptor.getConstraintDescriptors();

		// Set을 순회하며 제약 조건의 정보를 추출
		for (ConstraintDescriptor<?> constraintDescriptor : constraintDescriptors) {

			// 현재 속성의 NotNull 같은 어노테이션의 타입을 가져옴
			String type = constraintDescriptor.getAnnotation().annotationType().getSimpleName();

			// 현재 속성의 message, min, max 속성을 가져옴
			String message = (String) constraintDescriptor.getAttributes().get("message");
			Integer min = (Integer) constraintDescriptor.getAttributes().get("min");
			Integer max = (Integer) constraintDescriptor.getAttributes().get("max");

			// 실제 메시지를 가져옴
			String actualMessage = getActualMessage(message, min, max);

			sb.append(" [");
			sb.append(type);
			sb.append(" : ");
			sb.append(actualMessage);
			sb.append("] ");
		}

		return new Attributes.Attribute("constraints", sb.toString());
	}

	// 제약 조건의 실제 메시지를 가져오는 메서드
	protected String getActualMessage(String messageKey, Integer min, Integer max) {
		// messageSource를 사용해 실제 메시지를 가져오기 전에 중괄호 제거
		String actualMessageKey = messageKey.replace("{", "").replace("}", "");

		// 현재 언어에 맞는 실제 메시지를 가져옴
		String message = messageSource.getMessage(actualMessageKey, null, Locale.getDefault());

		if(min == null || max == null){
			return message;
		}

		return message.replace("{min}", min.toString()).replace("{max}", max.toString());
	}

	// 문서에서 값 보기 버튼을 클릭 했을 시 어떻게 열지 링크를 설정하는 메서드
	// 예) link:../common/member.html[Member 값 보기,role="popup"]
	public static String generateLinkCode(Class<?> clazz) {
		return String.format("link:../common/%s.html[%s 값 보기,role=\"popup\"]",
			clazz.getSimpleName().toLowerCase(), clazz.getSimpleName());
	}

	protected Member createMember() {
		return Member.builder()
			.email("test@google.com")
			.nickname("test")
			.password("1234abcd!")
			.authority(Authority.ROLE_USER)
			.build();
	}

	protected Member createMember(Long memberId) {
		return Member.builder()
			.memberId(memberId)
			.email("test@google.com")
			.nickname("test")
			.password("1234abcd!")
			.authority(Authority.ROLE_USER)
			.build();
	}

	protected Channel createChannel(Member member) {
		return Channel.builder()
			.channelId(1L)
			.channelName("test channel")
			.member(member)
			.build();
	}

	// 테스트에 사용할 인증정보 생성
	// 유저 디테일 생성 -> authenticationToken 생성 후 컨텍스트에 저장
	private UserDetails createUserDetails(Long id, Member notSavedmember) {
		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(notSavedmember.getAuthority().toString());

		return new CustomUserDetails(
			id,
			String.valueOf(notSavedmember.getEmail()),
			notSavedmember.getPassword(),
			Collections.singleton(grantedAuthority)
		);
	}

	protected void setDefaultAuthentication(Long id){
		UserDetails userDetails = createUserDetails(id, createMember());

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
			userDetails, null, userDetails.getAuthorities());

		SecurityContextImpl securityContext = new SecurityContextImpl(authenticationToken);
		SecurityContextHolder.setContext(securityContext);
	}
}
