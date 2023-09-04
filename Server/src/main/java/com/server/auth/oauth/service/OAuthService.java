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
import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.service.ChannelService;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.member.service.MemberService;
import com.server.global.exception.businessexception.authexception.OAuthCodeRequestException;
import com.server.global.exception.businessexception.authexception.OAuthGitubRequestException;

import lombok.Getter;
import lombok.Setter;

@Service
@Transactional(readOnly = true)
public class OAuthService {
	public static final String GITHUB_EMAIL_REQUEST_URL = "https://api.github.com/user/emails";

	private final InMemoryClientRegistrationRepository inMemoryRepository;
	private final MemberRepository memberRepository;
	private final ChannelService channelService;
	private final JwtProvider jwtProvider;
	private final DefaultOAuth2UserService defaultOAuth2UserService;
	private final RestTemplate restTemplate;

	public OAuthService(InMemoryClientRegistrationRepository inMemoryRepository, MemberRepository memberRepository,
		ChannelService channelService, JwtProvider jwtProvider, DefaultOAuth2UserService defaultOAuth2UserService,
		RestTemplate restTemplate) {
		this.inMemoryRepository = inMemoryRepository;
		this.memberRepository = memberRepository;
		this.channelService = channelService;
		this.jwtProvider = jwtProvider;
		this.defaultOAuth2UserService = defaultOAuth2UserService;
		this.restTemplate = restTemplate;
	}

	@Transactional
	public AuthApiRequest.Token login(OAuthProvider provider, String code) {

		String registrationId = provider.getDescription();

		ClientRegistration clientRegistration = inMemoryRepository.findByRegistrationId(registrationId);

		String token = getToken(code, clientRegistration);

		OAuth2User oAuth2User = getOAuth2User(token, clientRegistration);

		Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());

		if(registrationId.equals("github")){
			attributes.put("email", getGithubEmail(token));
		}

		MemberProfile memberProfile = OAuthProvider.extract(registrationId, attributes);

		Member member = getOrSaveMember(memberProfile);

		return createToken(member);
	}

	private MultiValueMap<String, String> tokenRequest(String code, ClientRegistration provider) {
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

		formData.add("code", code);
		formData.add("grant_type", "authorization_code");
		formData.add("redirect_uri", provider.getRedirectUri());
		formData.add("client_secret", provider.getClientSecret());
		formData.add("client_id",provider.getClientId());
		return formData;
	}

	private String getToken(String code, ClientRegistration clientRegistration) {

		String uri = clientRegistration.getProviderDetails().getTokenUri();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(APPLICATION_FORM_URLENCODED);
		headers.setAcceptCharset(List.of(UTF_8));

		HttpEntity<MultiValueMap<String, String>> entity =
			new HttpEntity<>(tokenRequest(code, clientRegistration), headers);

		try {
			ResponseEntity<Map<String, String>> responseEntity = restTemplate.exchange(
				uri,
				HttpMethod.POST,
				entity,
				new ParameterizedTypeReference<>() {}
			);

			return responseEntity.getBody().get("access_token");

		} catch (HttpClientErrorException.BadRequest e) {
			throw new OAuthCodeRequestException();
		}
	}

	private OAuth2User getOAuth2User(String token, ClientRegistration clientRegistration) {

		OAuth2AccessTokenResponse tokenResponse = OAuth2AccessTokenResponse.withToken(token)
			.tokenType(OAuth2AccessToken.TokenType.BEARER)
			.expiresIn(3600L)
			.build();

		OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, tokenResponse.getAccessToken());

		return defaultOAuth2UserService.loadUser(userRequest);
	}

	private String getGithubEmail(String accessToken){

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<GithubEmail[]> response = restTemplate.exchange(
			GITHUB_EMAIL_REQUEST_URL,
			HttpMethod.GET,
			entity,
			GithubEmail[].class
		);

		if(response.getBody() != null && response.getBody().length > 0) {
			return response.getBody()[0].getEmail();
		}

		throw new OAuthGitubRequestException();
	}

	private Member getOrSaveMember(MemberProfile memberProfile) {
		Member member = getMember(memberProfile);
		if (member == null) {
			member = saveMember(memberProfile);
		}
		return member;
	}

	private Member getMember(MemberProfile memberProfile) {
		return memberRepository.findByEmail(memberProfile.getEmail())
			.orElse(null);
	}

	private Member saveMember(MemberProfile memberProfile) {
		Member member = Member.createMember(
			memberProfile.getEmail(),
			memberProfile.getEmail().split("@")[0],
			"oauthUser");

		Member signMember = memberRepository.save(member);
		channelService.createChannel(signMember);
		return signMember;
	}

	private AuthApiRequest.Token createToken(Member member) {
		CustomUserDetails userDetails = new CustomUserDetails(
			member.getMemberId(),
			member.getEmail(),
			member.getPassword(),
			Collections.singleton(new SimpleGrantedAuthority(member.getAuthority().toString())));

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

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
