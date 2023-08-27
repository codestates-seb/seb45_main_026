package com.server.auth.oauth.service;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import com.server.global.entity.BaseEnum;

public enum OAuthProvider implements BaseEnum {

	GOOGLE("google", (attributes) ->
		MemberProfile.builder()
			.email((String) attributes.get("email"))
			.build()),

	GITHUB("github", (attributes) ->
		MemberProfile.builder()
			.email((String) attributes.get("email"))
			.build()),

	KAKAO("kakao", (attributes) -> {
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

		return MemberProfile.builder()
			.email((String) kakaoAccount.get("email"))
			.build();
	});

	private final String registrationId; // 서비스 제공자
	private final Function<Map<String, Object>, MemberProfile> of;

	OAuthProvider(String registrationId, Function<Map<String, Object>, MemberProfile> of) {
		this.registrationId = registrationId;
		this.of = of;
	}

	public static MemberProfile extract(String registrationId, Map<String, Object> attributes) {
		return Arrays.stream(values()) // 모든 enum을 스트림을 열고
			.filter(provider -> registrationId.equals(provider.registrationId)) // 전달 받은 제공자와 일치하는 것만 선택
			.findFirst()
			.orElseThrow(IllegalArgumentException::new) // 일치하지 않으면 예외 던지기 (구글 카카오 깃헙 로그인이 아닌 경우)
			.of.apply(attributes); // of 생성자로 멤버프로필 생성
	}

	@Override
	public String getName() {
		return name();
	}

	@Override
	public String getDescription() {
		return this.registrationId;
	}
}
