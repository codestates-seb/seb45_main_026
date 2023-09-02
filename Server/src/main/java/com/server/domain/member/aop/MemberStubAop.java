package com.server.domain.member.aop;

import java.time.LocalDateTime;
import java.util.List;

import com.server.module.s3.service.dto.FileType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.server.domain.member.entity.Grade;
import com.server.domain.member.service.dto.response.CartsResponse;
import com.server.domain.member.service.dto.response.LikesResponse;
import com.server.domain.member.service.dto.response.OrdersResponse;
import com.server.domain.member.service.dto.response.PlaylistsResponse;
import com.server.domain.member.service.dto.response.ProfileResponse;
import com.server.domain.member.service.dto.response.RewardsResponse;
import com.server.domain.member.service.dto.response.SubscribesResponse;
import com.server.domain.member.service.dto.response.WatchsResponse;
import com.server.domain.order.entity.OrderStatus;
import com.server.domain.reward.entity.RewardType;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import com.server.module.s3.service.AwsService;

@Aspect
@Component
public class MemberStubAop {
	private final AwsService awsService;

	public MemberStubAop(AwsService awsService) {
		this.awsService = awsService;
	}

//	@Around("execution(* com.server.domain.member.controller.MemberController.getMember(..))")
//	public ResponseEntity<ApiSingleResponse<ProfileResponse>> getMember(ProceedingJoinPoint joinPoint) {
//		ProfileResponse profileResponse = ProfileResponse.builder()
//			.memberId(1L)
//			.email("stub@email.com")
//			.nickname("stubName")
//			.imageUrl(awsService.getFileUrl(9999L, "test", FileType.PROFILE_IMAGE))
//			.grade(Grade.PLATINUM)
//			.reward(777)
//			.createdDate(LocalDateTime.now())
//			.build();
//
//		return ResponseEntity.ok(ApiSingleResponse.ok(profileResponse, "프로필 조회 성공"));
//	}

	@Around("execution(* com.server.domain.member.controller.MemberController.getRewards(..))")
	public ResponseEntity<ApiPageResponse<RewardsResponse>> getRewards(ProceedingJoinPoint joinPoint) {
		List<RewardsResponse> responses = List.of(
			RewardsResponse.builder()
				.questionId(1L)
				.videoId(1L)
				.rewardType(RewardType.QUIZ)
				.rewardPoint(100)
				.createdDate(LocalDateTime.now())
				.build(),
			RewardsResponse.builder()
				.videoId(298L)
				.rewardType(RewardType.VIDEO)
				.rewardPoint(10)
				.createdDate(LocalDateTime.now())
				.build(),
			RewardsResponse.builder()
				.videoId(114L)
				.rewardType(RewardType.VIDEO)
				.rewardPoint(300)
				.createdDate(LocalDateTime.now())
				.build(),
			RewardsResponse.builder()
				.questionId(1L)
				.videoId(418L)
				.rewardType(RewardType.QUIZ)
				.rewardPoint(5)
				.createdDate(LocalDateTime.now())
				.build()
		);

		PageImpl<RewardsResponse> page = new PageImpl<>(responses);

		return ResponseEntity.ok(ApiPageResponse.ok(page));
	}

	@Around("execution(* com.server.domain.member.controller.MemberController.getSubscribes(..))")
	public ResponseEntity<ApiPageResponse<SubscribesResponse>> getSubscribes(ProceedingJoinPoint joinPoint) {
		List<SubscribesResponse> responses = List.of(
			SubscribesResponse.builder()
				.memberId(23L)
				.channelName("vlog channel")
				.subscribes(1004)
				.imageUrl(awsService.getFileUrl(9999L, "test", FileType.PROFILE_IMAGE))
				.build(),
			SubscribesResponse.builder()
				.memberId(8136L)
				.channelName("study channel")
				.subscribes(486)
				.imageUrl(awsService.getFileUrl(9999L, "test", FileType.PROFILE_IMAGE))
				.build(),
			SubscribesResponse.builder()
				.memberId(931L)
				.channelName("music channel")
				.subscribes(333)
				.imageUrl(awsService.getFileUrl(9999L, "test", FileType.PROFILE_IMAGE))
				.build(),
			SubscribesResponse.builder()
				.memberId(49L)
				.channelName("game channel")
				.subscribes(777)
				.imageUrl(awsService.getFileUrl(9999L, "test", FileType.PROFILE_IMAGE))
				.build()
		);

		PageImpl<SubscribesResponse> page = new PageImpl<>(responses);

		return ResponseEntity.ok(ApiPageResponse.ok(page));
	}

