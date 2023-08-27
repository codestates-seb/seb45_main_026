package com.server.auth.controller;

import static com.server.auth.util.AuthConstant.*;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	@GetMapping("/oauth")
	public ResponseEntity<Void> oauth(@ModelAttribute AuthApiRequest.OAuth oAuth) {
		AuthApiRequest.Token token = oAuthService.login(oAuth.getProvider(), oAuth.getCode());

		HttpHeaders headers = new HttpHeaders();
		headers.add(AUTHORIZATION, BEARER + " " + token.getAccessToken());
		headers.add(REFRESH, BEARER + " " + token.getRefreshToken());

		return ResponseEntity.ok().headers(headers).build();
	}

	@GetMapping("/test")
	public void annotationTest(@LoginId Long id) {
		System.out.println("아이디 : " + id);
	}
}
