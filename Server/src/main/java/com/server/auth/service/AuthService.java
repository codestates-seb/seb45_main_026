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
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;

	public AuthService(MailService mailService, MemberService memberService, MemberRepository memberRepository,
		PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
		this.mailService = mailService;
		this.memberService = memberService;
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtProvider = jwtProvider;
	}

	public void sendEmail(String email) throws Exception {
		memberService.checkDuplicationEmail(email);
		mailService.sendEmail(email);
	}

	public String login(AuthServiceRequest.Login member, HttpServletResponse response) throws Exception {
		Member findMember = memberRepository.findByEmail(member.getEmail())
			.orElseThrow();

		if (!passwordEncoder.matches(member.getPassword(), findMember.getPassword())) {
			throw new IllegalAccessException();
		}

		return jwtProvider.createToken(findMember.getEmail(), response);
	}
}
