package com.server.domain.member.aop;

import java.time.LocalDateTime;
import java.util.List;

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
import com.server.domain.member.service.dto.response.PlaylistsResponse;
import com.server.domain.member.service.dto.response.ProfileResponse;
import com.server.domain.member.service.dto.response.RewardsResponse;
import com.server.domain.member.service.dto.response.SubscribesResponse;
import com.server.domain.member.service.dto.response.WatchsResponse;
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

	@Around("execution(* com.server.domain.member.controller.MemberController.getMember(..))")
	public ResponseEntity<ApiSingleResponse<ProfileResponse>> getMember(ProceedingJoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		Long memberId = (Long) args[0];

		ProfileResponse profileResponse = ProfileResponse.builder()
			.memberId(memberId)
			.email("stub@email.com")
			.nickname("stubName")
			.imageUrl(awsService.getImageUrl("test"))
			.grade(Grade.PLATINUM)
			.reward(777)
			.createdDate(LocalDateTime.now())
			.build();

		return ResponseEntity.ok(ApiSingleResponse.ok(profileResponse, "프로필 조회 성공"));
	}

	@Around("execution(* com.server.domain.member.controller.MemberController.getRewards(..))")
	public ResponseEntity<ApiPageResponse<RewardsResponse>> getRewards(ProceedingJoinPoint joinPoint) {
		List<RewardsResponse> responses = List.of(
			RewardsResponse.builder()
				.entityId(1L)
				.rewardType(RewardType.VIDEO)
				.rewardPoint(100)
				.date(LocalDateTime.now())
				.build(),
			RewardsResponse.builder()
				.entityId(33L)
				.rewardType(RewardType.QUIZ)
				.rewardPoint(10)
				.date(LocalDateTime.now())
				.build(),
			RewardsResponse.builder()
				.entityId(114L)
				.rewardType(RewardType.VIDEO)
				.rewardPoint(300)
				.date(LocalDateTime.now())
				.build(),
			RewardsResponse.builder()
				.entityId(418L)
				.rewardType(RewardType.QUIZ)
				.rewardPoint(5)
				.date(LocalDateTime.now())
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
				.imageUrl(awsService.getImageUrl("test"))
				.build(),
			SubscribesResponse.builder()
				.memberId(8136L)
				.channelName("study channel")
				.subscribes(486)
				.imageUrl(awsService.getImageUrl("test"))
				.build(),
			SubscribesResponse.builder()
				.memberId(931L)
				.channelName("music channel")
				.subscribes(333)
				.imageUrl(awsService.getImageUrl("test"))
				.build(),
			SubscribesResponse.builder()
				.memberId(49L)
				.channelName("game channel")
				.subscribes(777)
				.imageUrl(awsService.getImageUrl("test"))
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
				.thumbnailUrl(awsService.getImageUrl("test22"))
				.views(333)
				.createdDate(LocalDateTime.now())
				.price(100000)
				.channel(LikesResponse.Channel.builder()
					.memberId(3L)
					.channelName("Linus Torvalds")
					.isSubscribed(true)
					.subscribes(8391)
					.imageUrl(awsService.getImageUrl("test"))
					.build())
				.build(),
			LikesResponse.builder()
				.videoId(9514L)
				.videoName("컴활 강의")
				.thumbnailUrl(awsService.getImageUrl("test22"))
				.views(777)
				.createdDate(LocalDateTime.now())
				.price(70000)
				.channel(LikesResponse.Channel.builder()
					.memberId(361L)
					.channelName("Bill Gates")
					.isSubscribed(true)
					.subscribes(9999)
					.imageUrl(awsService.getImageUrl("test"))
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
				.thumbnailUrl(awsService.getImageUrl("test22"))
				.views(333)
				.createdDate(LocalDateTime.now())
				.price(100000)
				.channel(CartsResponse.Channel.builder()
					.channelId(3L)
					.channelName("Linus Torvalds")
					.subscribes(8391)
					.imageUrl(awsService.getImageUrl("test"))
					.build())
				.build(),
			CartsResponse.builder()
				.videoId(9514L)
				.videoName("컴활 강의")
				.thumbnailUrl(awsService.getImageUrl("test22"))
				.views(777)
				.createdDate(LocalDateTime.now())
				.price(70000)
				.channel(CartsResponse.Channel.builder()
					.channelId(361L)
					.channelName("Bill Gates")
					.subscribes(9999)
					.imageUrl(awsService.getImageUrl("test"))
					.build())
				.build()
		);

		PageImpl<CartsResponse> page = new PageImpl<>(responses);

		return ResponseEntity.ok(ApiPageResponse.ok(page));
	}

	@Around("execution(* com.server.domain.member.controller.MemberController.getPlaylists(..))")
	public ResponseEntity<ApiPageResponse<PlaylistsResponse>> getPlaylists(ProceedingJoinPoint joinPoint) {
		List<PlaylistsResponse> responses = List.of(
			PlaylistsResponse.builder()
				.videoId(321L)
				.videoName("가볍게 배우는 알고리즘")
				.thumbnailFile(awsService.getImageUrl("test22"))
				.star(4.7f)
				.price(20000)
				.modifiedDate(LocalDateTime.now())
				.channel(
					PlaylistsResponse.Channel.builder()
						.channelId(23L)
						.channelName("알고리즘 채널")
						.build()
				)
				.build(),
			PlaylistsResponse.builder()
				.videoId(2218L)
				.videoName("더 가볍게 배우는 알고리즘")
				.thumbnailFile(awsService.getImageUrl("test22"))
				.star(3.4f)
				.price(10000)
				.modifiedDate(LocalDateTime.now())
				.channel(
					PlaylistsResponse.Channel.builder()
						.channelId(23L)
						.channelName("알고리즘 채널")
						.build()
				)
				.build(),
			PlaylistsResponse.builder()
				.videoId(7831L)
				.videoName("많이 가볍게 배우는 알고리즘")
				.thumbnailFile(awsService.getImageUrl("test22"))
				.star(2.9f)
				.price(25000)
				.modifiedDate(LocalDateTime.now())
				.channel(
					PlaylistsResponse.Channel.builder()
						.channelId(23L)
						.channelName("알고리즘 채널")
						.build()
				)
				.build(),
			PlaylistsResponse.builder()
				.videoId(321L)
				.videoName("진짜 가볍게 배우는 알고리즘")
				.thumbnailFile(awsService.getImageUrl("test22"))
				.star(1.8f)
				.price(32000)
				.modifiedDate(LocalDateTime.now())
				.channel(
					PlaylistsResponse.Channel.builder()
						.channelId(23L)
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
				.thumbnailFile(awsService.getImageUrl("test22"))
				.createdDate(LocalDateTime.now())
				.channel(WatchsResponse.Channel.builder()
					.channelId(4325L)
					.channelName("채널1")
					.build())
				.build(),
			WatchsResponse.builder()
				.videoId(791L)
				.videoName("리액트")
				.thumbnailFile(awsService.getImageUrl("test22"))
				.createdDate(LocalDateTime.now())
				.channel(WatchsResponse.Channel.builder()
					.channelId(4325L)
					.channelName("채널2")
					.build())
				.build(),
			WatchsResponse.builder()
				.videoId(791L)
				.videoName("스프링")
				.thumbnailFile(awsService.getImageUrl("test22"))
				.createdDate(LocalDateTime.now())
				.channel(WatchsResponse.Channel.builder()
					.channelId(4325L)
					.channelName("채널3")
					.build())
				.build(),
			WatchsResponse.builder()
				.videoId(791L)
				.videoName("자바")
				.thumbnailFile(awsService.getImageUrl("test22"))
				.createdDate(LocalDateTime.now())
				.channel(WatchsResponse.Channel.builder()
					.channelId(4325L)
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
}
