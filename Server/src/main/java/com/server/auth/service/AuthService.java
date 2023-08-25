package com.server.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.domain.member.service.MemberService;
import com.server.module.email.service.MailService;

@Service
@Transactional
public class AuthService {

	private final MailService mailService;
	private final MemberService memberService;

	public AuthService(MailService mailService, MemberService memberService) {
		this.mailService = mailService;
		this.memberService = memberService;
	}

	public void sendEmail(String email) throws Exception {
		memberService.checkDuplicationEmail(email);
		mailService.sendEmail(email);
	}
}
