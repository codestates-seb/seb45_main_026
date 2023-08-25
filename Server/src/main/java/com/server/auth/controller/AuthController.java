package com.server.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.server.auth.controller.dto.AuthApiRequest;
import com.server.auth.service.AuthService;
import com.server.domain.member.service.MemberService;
import com.server.module.email.service.MailService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

	private final AuthService authService;
	private final MailService mailService;
	private final MemberService memberService;

	public AuthController(AuthService authService, MailService mailService, MemberService memberService) {
		this.authService = authService;
		this.mailService = mailService;
		this.memberService = memberService;
	}

	@PostMapping("/signup/email")
	public ResponseEntity<String> sendEmail(@RequestParam("email") String email) throws Exception {
		authService.sendEmail(email);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/signup/confirm")
	public ResponseEntity<Void> confirmEmail(@RequestBody AuthApiRequest.Confirm confirm) {
		mailService.verifyEmail(confirm.getEmail(), confirm.getCode());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/signup")
	public ResponseEntity<Void> signup(@RequestBody AuthApiRequest.SignUp signUp) {
		memberService.signUp(signUp.toServiceRequest());
		return new ResponseEntity<>(HttpStatus.CREATED);
	}


}