	@Around("execution(* com.server.domain.member.controller.MemberController.getLikes(..))")
	public ResponseEntity<ApiPageResponse<LikesResponse>> getLikes(ProceedingJoinPoint joinPoint) {
		List<LikesResponse> responses = List.of(
			LikesResponse.builder()
				.videoId(151L)
				.videoName("리눅스 만드는 법")
				.thumbnailUrl(awsService.getFileUrl(9999L, "test22", FileType.PROFILE_IMAGE))
				.views(333)
				.createdDate(LocalDateTime.now())
				.price(100000)
				.channel(LikesResponse.Channel.builder()
					.memberId(3L)
					.channelName("Linus Torvalds")
					.isSubscribed(true)
					.subscribes(8391)
					.imageUrl(awsService.getFileUrl(9999L, "test", FileType.PROFILE_IMAGE))
					.build())
				.build(),
			LikesResponse.builder()
				.videoId(9514L)
				.videoName("컴활 강의")
				.thumbnailUrl(awsService.getFileUrl(9999L, "test22", FileType.PROFILE_IMAGE))
				.views(777)
				.createdDate(LocalDateTime.now())
				.price(70000)
				.channel(LikesResponse.Channel.builder()
					.memberId(361L)
					.channelName("Bill Gates")
					.isSubscribed(true)
					.subscribes(9999)
					.imageUrl(awsService.getFileUrl(9999L, "test", FileType.PROFILE_IMAGE))
					.build())
				.build()
		);

		PageImpl<LikesResponse> page = new PageImpl<>(responses);

		return ResponseEntity.ok(ApiPageResponse.ok(page));
	}

	@Around("execution(* com.server.domain.member.controller.MemberController.getCarts(..))")
	public ResponseEntity<ApiPageResponse<CartsResponse>> getCarts(ProceedingJoinPoint joinPoint) {
		List<CartsResponse> responses = List.of(
			CartsResponse.builder()
				.videoId(151L)
				.videoName("리눅스 만드는 법")
				.thumbnailUrl(awsService.getFileUrl(9999L, "test22", FileType.PROFILE_IMAGE))
				.views(333)
				.createdDate(LocalDateTime.now())
				.price(100000)
				.channel(CartsResponse.Channel.builder()
					.memberId(3L)
					.channelName("Linus Torvalds")
					.subscribes(8391)
					.imageUrl(awsService.getFileUrl(9999L, "test", FileType.PROFILE_IMAGE))
					.build())
				.build(),
			CartsResponse.builder()
				.videoId(9514L)
				.videoName("컴활 강의")
				.thumbnailUrl(awsService.getFileUrl(9999L, "test", FileType.PROFILE_IMAGE))
				.views(777)
				.createdDate(LocalDateTime.now())
				.price(70000)
				.channel(CartsResponse.Channel.builder()
					.memberId(361L)
					.channelName("Bill Gates")
					.subscribes(9999)
					.imageUrl(awsService.getFileUrl(9999L, "test", FileType.PROFILE_IMAGE))
					.build())
				.build()
		);

		PageImpl<CartsResponse> page = new PageImpl<>(responses);

		return ResponseEntity.ok(ApiPageResponse.ok(page));
	}

	@Around("execution(* com.server.domain.member.controller.MemberController.getOrders(..))")
	public ResponseEntity<ApiPageResponse<OrdersResponse>> getOrders(ProceedingJoinPoint joinPoint) {
		List<OrdersResponse> responses = List.of(
			OrdersResponse.builder()
				.orderId("aBzd031dpf414")
				.reward(300)
				.orderCount(4)
				.orderStatus(OrderStatus.ORDERED)
				.build(),
			OrdersResponse.builder()
				.orderId("dfghkdf908sd023")
				.reward(400)
				.orderCount(6)
				.orderStatus(OrderStatus.CANCELED)
				.build(),
			OrdersResponse.builder()
				.orderId("fd932jkfdgklgdf")
				.reward(200)
				.orderCount(3)
				.orderStatus(OrderStatus.COMPLETED)
				.build(),
			OrdersResponse.builder()
				.orderId("nvbio328sdfhs13")
				.reward(100)
				.orderCount(7)
				.orderStatus(OrderStatus.ORDERED)
				.build()
		);

		PageImpl<OrdersResponse> page = new PageImpl<>(responses);

		return ResponseEntity.ok(ApiPageResponse.ok(page));
	}

