package com.server.domain.member.controller;

import javax.servlet.annotation.MultipartConfig;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.server.domain.member.controller.dto.MemberApiRequest;
import com.server.domain.member.entity.Member;
import com.server.domain.member.service.MemberService;
import com.server.global.annotation.LoginId;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;

@RestController
@RequestMapping("/members")
public class MemberController {

	private final MemberService memberService;

	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@GetMapping("/{member-id}")
	public ResponseEntity<ApiSingleResponse> getMember(@PathVariable("member-id") Long memberId,
														@LoginId Long loginId) {

		memberService.getMember(memberId, loginId);

		return ResponseEntity.ok(ApiSingleResponse.ok(new Object(), "test"));
	}

	@GetMapping("/{member-id}/rewards")
	public ResponseEntity<ApiSingleResponse> getRewards(@PathVariable("member-id") Long memberId) {
		memberService.getRewards(memberId);
		return ResponseEntity.ok(ApiSingleResponse.ok(new Object(), "test"));
	}

	@GetMapping("/{member-id}/subscribes")
	public ResponseEntity<ApiSingleResponse> getSubscribes(@PathVariable("member-id") Long memberId) {
		memberService.getSubscribes(memberId);

		return ResponseEntity.ok(ApiSingleResponse.ok(new Object(), "test"));
	}

	@GetMapping("/{member-id}/likes")
	public ResponseEntity<ApiPageResponse> getLikes(@PathVariable("member-id") Long memberId,
													@RequestParam("page") int page) {
		memberService.getLikes(memberId, page);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{member-id}/carts")
	public ResponseEntity<ApiPageResponse> getCarts(@PathVariable("member-id") Long memberId,
													@RequestParam("page") int page) {
		memberService.getCarts(memberId, page);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{member-id}/pays")
	public ResponseEntity<ApiPageResponse> getPays(@PathVariable("member-id") Long memberId,
													@RequestParam("page") int page,
													@RequestParam("month") int month) {
		memberService.getPays(memberId, page, month);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{member-id}/watchs")
	public ResponseEntity<ApiPageResponse> getWatchs(@PathVariable("member-id") Long memberId,
													@RequestParam("page") int page,
													@RequestParam("day") int day) {
		memberService.getWatchs(memberId, page, day);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PatchMapping("/{member-id}")
	public ResponseEntity<ApiSingleResponse> updateNickname(@PathVariable("member-id") Long memberId,
														@RequestBody MemberApiRequest.Nickname request) {
		memberService.updateNickname(request.toServiceRequest());
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{member-id}/image")
	public ResponseEntity<ApiSingleResponse> updateImage(@PathVariable("member-id") Long memberId,
		@RequestBody MemberApiRequest.Image request) {
		memberService.updateImage(request.getImage());
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/password")
	public ResponseEntity<Void> updatePassword(@LoginId Long loginId,
												@RequestBody @Valid MemberApiRequest.Password request) {
		request.setLoginId(loginId);

		memberService.updatePassword(request.toServiceRequest());

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{member-id}")
	public ResponseEntity<Void> deleteMember(@PathVariable("member-id") Long memberId) {
		memberService.deleteMember(memberId);

		return ResponseEntity.noContent().build();
	}
}
