package com.server.domain.member.controller;

import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.annotation.MultipartConfig;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.server.domain.member.controller.dto.MemberApiRequest;
import com.server.domain.member.entity.Grade;
import com.server.domain.member.entity.Member;
import com.server.domain.member.service.MemberService;
import com.server.domain.member.service.dto.response.ProfileResponse;
import com.server.domain.member.service.dto.response.RewardsResponse;
import com.server.global.annotation.LoginId;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import com.server.module.s3.service.AwsService;

@RestController
@RequestMapping("/members")
public class MemberController {

	private final MemberService memberService;
	private final AwsService awsService;

	public MemberController(MemberService memberService, AwsService awsService) {
		this.memberService = memberService;
		this.awsService = awsService;
	}

	@GetMapping
	public ResponseEntity<ApiSingleResponse<ProfileResponse>> getMember(@LoginId Long loginId) {

		ProfileResponse profileResponse = memberService.getMember(loginId);

		return ResponseEntity.ok(ApiSingleResponse.ok(profileResponse, "프로필 조회 성공"));
	}

	@GetMapping("/rewards")
	public ResponseEntity<ApiPageResponse<RewardsResponse>> getRewards(@LoginId Long loginId) {
		Page<RewardsResponse> responses = memberService.getRewards(loginId);
		return ResponseEntity.ok(ApiPageResponse.ok(responses));
	}

	@GetMapping("/subscribes")
	public ResponseEntity<ApiPageResponse> getSubscribes(@LoginId Long loginId) {
		memberService.getSubscribes(loginId);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	// 좋아요 기능은 구현하지 않을 예정
	@GetMapping("/likes")
	public ResponseEntity<ApiPageResponse> getLikes(@LoginId Long loginId,
													@RequestParam("page") int page) {
		memberService.getLikes(loginId, page);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/carts")
	public ResponseEntity<ApiPageResponse> getCarts(@LoginId Long loginId,
													@RequestParam("page") int page) {
		memberService.getCarts(loginId, page);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/orders")
	public ResponseEntity<ApiPageResponse> getOrders(@LoginId Long loginId,
													@RequestParam("page") int page,
													@RequestParam("month") int month) {

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/playlists")
	public ResponseEntity<ApiPageResponse> getPlaylists(@LoginId Long loginId,
													@RequestParam("page") int page,
													@RequestParam("sort") String sort) {
		memberService.getPlaylists(loginId, page, sort);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/watchs")
	public ResponseEntity<ApiPageResponse> getWatchs(@LoginId Long loginId,
													@RequestParam("page") int page,
													@RequestParam("day") int day) {
		memberService.getWatchs(loginId, page, day);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PatchMapping
	public ResponseEntity<Void> updateNickname(@LoginId Long loginId,
												@RequestBody MemberApiRequest.Nickname request) {
		memberService.updateNickname(request.toServiceRequest());
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/image")
	public ResponseEntity<ApiSingleResponse> updateImage(@LoginId Long loginId,
		@RequestBody MemberApiRequest.Image request) {
		// 주소 생성 및 파일명을 해당 멤버에 저장
		memberService.updateImage(request.getImageName());
		String presignedUrl = awsService.getUploadImageUrl(request.getImageName(), request.getImageType());
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", presignedUrl);

		return ResponseEntity.ok().headers(headers).build();
	}

	@PatchMapping("/password")
	public ResponseEntity<Void> updatePassword(@LoginId Long loginId,
												@RequestBody @Valid MemberApiRequest.Password request) {
		request.setLoginId(loginId);

		memberService.updatePassword(request.toServiceRequest());

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteMember(@PathVariable("member-id") Long memberId) {
		memberService.deleteMember(memberId);

		return ResponseEntity.noContent().build();
	}
}
