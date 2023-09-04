package com.server.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.server.auth.service.dto.AuthServiceRequest;
import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.member.service.MemberService;
import com.server.global.exception.businessexception.memberexception.MemberDuplicateException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.memberexception.MemberNotUpdatedException;
import com.server.module.email.service.MailService;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthServiceTest {

	@Autowired
	MailService mailService;
	@Autowired
	MemberService memberService;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	AuthService authService;

	@Mock
	MailService mockMailService;
	@Mock
	MemberRepository mockMemberRepository;
	@Mock
	PasswordEncoder mockPasswordEncoder;
	@InjectMocks
	AuthService mockAuthService;

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
	@DisplayName("비빌번호 찾기 테스트")
	Collection<DynamicTest> updatePassword() {
		Member member = createAndSaveMember();

		AuthServiceRequest.Reset reset = AuthServiceRequest.Reset.builder()
			.email("test@gmail.com")
			.password("qwer1234!")
			.build();

		return List.of(
			DynamicTest.dynamicTest(
				"성공하는 경우 비밀번호가 정상적으로 바뀌는지 테스트",
				() -> {
					when(mockMemberRepository.findByEmail(reset.getEmail())).thenReturn(Optional.ofNullable(member));
					when(mockPasswordEncoder.encode(reset.getPassword())).thenReturn(reset.getPassword());
					mockAuthService.updatePassword(reset);

					verify(mockMailService, times(1)).checkEmailCertify(reset.getEmail());
					verify(mockPasswordEncoder, times(1)).encode(reset.getPassword());
					assert member != null;
					assertThat(member.getPassword()).isEqualTo(reset.getPassword());
				}
			),
			DynamicTest.dynamicTest(
				"비밀번호 변경사항이 없는 경우 예외 처리 테스트",
				() -> {
					when(mockMemberRepository.findByEmail(reset.getEmail())).thenReturn(Optional.ofNullable(member));
					when(mockPasswordEncoder.encode(reset.getPassword())).thenReturn(member.getPassword());

					assertThrows(MemberNotUpdatedException.class, () -> mockAuthService.updatePassword(reset));
				}
			)
		);
	}


	private Member createAndSaveMember() {
		Member member = Member.builder()
			.email("test@gmail.com")
			.password("1q2w3e4r!")
			.nickname("test")
			.authority(Authority.ROLE_USER)
			.reward(1000)
			.imageFile("imageFile")
			.build();

		memberRepository.save(member);

		return member;
	}
}
