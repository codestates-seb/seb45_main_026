package com.server.auth.controller;

import static com.server.auth.util.AuthConstant.*;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.auth.controller.dto.AuthApiRequest;
import com.server.auth.oauth.service.OAuthService;
import com.server.auth.service.AuthService;
import com.server.domain.member.service.MemberService;
import com.server.global.annotation.LoginId;
import com.server.module.email.service.MailService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

	private final AuthService authService;
	private final OAuthService oAuthService;
	private final MailService mailService;
	private final MemberService memberService;

	public AuthController(AuthService authService, OAuthService oAuthService, MailService mailService,
		MemberService memberService) {
		this.authService = authService;
		this.oAuthService = oAuthService;
		this.mailService = mailService;
		this.memberService = memberService;
	}

	@PostMapping(value = { "/signup/email", "/password/email" })
	public ResponseEntity<Void> sendEmail(@RequestBody @Valid AuthApiRequest.Send request) throws Exception {
		authService.sendEmail(request.toServiceRequest());
		return ResponseEntity.noContent().build();
	}

	@PostMapping(value = { "/signup/confirm", "/password/confirm" })
	public ResponseEntity<Void> confirmEmail(@RequestBody @Valid AuthApiRequest.Confirm request) {
		mailService.verifyEmail(request.toServiceRequest());
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/signup")
	public ResponseEntity<Void> signup(@RequestBody @Valid AuthApiRequest.SignUp request) {
		memberService.signUp(request.toServiceRequest());
		return new ResponseEntity<>(HttpStatus.CREATED); // 나중에 로케이션으로 바꾸기
	}

	@PostMapping("/oauth")
	public ResponseEntity<Void> oauth(@RequestBody @Valid AuthApiRequest.OAuth request) {
		AuthApiRequest.Token token = oAuthService.login(request.getProvider(), request.getCode());

		HttpHeaders headers = new HttpHeaders();
		headers.add(AUTHORIZATION, BEARER + token.getAccessToken());
		headers.add(REFRESH, BEARER + token.getRefreshToken());

		return ResponseEntity.ok().headers(headers).build();
	}

	@PatchMapping("/password")
	public ResponseEntity<Void> updatePassword(@RequestBody @Valid AuthApiRequest.Reset request, @LoginId Long loginId) {
		authService.updatePassword(request.toServiceRequest(), loginId);
		return ResponseEntity.noContent().build();
	}
}
