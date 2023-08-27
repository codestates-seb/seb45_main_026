package com.server.auth.oauth.service;

import static java.nio.charset.StandardCharsets.*;
import static org.springframework.http.MediaType.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.server.auth.controller.dto.AuthApiRequest;
import com.server.auth.jwt.service.CustomUserDetails;
import com.server.auth.jwt.service.JwtProvider;
import com.server.auth.util.AuthConstant;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.global.exception.businessexception.authexception.OAuthCodeRequestException;
import com.server.global.exception.businessexception.authexception.OAuthGitubRequestException;

import lombok.Getter;
import lombok.Setter;

@Service
@Transactional(readOnly = true)
public class OAuthService {
	public static final String GITHUB_EMAIL_REQUEST_URL = "https://api.github.com/user/emails";

	// OAuth2.0 클라이언트 등록 정보를 저장하는 리포지토리
	private final InMemoryClientRegistrationRepository inMemoryRepository;
	private final MemberRepository memberRepository;
	private final JwtProvider jwtProvider;
	private final DefaultOAuth2UserService defaultOAuth2UserService;
	private final RestTemplate restTemplate;

	public OAuthService(InMemoryClientRegistrationRepository inMemoryRepository,
						MemberRepository memberRepository,
						JwtProvider jwtProvider,
						DefaultOAuth2UserService defaultOAuth2UserService,
						RestTemplate restTemplate) {
		this.inMemoryRepository = inMemoryRepository;
		this.memberRepository = memberRepository;
		this.jwtProvider = jwtProvider;
		this.defaultOAuth2UserService = defaultOAuth2UserService;
		this.restTemplate = restTemplate;
	}

	@Transactional
	public AuthApiRequest.Token login(OAuthProvider provider, String code) { //어떤 인증 서비스를 사용할지와 Authorization Code

		String registrationId = provider.getDescription(); // OAuth 서비스 이름(ex. kakao, naver, google)

		ClientRegistration clientRegistration = inMemoryRepository.findByRegistrationId(registrationId); // 클라이언트 등록

		String token = getToken(code, clientRegistration); // 액세스 토큰 가져오기

		OAuth2User oAuth2User = getOAuth2User(token, clientRegistration); // 가져온 액세스 토큰으로 OAuth2User 가져오기

		Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes()); // OAuth2UserService 를 통해 가져온 OAuth2User 의 attribute를 담을 클래스
																					// 이메일 같은 정보들이 담겨져 있음

		if(registrationId.equals("github")){	// 깃허브의 경우에는 구글 카카오와 다르게 이메일을 가져오는 방식이 조금 달라서 따로 처리해야 함
			attributes.put("email", getGithubEmail(token)); // 이메일을 가져와서 attributes에 넣어줌
		}

		MemberProfile memberProfile = OAuthProvider.extract(registrationId, attributes); // registrationId에 따라 유저 정보를 통해 공통된 UserProfile 객체로 만들어 줌

		Member member = getOrSaveMember(memberProfile); // 일치하는 멤버가 있으면 그대로 가져오고 없으면 새로 만들어서 가져옴(회원가입)

