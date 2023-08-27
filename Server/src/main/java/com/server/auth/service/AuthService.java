package com.server.auth.service;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.auth.jwt.service.JwtProvider;
import com.server.auth.service.dto.AuthServiceRequest;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
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

	public void sendEmail(AuthServiceRequest.Send request) throws Exception {
		String email = request.getEmail();

		memberService.checkDuplicationEmail(email);
		mailService.sendEmail(email);
	}

	public void resetPassword(AuthServiceRequest.Reset request) {

	}
}
