package com.server.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.server.domain.channel.service.ChannelService;
import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.member.service.dto.request.MemberServiceRequest;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberDuplicateException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.memberexception.MemberNotUpdatedException;
import com.server.global.exception.businessexception.memberexception.MemberPasswordException;
import com.server.global.testhelper.ServiceTest;
import com.server.module.email.service.MailService;
import com.server.module.s3.service.AwsService;

public class MemberServiceTest extends ServiceTest {

	// @Autowired MailService mailService;
	// @Autowired ChannelService channelService;
	// @Autowired AwsService awsService;
	@Autowired PasswordEncoder passwordEncoder;
	@Autowired MemberService memberService;

	@Test
	@DisplayName("로그인한 사용자의 ID에 맞는 회원 테이블을 삭제한다.")
	void deleteMember() {
		Member member = createAndSaveMember();
		Long id = member.getMemberId();

		memberService.deleteMember(id);

		assertThrows(MemberNotFoundException.class, () -> memberService.findMemberBy(id));
	}

	@Test
	@DisplayName("로그인 아이디가 1보다 작지 않은지 로그인한 회원이 맞는지 검증한다.")
	void validateLoginId() {
		Long validLoginId = 123L;
		Long invalidLoginId = -1L;

		assertDoesNotThrow(() -> memberService.validateLoginId(validLoginId));

		assertThrows(MemberAccessDeniedException.class, () -> memberService.validateLoginId(invalidLoginId));
	}

	@Test
	@DisplayName("입력한 패스워드가 일치하는지 검증한다.")
	void validatePassword() {
		Member member = createMember();
		String password = member.getPassword();

		String validPassword = "1q2w3e4r!";
		String invalidPassword = "4q3w2e1r!";

		assertDoesNotThrow(() -> memberService.validatePassword(validPassword, password));

		assertThrows(MemberPasswordException.class, () -> memberService.validatePassword(invalidPassword, password));
	}

	@Test
	@DisplayName("프로필 이미지가 저장되는지 검증한다.")
	void updateImage() {
		Member member = createMember();

		Long loginId = member.getMemberId();
		String imageName = member.getIdFromEmail();

		memberService.updateImage(loginId);

		assertThat(imageName).isEqualTo("test");
		assertThat(member.getImageFile()).isNotNull().isEqualTo(imageName);
	}

	@Test
	@DisplayName("닉네임 변경이 정상적으로 수행되는지 검증한다.")
	void updateNickname() {
		Member member = createMember();
		Long loginId = member.getMemberId();

		MemberServiceRequest.Nickname valid =
			MemberServiceRequest.Nickname.builder().nickname("이름 바꾸기").build();
		MemberServiceRequest.Nickname invalid =
			MemberServiceRequest.Nickname.builder().build();

		assertThrows(MemberNotUpdatedException.class, () -> memberService.updateNickname(invalid, loginId));

		memberService.updateNickname(valid, loginId);
		assertThat(member.getNickname()).isEqualTo(valid.getNickname());
	}

	@Test
	@DisplayName(("패스워드 변경이 정상적으로 수행되는지 검증한다."))
	void updatePassword() {
		Member member = createMember();
		Long loginId = member.getMemberId();

		MemberServiceRequest.Password request = MemberServiceRequest.Password.builder()
			.prevPassword("1q2w3e4r!")
			.newPassword("qwer1234!")
			.build();

		memberService.updatePassword(request, loginId);
		assertThat(passwordEncoder.matches(request.getNewPassword(), member.getPassword())).isTrue();
	}

	@Test
	@DisplayName("이메일 중복 검증이 정상적으로 수행되는지 확인한다.")
	void checkDuplicationEmail() {
		Member member = createMember();

		String existEmail = member.getEmail();
		String notExistEmail = "notexist@email.com";

		assertThrows(MemberDuplicateException.class, () -> memberService.checkDuplicationEmail(existEmail));
		assertDoesNotThrow(() -> memberService.checkDuplicationEmail(notExistEmail));
	}

	private Member createMember() {
		Member member = Member.builder()
			.email("test@gmail.com")
			.password(passwordEncoder.encode("1q2w3e4r!"))
			.nickname("test")
			.authority(Authority.ROLE_USER)
			.reward(1000)
			.build();

		memberRepository.save(member);

		return member;
	}
}