		return createToken(member);
	}

	// 요청을 보낼 때 사용될 데이터들을 담는 메서드
	private MultiValueMap<String, String> tokenRequest(String code, ClientRegistration provider) {
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

		formData.add("code", code);
		formData.add("grant_type", "authorization_code");
		formData.add("redirect_uri", provider.getRedirectUri());
		formData.add("client_secret", provider.getClientSecret());
		formData.add("client_id",provider.getClientId());
		return formData;
	}

	// OAuth 서비스의 액세스 토큰을 가져옴, 구글 같은 서비스를 이용할 때 사용
	private String getToken(String code, ClientRegistration clientRegistration) {

		String uri = clientRegistration.getProviderDetails().getTokenUri(); // 서비스 제공자의 상세정보에서 엔드포인트 uri를 가져옴

		// 엔드포인트로 요청을 보내기 위한 설정들
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(APPLICATION_FORM_URLENCODED);
		headers.setAcceptCharset(List.of(UTF_8));

		HttpEntity<MultiValueMap<String, String>> entity =
			new HttpEntity<>(tokenRequest(code, clientRegistration), headers);

		try {
			ResponseEntity<Map<String, String>> responseEntity = restTemplate.exchange( // exchange로 요청을 보내고 받은 응답 결과를 저장
				uri,
				HttpMethod.POST,
				entity,
				new ParameterizedTypeReference<>() {}
			);

			return responseEntity.getBody().get("access_token"); // 응답에서 액세스 토큰을 가져옴

		} catch (HttpClientErrorException.BadRequest e) {
			throw new OAuthCodeRequestException();
		}
	}

	// 액세스 토큰을 사용하여 서비스 제공자로부터 사용자 정보를 가진 OAuth2User 객체를 가져오기
	private OAuth2User getOAuth2User(String token, ClientRegistration clientRegistration) {

		OAuth2AccessTokenResponse tokenResponse = OAuth2AccessTokenResponse.withToken(token) // 주어진 토큰으로 OAuth2AccessToken을 가진 응답 생성
			.tokenType(OAuth2AccessToken.TokenType.BEARER)
			.expiresIn(3600L)
			.build();

		OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, tokenResponse.getAccessToken()); // 응답에서 얻은 토큰으로 OAuth2UserRequest 생성

		return defaultOAuth2UserService.loadUser(userRequest); // OAuth2UserRequest로 OAuth2User 가져오기
	}

	// 깃허브는 다른 서비스와는 다르게 이메일을 가져오는 방식이 다르기 때문에 이를 처리해주는 메서드
	private String getGithubEmail(String accessToken){

		// 요청을 위한 전처리 과정
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<GithubEmail[]> response = restTemplate.exchange( // GITHUB_EMAIL_REQUEST_URL로 요청을 보내어 깃허브 이메일을 얻어옴
			GITHUB_EMAIL_REQUEST_URL,
			HttpMethod.GET,
			entity,
			GithubEmail[].class
		);

		if(response.getBody() != null && response.getBody().length > 0) {
			return response.getBody()[0].getEmail(); // 요청이 성공적이면 응답에서 이메일을 가져옴
		}

		throw new OAuthGitubRequestException();
	}

	// 기존 회원 로그인 및 신규 회원 회원가입
	private Member getOrSaveMember(MemberProfile memberProfile) {
		// 회원 조회
		Member member = getMember(memberProfile);
		// 없으면 회원가입
		if (member == null) {
			member = saveMember(memberProfile);
		}
		// 있으면 그냥 리턴(로그인)
		return member;
	}

	private Member getMember(MemberProfile memberProfile) {
		// 회원이 없는 경우에는 회원가입을 해야하기 때문에 없어도 null을 리턴함
		return memberRepository.findByEmail(memberProfile.getEmail())
			.orElse(null);
	}

	private Member saveMember(MemberProfile memberProfile) {
		// 회원가입
		Member member = Member.createMember(
			memberProfile.getEmail(),
			memberProfile.getEmail().split("@")[0],
			"oauthUser");
		return memberRepository.save(member);
	}

	// 실제로 인증 과정을 처리하고 토큰을 만드는 메서드
	private AuthApiRequest.Token createToken(Member member) {
		// 인증에 사용할 userDetails 생성
		CustomUserDetails userDetails = new CustomUserDetails(
			member.getMemberId(),
			member.getEmail(),
			member.getPassword(),
			Collections.singleton(new SimpleGrantedAuthority(member.getAuthority().toString())));

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		// 인증된 UsernamePasswordAuthenticationToken으로 액세스 및 리프래시 토큰 생성
		String accessToken = jwtProvider.createAccessToken(authentication, AuthConstant.ACCESS_TOKEN_EXPIRE_TIME);
		String refreshToken = jwtProvider.createRefreshToken(authentication, AuthConstant.ACCESS_TOKEN_EXPIRE_TIME);

		return new AuthApiRequest.Token(accessToken, refreshToken, member.getMemberId());
	}

	@Getter
	@Setter
	public static class GithubEmail {

		private String email;
		private boolean primary;
		private boolean verified;
		private String visibility;
	}
}
