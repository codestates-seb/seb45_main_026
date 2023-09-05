package com.server.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.server.auth.service.dto.AuthServiceRequest;
import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Member;
import com.server.global.exception.businessexception.mailexception.MailCertificationException;
import com.server.global.exception.businessexception.memberexception.MemberDuplicateException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.testhelper.ServiceTest;

public class AuthServiceTest extends ServiceTest {

	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	AuthService authService;

	@TestFactory
	@DisplayName("이메일 전송 예외 처리 테스트")
	Collection<DynamicTest> checkExistMember() {
		Member member = createAndSaveMember();

		AuthServiceRequest.Send sendPassword = AuthServiceRequest.Send.builder()
				.email("test@email.com")
				.build();

		AuthServiceRequest.Send sendSign = AuthServiceRequest.Send.builder()
			.email("test@gmail.com")
			.build();

		return List.of(
			DynamicTest.dynamicTest(
				"비밀번호 찾기를 위한 이메일 전송인 경우 존재하지 않는 이메일 예외 처리 테스트",
				() -> {
					assertThrows(MemberNotFoundException.class, () -> authService.sendEmail(sendPassword, "password"));
				}
			),
			DynamicTest.dynamicTest(
				"회원가입을 위한 이메일 전송인 경우 존재하지 않는 이메일 예외 처리 테스트",
				() -> {
					assertThrows(MemberDuplicateException.class, () -> authService.sendEmail(sendSign, "signup"));
				}
			)
		);
	}


	@TestFactory
	@DisplayName("비밀번호 찾기 테스트")
	Collection<DynamicTest> updatePassword() {
		Member member = createAndSaveMemberWithEncodingPassword();

		AuthServiceRequest.Reset reset = AuthServiceRequest.Reset.builder()
			.email("test@gmail.com")
			.password("qwer1234!")
			.build();

		return List.of(
			DynamicTest.dynamicTest(
				"성공하는 경우 비밀번호가 정상적으로 바뀌는지 테스트",
				() -> {
					when(redisService.getData("test@gmail.com")).thenReturn("true");
					authService.updatePassword(reset);
					assertThat(passwordEncoder.matches(reset.getPassword(), member.getPassword())).isEqualTo(true);
				}
			),
			DynamicTest.dynamicTest(
				"인증되지 않은 인증번호인 경우 예외 테스트",
				() -> {
					when(redisService.getData("test@gmail.com")).thenReturn("false");
					assertThrows(MailCertificationException.class, () -> authService.updatePassword(reset));
				}
			)
		);
	}

	@TestFactory
	@DisplayName("이메일 인증 테스트")
	Collection<DynamicTest> verifyEmail() {
		Member member = createAndSaveMemberWithEncodingPassword();
		String email = "test@gmail.com";
		String code = "abcd1234";

		AuthServiceRequest.Confirm confirm = AuthServiceRequest.Confirm.builder()
			.email(email)
			.code(code)
			.build();

		return List.of(
			DynamicTest.dynamicTest(
				"인증이 성공적인 경우 예외 발생 없이 넝어가는지 테스트",
				() -> {
					when(redisService.getData(code)).thenReturn(email);
					assertDoesNotThrow(() -> authService.verifyEmail(confirm));
				}
			),
			DynamicTest.dynamicTest(
				"이메일 인증에 실패한 경우 예외가 발생하는지 테스트",
				() -> {
					when(redisService.getData(code)).thenReturn("test1234423423@gmail.com");
					assertThrows(MailCertificationException.class, () -> authService.verifyEmail(confirm));
				}
			)
		);
	}

	private Member createAndSaveMemberWithEncodingPassword() {
		Member member = Member.builder()
			.email("test@gmail.com")
			.password(passwordEncoder.encode("1q2w3e4r!"))
			.nickname("test")
			.authority(Authority.ROLE_USER)
			.reward(1000)
			.imageFile("imageFile")
			.build();

		memberRepository.save(member);

		return member;
	}
}
