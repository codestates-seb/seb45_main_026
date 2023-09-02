package com.server.domain.member.controller;

import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.annotation.MultipartConfig;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import com.server.module.s3.service.dto.FileType;
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
import com.server.domain.member.controller.dto.PlaylistsSort;
import com.server.domain.member.entity.Grade;
import com.server.domain.member.entity.Member;
import com.server.domain.member.service.MemberService;
import com.server.domain.member.service.dto.response.CartsResponse;
import com.server.domain.member.service.dto.response.OrdersResponse;
import com.server.domain.member.service.dto.response.PlaylistsResponse;
import com.server.domain.member.service.dto.response.ProfileResponse;
import com.server.domain.member.service.dto.response.RewardsResponse;
import com.server.domain.member.service.dto.response.SubscribesResponse;
import com.server.domain.member.service.dto.response.WatchsResponse;
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
	public ResponseEntity<ApiPageResponse<RewardsResponse>> getRewards(@RequestParam(value = "page", defaultValue = "1") int page,
																		@RequestParam(value = "size", defaultValue = "16") int size,
																		@LoginId Long loginId) {

		Page<RewardsResponse> responses = memberService.getRewards(loginId, page, size);

		return ResponseEntity.ok(ApiPageResponse.ok(responses));
	}

	@GetMapping("/subscribes")
	public ResponseEntity<ApiPageResponse<SubscribesResponse>> getSubscribes(@RequestParam(value = "page", defaultValue = "1") int page,
																			@RequestParam(value = "size", defaultValue = "16") int size,
																			@LoginId Long loginId) {

		Page<SubscribesResponse> responses = memberService.getSubscribes(loginId, page, size);

		return ResponseEntity.ok(ApiPageResponse.ok(responses));
	}

	@GetMapping("/carts")
	public ResponseEntity<ApiPageResponse<CartsResponse>> getCarts(@LoginId Long loginId,
																	@RequestParam(value = "page", defaultValue = "1") int page,
																	@RequestParam(value = "size", defaultValue = "20") int size) {

		Page<CartsResponse> responses = memberService.getCarts(loginId, page, size);

		return ResponseEntity.ok(ApiPageResponse.ok(responses));
	}

	@GetMapping("/orders")
	public ResponseEntity<ApiPageResponse<OrdersResponse>> getOrders(@LoginId Long loginId,
													@RequestParam(value = "page", defaultValue = "1") int page,
													@RequestParam(value = "size", defaultValue = "4") int size,
													@RequestParam(value = "month", defaultValue = "1") int month) {

		Page<OrdersResponse> responses = memberService.getOrders(loginId, page, size, month);

		return ResponseEntity.ok(ApiPageResponse.ok(responses));
	}

	@GetMapping("/playlists")
	public ResponseEntity<ApiPageResponse<PlaylistsResponse>> getPlaylists(@LoginId Long loginId,
													@RequestParam(value = "page", defaultValue = "1") @Positive(message = "{validation.positive}") int page,
													@RequestParam(value = "size", defaultValue = "16") @Positive(message = "{validation.positive}") int size,
													@RequestParam(value = "sort", defaultValue = "created-date") PlaylistsSort sort) {

		Page<PlaylistsResponse> responses = memberService.getPlaylists(loginId, page, size, sort.getSort());

		return ResponseEntity.ok(ApiPageResponse.ok(responses));
	}

	@GetMapping("/watchs")
	public ResponseEntity<ApiPageResponse<WatchsResponse>> getWatchs(@LoginId Long loginId,
																	@RequestParam(value = "page", defaultValue = "1") @Positive(message = "{validation.positive}") int page,
																	@RequestParam(value = "size", defaultValue = "16") @Positive(message = "{validation.positive}") int size,
																	@RequestParam(value = "day", defaultValue = "30") @Positive(message = "{validation.positive}") int day) {

		Page<WatchsResponse> responses = memberService.getWatchs(loginId, page, size, day);

		return ResponseEntity.ok(ApiPageResponse.ok(responses));
	}

	@PatchMapping
	public ResponseEntity<Void> updateNickname(@LoginId Long loginId,
												@RequestBody @Valid MemberApiRequest.Nickname request) {
		memberService.updateNickname(request.toServiceRequest(), loginId);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/image")
	public ResponseEntity<Void> updateImage(@LoginId Long loginId,
														@RequestBody @Valid MemberApiRequest.Image request) {

		memberService.updateImage(loginId);

		String presignedUrl = awsService.getImageUploadUrl(
				loginId,
				request.getImageName(),
				FileType.PROFILE_IMAGE,
				request.getImageType());

		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", presignedUrl);

		return ResponseEntity.ok().headers(headers).build();
	}

	@DeleteMapping("/image")
	public ResponseEntity<Void> deleteImage(@LoginId Long loginId) {
		memberService.deleteImage(loginId);

		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/password")
	public ResponseEntity<Void> updatePassword(@LoginId Long loginId,
												@RequestBody @Valid MemberApiRequest.Password request) {

		memberService.updatePassword(request.toServiceRequest(loginId), loginId);

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteMember(@LoginId Long loginId) {
		memberService.deleteMember(loginId);

		return ResponseEntity.noContent().build();
	}
}
