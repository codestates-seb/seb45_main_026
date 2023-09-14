package com.server.domain.member.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import com.server.global.exception.businessexception.memberexception.MemberNotUpdatedException;
import com.server.global.exception.businessexception.orderexception.RewardNotEnoughException;

public class MemberEntityTest {

	@Test
	@DisplayName("지정한 이메일, 패스워드, 닉네임을 가진 유저 권한의 회원이 만들어진다.")
	void createMember() {
		String email = "test@email.com";
		String password = "abcd1234!";
		String nickname = "test";

		Member member = Member.createMember(
			email,
			password,
			nickname
		);

		assertThat(member.getEmail()).isEqualTo(email);
		assertThat(member.getPassword()).isEqualTo(password);
		assertThat(member.getNickname()).isEqualTo(nickname);
		assertThat(member.getAuthority()).isEqualTo(Authority.ROLE_USER);
	}

	@TestFactory
	@DisplayName("패스워드 변경 테스트")
	Collection<DynamicTest> updatePassword() {
		String email = "test@email.com";
		String password = "abcd1234!";
		String nickname = "test";

		Member member = Member.createMember(
			email,
			password,
			nickname
		);

		return List.of(
			DynamicTest.dynamicTest(
				"변경하려는 패스워드와 기존 패스워드와 일치하는 경우",
				() -> {
					assertThrows(MemberNotUpdatedException.class, () -> member.updatePassword(password));
				}
			),
			DynamicTest.dynamicTest(
				"패스워드가 정상적으로 변경 되는지 테스트",
				() -> {
					String newPassword = "qwer1234!";
					member.updatePassword(newPassword);

					assertThat(member.getPassword()).isEqualTo(newPassword);
				}
			)
		);
	}

	@TestFactory
	@DisplayName("닉네임 변경 테스트")
	Collection<DynamicTest> updateNickname() {
		String email = "test@email.com";
		String password = "abcd1234!";
		String nickname = "test";

		Member member = Member.createMember(
			email,
			password,
			nickname
		);

		return List.of(
			DynamicTest.dynamicTest(
				"변경하려는 닉네임과 기존 닉네임이 일치하는 경우",
				() -> {
					assertThrows(MemberNotUpdatedException.class, () -> member.updateNickname(nickname));
				}
			),
			DynamicTest.dynamicTest(
				"닉네임이 정상적으로 변경 되는지 테스트",
				() -> {
					String newNickname = "testtest";
					member.updateNickname(newNickname);

					assertThat(member.getNickname()).isEqualTo(newNickname);
				}
			)
		);
	}

	@TestFactory
	@DisplayName("프로필 이미지 수정 및 삭제 테스트")
	Collection<DynamicTest> updateAndDeleteImageFile() {
		String imageFile = "newImage";

		Member member = createMemberWithImage();

		return List.of(
			DynamicTest.dynamicTest(
				"이미지 파일명이 정상적으로 변경되는지 테스트",
				() -> {
					member.updateImageFile(imageFile);

					assertThat(member.getImageFile()).isEqualTo(member.getMemberId() + "/profile/" + imageFile);
				}
			),
			DynamicTest.dynamicTest(
				"이미지 파일명이 정상적으로 삭제(null)되는지 테스트",
				() -> {
					member.deleteImageFile();

					assertThat(member.getImageFile()).isNull();
				}
			)
		);
	}

	@TestFactory
	@DisplayName("리워드 관리 테스트")
	Collection<DynamicTest> reward() {
		Member member = createMemberWithReward();

		return List.of(
			DynamicTest.dynamicTest(
				"리워드가 정상적으로 증가하는지 테스트",
				() -> {
					member.addReward(1);

					assertThat(member.getReward()).isEqualTo(101);
				}
			),
			DynamicTest.dynamicTest(
				"리워드가 정상적으로 감소하는지 테스트",
				() -> {
					member.minusReward(1);

					assertThat(member.getReward()).isEqualTo(100);
				}
			),
			DynamicTest.dynamicTest(
				"사용할 리워드가 충분한 경우 예외를 던지지 않는지 테스트",
				() -> {
					assertDoesNotThrow(() -> member.checkReward(100));
				}
			),
			DynamicTest.dynamicTest(
				"사용할 리워드가 충분하지 않은 경우 예외를 던지는지 테스트",
				() -> {
					assertThrows(
						RewardNotEnoughException.class,
						() -> member.checkReward(101)
					);
				}
			),
			DynamicTest.dynamicTest(
				"사용할 리워드가 충분한지 검증하는 메서드 테스트(충분한 경우)",
				() -> {
					assertThat(member.hasEnoughReward(100)).isTrue();
				}
			),
			DynamicTest.dynamicTest(
				"사용할 리워드가 충분한지 검증하는 메서드 테스트(충분하지 않은 경우)",
				() -> {
					assertThat(member.hasEnoughReward(101)).isFalse();
				}
			)
		);
	}

	private Member createMemberWithImage() {

		return Member.builder()
			.email("test@email.com")
			.nickname("test")
			.password("abcd1234!")
			.authority(Authority.ROLE_USER)
			.imageFile("image")
			.build();
	}

	private Member createMemberWithReward() {

		return Member.builder()
			.email("test@email.com")
			.nickname("test")
			.password("abcd1234!")
			.authority(Authority.ROLE_USER)
			.reward(100)
			.build();
	}
}
