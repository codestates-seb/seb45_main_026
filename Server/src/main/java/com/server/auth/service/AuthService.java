package com.server.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.auth.service.dto.AuthServiceRequest;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.member.service.MemberService;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.module.email.service.MailService;

@Service
@Transactional
public class AuthService {

	private final MailService mailService;
	private final MemberService memberService;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public AuthService(MailService mailService, MemberService memberService, MemberRepository memberRepository,
		PasswordEncoder passwordEncoder) {
		this.mailService = mailService;
		this.memberService = memberService;
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public void sendEmail(AuthServiceRequest.Send request, String type) throws Exception {
		String email = request.getEmail();

		if(type.equals("signup")) {
			memberService.checkDuplicationEmail(email);
		}
		else {
			checkExistMember(email);
		}

		mailService.sendEmail(email);
	}

	public void updatePassword(AuthServiceRequest.Reset request) {
		mailService.checkEmailCertify(request.getEmail());

		Member member = checkExistMember(request.getEmail());
		member.setPassword(passwordEncoder.encode(request.getPassword()));
	}

	private Member checkExistMember(String email) {
		return memberRepository.findByEmail(email).orElseThrow(
			MemberNotFoundException::new
		);
	}
}