	@Around("execution(* com.server.domain.member.controller.MemberController.getPlaylists(..))")
	public ResponseEntity<ApiPageResponse<PlaylistsResponse>> getPlaylists(ProceedingJoinPoint joinPoint) {
		List<PlaylistsResponse> responses = List.of(
			PlaylistsResponse.builder()
				.videoId(321L)
				.videoName("가볍게 배우는 알고리즘")
				.thumbnailFile(awsService.getFileUrl(9999L, "test22", FileType.PROFILE_IMAGE))
				.star(4.7f)
				.modifiedDate(LocalDateTime.now())
				.channel(
					PlaylistsResponse.Channel.builder()
						.memberId(23L)
						.channelName("알고리즘 채널")
						.build()
				)
				.build(),
			PlaylistsResponse.builder()
				.videoId(2218L)
				.videoName("더 가볍게 배우는 알고리즘")
				.thumbnailFile(awsService.getFileUrl(9999L, "test22", FileType.PROFILE_IMAGE))
				.star(3.4f)
				.modifiedDate(LocalDateTime.now())
				.channel(
					PlaylistsResponse.Channel.builder()
						.memberId(23L)
						.channelName("알고리즘 채널")
						.build()
				)
				.build(),
			PlaylistsResponse.builder()
				.videoId(7831L)
				.videoName("많이 가볍게 배우는 알고리즘")
				.thumbnailFile(awsService.getFileUrl(9999L, "test22", FileType.PROFILE_IMAGE))
				.star(2.9f)
				.modifiedDate(LocalDateTime.now())
				.channel(
					PlaylistsResponse.Channel.builder()
						.memberId(23L)
						.channelName("알고리즘 채널")
						.build()
				)
				.build(),
			PlaylistsResponse.builder()
				.videoId(321L)
				.videoName("진짜 가볍게 배우는 알고리즘")
				.thumbnailFile(awsService.getFileUrl(9999L, "test22", FileType.PROFILE_IMAGE))
				.star(1.8f)
				.modifiedDate(LocalDateTime.now())
				.channel(
					PlaylistsResponse.Channel.builder()
						.memberId(23L)
						.channelName("알고리즘 채널")
						.build()
				)
				.build()
		);

		PageImpl<PlaylistsResponse> page = new PageImpl<>(responses);

		return ResponseEntity.ok(ApiPageResponse.ok(page));
	}

	@Around("execution(* com.server.domain.member.controller.MemberController.getWatchs(..))")
	public ResponseEntity<ApiPageResponse<WatchsResponse>> getWatchs(ProceedingJoinPoint joinPoint) {
		List<WatchsResponse> responses = List.of(
			WatchsResponse.builder()
				.videoId(791L)
				.videoName("알고리즘")
				.thumbnailFile(awsService.getFileUrl(9999L, "test22", FileType.PROFILE_IMAGE))
				.modifiedDate(LocalDateTime.now())
				.channel(WatchsResponse.Channel.builder()
					.memberId(4325L)
					.channelName("채널1")
					.build())
				.build(),
			WatchsResponse.builder()
				.videoId(791L)
				.videoName("리액트")
				.thumbnailFile(awsService.getFileUrl(9999L, "test22", FileType.PROFILE_IMAGE))
				.modifiedDate(LocalDateTime.now())
				.channel(WatchsResponse.Channel.builder()
					.memberId(4325L)
					.channelName("채널2")
					.build())
				.build(),
			WatchsResponse.builder()
				.videoId(791L)
				.videoName("스프링")
				.thumbnailFile(awsService.getFileUrl(9999L, "test22", FileType.PROFILE_IMAGE))
				.modifiedDate(LocalDateTime.now())
				.channel(WatchsResponse.Channel.builder()
					.memberId(4325L)
					.channelName("채널3")
					.build())
				.build(),
			WatchsResponse.builder()
				.videoId(791L)
				.videoName("자바")
				.thumbnailFile(awsService.getFileUrl(9999L, "test22", FileType.PROFILE_IMAGE))
				.modifiedDate(LocalDateTime.now())
				.channel(WatchsResponse.Channel.builder()
					.memberId(4325L)
					.channelName("채널3")
					.build())
				.build()
		);

		PageImpl<WatchsResponse> page = new PageImpl<>(responses);

		return ResponseEntity.ok(ApiPageResponse.ok(page));
	}

	@Around("execution(* com.server.domain.member.controller.MemberController.updateNickname(..))")
	public ResponseEntity<Void> updateNickname(ProceedingJoinPoint joinPoint) {
		return ResponseEntity.noContent().build();
	}

	@Around("execution(* com.server.domain.member.controller.MemberController.updateImage(..))")
	public ResponseEntity<Void> updateImage(ProceedingJoinPoint joinPoint) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", "http://www.presignedUrl.com");

		return ResponseEntity.ok().headers(headers).build();
	}

	@Around("execution(* com.server.domain.member.controller.MemberController.updatePassword(..))")
	public ResponseEntity<Void> updatePassword(ProceedingJoinPoint joinPoint) {
		return ResponseEntity.noContent().build();
	}

	@Around("execution(* com.server.domain.member.controller.MemberController.deleteMember(..))")
	public ResponseEntity<Void> deleteMember(ProceedingJoinPoint joinPoint) {
		return ResponseEntity.noContent().build();
	}

	@Around("execution(* com.server.domain.member.controller.MemberController.deleteMember(..))")
	public ResponseEntity<Void> confirmEmail(ProceedingJoinPoint pjp) {
		return ResponseEntity.noContent().build();
	}
}
