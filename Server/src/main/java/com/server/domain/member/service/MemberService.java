package com.server.domain.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.member.service.dto.MemberServiceRequest;
import com.server.global.exception.businessexception.memberexception.MemberDuplicateException;
import com.server.module.email.service.MailService;

@Service
@Transactional(readOnly = true)
public class MemberService {

	private final MemberRepository memberRepository;
	private final MailService mailService;
	private final PasswordEncoder passwordEncoder;

	public MemberService(MemberRepository memberRepository, MailService mailService, PasswordEncoder passwordEncoder) {
		this.memberRepository = memberRepository;
		this.mailService = mailService;
		this.passwordEncoder = passwordEncoder;
	}

	public void signUp(MemberServiceRequest.Create create) {
		checkDuplicationEmail(create.getEmail());
		mailService.checkEmailCertify(create.getEmail());

		Channel channel = new Channel();

		Member member = Member.createMember(create.getEmail(), passwordEncoder.encode(create.getPassword()),
			create.getNickname());

		memberRepository.save(member);
	}

	public void checkDuplicationEmail(String email) {
		memberRepository.findByEmail(email).ifPresent(member -> {
			throw new MemberDuplicateException();
		});
	}
}
