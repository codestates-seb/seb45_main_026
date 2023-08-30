package com.server.auth.aop;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;

@Component
public class AuthInitializer {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public AuthInitializer(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@PostConstruct
	public void initialize() {
		Member member = Member.createMember(
			"test@email.com",
			passwordEncoder.encode("qwer1234!"),
			"테스트 사용자"
		);

		memberRepository.save(member);
	}
}
