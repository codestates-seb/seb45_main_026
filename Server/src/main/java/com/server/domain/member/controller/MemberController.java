package com.server.domain.member.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.domain.member.controller.dto.MemberApiRequest;
import com.server.domain.member.service.MemberService;
import com.server.global.annotation.LoginId;
import com.server.global.reponse.ApiSingleResponse;

@RestController
@RequestMapping("/members")
public class MemberController {

	private final MemberService memberService;

	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@GetMapping("/{member-id}")
	public ResponseEntity<ApiSingleResponse> getMember(@PathVariable("member-id") Long memberId) {

		return ResponseEntity.ok(ApiSingleResponse.ok(new Object(), "test"));
	}

	@PatchMapping("/password")
	public ResponseEntity<Void> updatePassword(@LoginId Long loginId,
												@RequestBody @Valid MemberApiRequest.Password request) {
		request.setLoginId(loginId);

		memberService.changePassword(request.toServiceRequest());

		return ResponseEntity.noContent().build();
	}
}
