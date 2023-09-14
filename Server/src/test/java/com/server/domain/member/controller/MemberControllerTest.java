package com.server.domain.member.controller;

import static com.server.auth.util.AuthConstant.*;
import static com.server.global.testhelper.RestDocsUtil.pageResponseFields;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import com.server.domain.category.service.dto.response.CategoryResponse;
import com.server.domain.member.service.dto.response.RewardsResponse;
import com.server.domain.member.service.dto.response.PlaylistChannelDetailsResponse;
import com.server.domain.member.service.dto.response.PlaylistChannelResponse;
import com.server.global.reponse.ApiPageResponse;
import com.server.module.s3.service.dto.FileType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultActions;

import com.server.domain.member.controller.dto.MemberApiRequest;
import com.server.domain.member.entity.Grade;
import com.server.domain.member.service.dto.response.CartsResponse;
import com.server.domain.member.service.dto.response.OrdersResponse;
import com.server.domain.member.service.dto.response.PlaylistsResponse;
import com.server.domain.member.service.dto.response.ProfileResponse;
import com.server.domain.member.service.dto.response.SubscribesResponse;
import com.server.domain.member.service.dto.response.WatchsResponse;
import com.server.domain.order.entity.OrderStatus;
import com.server.domain.reward.entity.RewardType;
import com.server.global.reponse.ApiSingleResponse;
import com.server.global.testhelper.ControllerTest;
import com.server.global.testhelper.RestDocsUtil;
import com.server.module.s3.service.dto.ImageType;

// @Import(MemberStubAop.class)
// @EnableAspectJAutoProxy
public class MemberControllerTest extends ControllerTest {

	@Test
	@DisplayName("프로필 조회 성공 테스트")
	void getMember() throws Exception {
		// given
		Long memberId = 1L;

		ProfileResponse response = ProfileResponse.builder()
			.memberId(memberId)
			.email("stub@email.com")
			.nickname("stubName")
			.imageUrl("https://d2ouhv9pc4idoe.cloudfront.net/images/test")
			.grade(Grade.PLATINUM)
			.reward(777)
			.createdDate(LocalDateTime.now())
			.build();

		String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(response, "프로필 조회 성공"));

		given(memberService.getMember(Mockito.anyLong())).willReturn(response);

		// when
		ResultActions actions = mockMvc.perform(
			get("/members")
				.header(AUTHORIZATION, TOKEN)
				.accept(APPLICATION_JSON)
		);

		// then
		actions.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().string(apiResponse));

		// restdocs
		actions.andDo(documentHandler.document(
			requestHeaders(
				headerWithName(AUTHORIZATION).description("액세스 토큰")
			),
			responseFields(
				fieldWithPath("data.memberId").description("회원 아이디"),
				fieldWithPath("data.email").description("회원 이메일"),
				fieldWithPath("data.nickname").description("회원 닉네임"),
				fieldWithPath("data.imageUrl").description("회원 프로필 이미지"),
				fieldWithPath("data.grade").description("회원 등급"),
				fieldWithPath("data.reward").description("보유 중인 리워드"),
				fieldWithPath("data.createdDate").description("가입일"),
				fieldWithPath("code").description("응답 코드"),
				fieldWithPath("status").description("응답 상태"),
				fieldWithPath("message").description("응답 메시지")
			)
		));
	}

	@Test
	@DisplayName("리워드 목록 조회 성공 테스트")
	void getRewards() throws Exception {
		//given
		List<RewardsResponse> responses = List.of(
			RewardsResponse.builder()
				.rewardId(1L)
				.rewardType(RewardType.VIDEO)
				.rewardPoint(100)
				.isCanceled(false)
				.createdDate(LocalDateTime.now())
				.modifiedDate(LocalDateTime.now())
				.build(),
			RewardsResponse.builder()
				.rewardId(2L)
				.rewardType(RewardType.QUIZ)
				.rewardPoint(200)
				.isCanceled(true)
				.createdDate(LocalDateTime.now())
				.modifiedDate(LocalDateTime.now())
				.build(),
			RewardsResponse.builder()
				.rewardId(3L)
				.rewardType(RewardType.REPLY)
				.rewardPoint(300)
				.isCanceled(false)
				.createdDate(LocalDateTime.now())
				.modifiedDate(LocalDateTime.now())
				.build(),
			RewardsResponse.builder()
				.rewardId(4L)
				.rewardType(RewardType.VIDEO)
				.rewardPoint(400)
				.isCanceled(false)
				.createdDate(LocalDateTime.now())
				.modifiedDate(LocalDateTime.now())
				.build(),
			RewardsResponse.builder()
				.rewardId(5L)
				.rewardType(RewardType.REPLY)
				.rewardPoint(500)
				.isCanceled(true)
				.createdDate(LocalDateTime.now())
				.modifiedDate(LocalDateTime.now())
				.build(),
			RewardsResponse.builder()
				.rewardId(6L)
				.rewardType(RewardType.QUIZ)
				.rewardPoint(600)
				.isCanceled(false)
				.createdDate(LocalDateTime.now())
				.modifiedDate(LocalDateTime.now())
				.build(),
			RewardsResponse.builder()
				.rewardId(7L)
				.rewardType(RewardType.QUIZ)
				.rewardPoint(700)
				.isCanceled(true)
				.createdDate(LocalDateTime.now())
				.modifiedDate(LocalDateTime.now())
				.build(),
			RewardsResponse.builder()
				.rewardId(8L)
				.rewardType(RewardType.VIDEO)
				.rewardPoint(800)
				.isCanceled(false)
				.createdDate(LocalDateTime.now())
				.modifiedDate(LocalDateTime.now())
				.build(),
			RewardsResponse.builder()
				.rewardId(9L)
				.rewardType(RewardType.VIDEO)
				.rewardPoint(900)
				.isCanceled(false)
				.createdDate(LocalDateTime.now())
				.modifiedDate(LocalDateTime.now())
				.build(),
			RewardsResponse.builder()
				.rewardId(10L)
				.rewardType(RewardType.REPLY)
				.rewardPoint(1000)
				.isCanceled(true)
				.createdDate(LocalDateTime.now())
				.modifiedDate(LocalDateTime.now())
				.build()

		);

		PageImpl<RewardsResponse> page = new PageImpl<>(responses);

		given(memberService.getRewards(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).willReturn(page);

		//when
		ResultActions actions = mockMvc.perform(
			get("/members/rewards")
				.header(AUTHORIZATION, TOKEN)
				.param("page","1")
				.param("size","16")
				.accept(APPLICATION_JSON)
		);

		//then
		RestDocsUtil.assertPageResponse(actions, responses.size());

		actions
			.andDo(
				documentHandler.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("액세스 토큰")
					),
					requestParameters(
						parameterWithName("page").description("조회할 리워드 목록 페이지").optional(),
						parameterWithName("size").description("페이지의 데이터 수").optional()
					),
					pageResponseFields(
						fieldWithPath("data[]").description("리워드 목록"),
						fieldWithPath("data[].rewardId").description("리워드 ID"),
						fieldWithPath("data[].rewardType").description("리워드 획득 타입"),
						fieldWithPath("data[].rewardPoint").description("지급된 리워드 포인트"),
						fieldWithPath("data[].canceled").description("리워드 취소 여부"),
						fieldWithPath("data[].createdDate").description("리워드 지급일"),
						fieldWithPath("data[].modifiedDate").description("리워드 변경일")
					)
				)
			);
	}

	@Test
	@DisplayName("구독 목록 조회 성공 테스트")
	void getSubscribes() throws Exception {
		//given
		List<SubscribesResponse> responses = List.of(
			SubscribesResponse.builder()
				.memberId(23L)
				.channelName("vlog channel")
				.subscribes(1004)
				.imageUrl("https://d2ouhv9pc4idoe.cloudfront.net/images/test")
				.build(),
			SubscribesResponse.builder()
				.memberId(8136L)
				.channelName("study channel")
				.subscribes(486)
				.imageUrl("https://d2ouhv9pc4idoe.cloudfront.net/images/test")
				.build(),
			SubscribesResponse.builder()
				.memberId(931L)
				.channelName("music channel")
				.subscribes(333)
				.imageUrl("https://d2ouhv9pc4idoe.cloudfront.net/images/test")
				.build(),
			SubscribesResponse.builder()
				.memberId(49L)
				.channelName("game channel")
				.subscribes(777)
				.imageUrl("https://d2ouhv9pc4idoe.cloudfront.net/images/test")
				.build()
		);

		PageImpl<SubscribesResponse> page = new PageImpl<>(responses);

		given(memberService.getSubscribes(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).willReturn(page);

		//when
		ResultActions actions = mockMvc.perform(
			get("/members/subscribes")
				.header(AUTHORIZATION, TOKEN)
				.param("page","1")
				.param("size","16")
				.accept(APPLICATION_JSON)
		);

		RestDocsUtil.assertPageResponse(actions, responses.size());

		FieldDescriptor[] responseFields = new FieldDescriptor[]{
			fieldWithPath("data[]").description("구독 목록"),
			fieldWithPath("data[].memberId").description("구독한 채널의 ID"),
			fieldWithPath("data[].channelName").description("구독한 채널명"),
			fieldWithPath("data[].subscribes").description("채널의 구독자 수"),
			fieldWithPath("data[].imageUrl").description("채널 소유자의 프로필 이미지"),
		};

		actions
			.andDo(
				documentHandler.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("액세스 토큰")
					),
					requestParameters(
						parameterWithName("page").description("조회할 구독 목록 페이지").optional(),
						parameterWithName("size").description("페이지의 데이터 수").optional()
					),
					responseFields(
						RestDocsUtil.getPageResponseFields(responseFields)
					)
				)
			);
	}

	@Test
	@DisplayName("장바구니(찜 목록) 조회 성공 테스트")
	void getCarts() throws Exception {
		//given
		List<CartsResponse> responses = List.of(
			CartsResponse.builder()
				.videoId(151L)
				.videoName("리눅스 만드는 법")
				.thumbnailUrl("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
				.views(333)
				.createdDate(LocalDateTime.now())
				.price(100000)
				.videoCategories(List.of(
					CategoryResponse.builder()
						.categoryId(1L)
						.categoryName("Java")
						.build()
				))
				.channel(CartsResponse.Channel.builder()
					.memberId(3L)
					.channelName("Linus Torvalds")
					.subscribes(8391)
					.imageUrl("https://d2ouhv9pc4idoe.cloudfront.net/images/test")
					.build())
				.build(),
			CartsResponse.builder()
				.videoId(9514L)
				.videoName("컴활 강의")
				.thumbnailUrl("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
				.views(777)
				.createdDate(LocalDateTime.now())
				.price(70000)
				.videoCategories(List.of(
					CategoryResponse.builder()
						.categoryId(2L)
						.categoryName("JS")
						.build()
				))
				.channel(CartsResponse.Channel.builder()
					.memberId(361L)
					.channelName("Bill Gates")
					.subscribes(9999)
					.imageUrl("https://d2ouhv9pc4idoe.cloudfront.net/images/test")
					.build())
				.build()
		);

		PageImpl<CartsResponse> page = new PageImpl<>(responses);

		given(memberService.getCarts(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).willReturn(page);

		//when
		ResultActions actions = mockMvc.perform(
			get("/members/carts")
				.header(AUTHORIZATION, TOKEN)
				.param("page","1")
				.param("size","20")
				.accept(APPLICATION_JSON)
		);

		//then
		RestDocsUtil.assertPageResponse(actions, responses.size());

		FieldDescriptor[] responseFields = new FieldDescriptor[]{
			fieldWithPath("data[]").description("장바구니 목록"),
			fieldWithPath("data[].videoId").description("장바구니에 담은 영상 ID"),
			fieldWithPath("data[].videoName").description("장바구니에 담은 영상명"),
			fieldWithPath("data[].thumbnailUrl").description("영상의 썸네일 이미지 주소"),
			fieldWithPath("data[].views").description("영상 조회수"),
			fieldWithPath("data[].createdDate").description("영상 업로드 날짜"),
			fieldWithPath("data[].price").description("영상의 가격"),
			fieldWithPath("data[].videoCategories").description("영상의 카테고리 목록"),
			fieldWithPath("data[].videoCategories[].categoryId").description("영상의 카테고리 아이디"),
			fieldWithPath("data[].videoCategories[].categoryName").description("영상의 카테고리 이름"),
			fieldWithPath("data[].channel").description("영상 업로더의 채널 정보"),
			fieldWithPath("data[].channel.memberId").description("업로더 아이디"),
			fieldWithPath("data[].channel.channelName").description("업로더의 채널명"),
			fieldWithPath("data[].channel.subscribes").description("업로더의 구독자 수"),
			fieldWithPath("data[].channel.imageUrl").description("업로더의 프로필 이미지 주소")
		};

		actions
			.andDo(
				documentHandler.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("액세스 토큰")
					),
					requestParameters(
						parameterWithName("page").description("조회할 장바구니 페이지").optional(),
						parameterWithName("size").description("페이지의 데이터 수").optional()
					),
					responseFields(
						RestDocsUtil.getPageResponseFields(responseFields)
					)
				)
			);
	}

	@Test
	@DisplayName("결제 목록 조회 성공 테스트")
	void getOrders() throws Exception {
		//given
		List<OrdersResponse> responses = List.of(
			OrdersResponse.builder()
				.orderId("aBzd031dpf414")
				.amount(30000)
				.orderCount(4)
				.orderStatus(OrderStatus.ORDERED)
				.createdDate(LocalDateTime.now())
				.completedDate(LocalDateTime.now())
				.orderVideos(
					List.of(
						OrdersResponse.OrderVideo.builder()
							.videoId(1L)
							.videoName("구매한 영상명1")
							.thumbnailFile("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
							.channelName("영상 업로드 채널")
							.price(10000)
							.build(),
						OrdersResponse.OrderVideo.builder()
							.videoId(1L)
							.videoName("구매한 영상명2")
							.thumbnailFile("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
							.channelName("영상 업로드 채널")
							.price(20000)
							.build()
					)
				)
				.build(),
			OrdersResponse.builder()
				.orderId("dfghkdf908sd023")
				.amount(40000)
				.orderCount(6)
				.orderStatus(OrderStatus.CANCELED)
				.createdDate(LocalDateTime.now())
				.completedDate(LocalDateTime.now())
				.orderVideos(
					List.of(
						OrdersResponse.OrderVideo.builder()
							.videoId(1L)
							.videoName("구매한 영상명1")
							.thumbnailFile("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
							.channelName("영상 업로드 채널")
							.price(10000)
							.build(),
						OrdersResponse.OrderVideo.builder()
							.videoId(1L)
							.videoName("구매한 영상명2")
							.thumbnailFile("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
							.channelName("영상 업로드 채널")
							.price(20000)
							.build()
					)
				)
				.build(),
			OrdersResponse.builder()
				.orderId("fd932jkfdgklgdf")
				.amount(20000)
				.orderCount(3)
				.orderStatus(OrderStatus.COMPLETED)
				.createdDate(LocalDateTime.now())
				.completedDate(LocalDateTime.now())
				.orderVideos(
					List.of(
						OrdersResponse.OrderVideo.builder()
							.videoId(1L)
							.videoName("구매한 영상명1")
							.thumbnailFile("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
							.channelName("영상 업로드 채널")
							.price(10000)
							.build(),
						OrdersResponse.OrderVideo.builder()
							.videoId(1L)
							.videoName("구매한 영상명2")
							.thumbnailFile("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
							.channelName("영상 업로드 채널")
							.price(20000)
							.build()
					)
				)
				.build(),
			OrdersResponse.builder()
				.orderId("nvbio328sdfhs13")
				.amount(10000)
				.orderCount(7)
				.orderStatus(OrderStatus.ORDERED)
				.createdDate(LocalDateTime.now())
				.completedDate(LocalDateTime.now())
				.orderVideos(
					List.of(
						OrdersResponse.OrderVideo.builder()
							.videoId(1L)
							.videoName("구매한 영상명1")
							.thumbnailFile("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
							.channelName("영상 업로드 채널")
							.price(10000)
							.build(),
						OrdersResponse.OrderVideo.builder()
							.videoId(1L)
							.videoName("구매한 영상명2")
							.thumbnailFile("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
							.channelName("영상 업로드 채널")
							.price(20000)
							.build()
					)
				)
				.build()
		);

		PageImpl<OrdersResponse> page = new PageImpl<>(responses);

		given(memberService.getOrders(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).willReturn(page);

		//when
		ResultActions actions = mockMvc.perform(
			get("/members/orders")
				.header(AUTHORIZATION, TOKEN)
				.param("page", "1")
				.param("size","4")
				.param("month", "1")
				.accept(APPLICATION_JSON)
		);

		//then
		RestDocsUtil.assertPageResponse(actions, responses.size());

		FieldDescriptor[] responseFields = new FieldDescriptor[]{
			fieldWithPath("data[]").description("결제 목록"),
			fieldWithPath("data[].orderId").description("결제 번호"),
			fieldWithPath("data[].amount").description("총 결제 금액(실제 가격 - 리워드 사용량)"),
			fieldWithPath("data[].orderCount").description("결제한 강의 수"),
			fieldWithPath("data[].orderStatus").description("주문 상태"),
			fieldWithPath("data[].createdDate").description("결제일"),
			fieldWithPath("data[].completedDate").description("결제완료일"),
			fieldWithPath("data[].orderVideos[]").description("결제한 강의 목록"),
			fieldWithPath("data[].orderVideos[].videoId").description("강의 ID"),
			fieldWithPath("data[].orderVideos[].videoName").description("강의명"),
			fieldWithPath("data[].orderVideos[].thumbnailFile").description("강의 썸네일 이미지 주소"),
			fieldWithPath("data[].orderVideos[].channelName").description("강의 업로더 채널명"),
			fieldWithPath("data[].orderVideos[].price").description("강의 가격")
		};

		actions
			.andDo(
				documentHandler
					.document(
						requestHeaders(
							headerWithName(AUTHORIZATION).description("액세스 토큰")
						),
						requestParameters(
							parameterWithName("page").description("조회할 결제 목록 페이지").optional(),
							parameterWithName("size").description("페이지의 데이터 수").optional(),
							parameterWithName("month").description("조회할 범위 지정(월 단위)").optional()
						),
						responseFields(
							RestDocsUtil.getPageResponseFields(responseFields)
						)
					)
			);
	}

	@Test
	@DisplayName("구매한 강의 보관함 조회 성공 테스트")
	void getPlaylists() throws Exception {
		//given
		List<PlaylistsResponse> responses = List.of(
			PlaylistsResponse.builder()
				.videoId(321L)
				.videoName("가볍게 배우는 알고리즘")
				.thumbnailUrl("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
				.star(4.7f)
				.createdDate(LocalDateTime.now())
				.modifiedDate(LocalDateTime.now())
				.channel(
					PlaylistsResponse.Channel.builder()
						.memberId(23L)
						.channelName("알고리즘 채널")
						.imageUrl("https://d2ouhv9pc4idoe.cloudfront.net/777/test")
						.build()
				)
				.build(),
			PlaylistsResponse.builder()
				.videoId(2218L)
				.videoName("더 가볍게 배우는 알고리즘")
				.thumbnailUrl("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
				.star(3.4f)
				.createdDate(LocalDateTime.now())
				.modifiedDate(LocalDateTime.now())
				.channel(
					PlaylistsResponse.Channel.builder()
						.memberId(23L)
						.channelName("알고리즘 채널")
						.imageUrl("https://d2ouhv9pc4idoe.cloudfront.net/777/test")
						.build()
				)
				.build(),
			PlaylistsResponse.builder()
				.videoId(7831L)
				.videoName("많이 가볍게 배우는 알고리즘")
				.thumbnailUrl("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
				.star(2.9f)
				.createdDate(LocalDateTime.now())
				.modifiedDate(LocalDateTime.now())
				.channel(
					PlaylistsResponse.Channel.builder()
						.memberId(23L)
						.channelName("알고리즘 채널")
						.imageUrl("https://d2ouhv9pc4idoe.cloudfront.net/777/test")
						.build()
				)
				.build(),
			PlaylistsResponse.builder()
				.videoId(321L)
				.videoName("진짜 가볍게 배우는 알고리즘")
				.thumbnailUrl("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
				.star(1.8f)
				.createdDate(LocalDateTime.now())
				.modifiedDate(LocalDateTime.now())
				.channel(
					PlaylistsResponse.Channel.builder()
						.memberId(23L)
						.channelName("알고리즘 채널")
						.imageUrl("https://d2ouhv9pc4idoe.cloudfront.net/777/test")
						.build()
				)
				.build()
		);

		PageImpl<PlaylistsResponse> page = new PageImpl<>(responses);

		given(memberService.getPlaylists(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString())).willReturn(page);

		//when
		ResultActions actions = mockMvc.perform(
			get("/members/playlists")
				.header(AUTHORIZATION, TOKEN)
				.param("page",  "1")
				.param("size", "16")
				.param("sort", "star")
				.accept(APPLICATION_JSON)
		);

		RestDocsUtil.assertPageResponse(actions, responses.size());

		FieldDescriptor[] responseFields = new FieldDescriptor[]{
			fieldWithPath("data[]").description("구매한 강의 목록"),
			fieldWithPath("data[].videoId").description("구매한 영상 ID"),
			fieldWithPath("data[].videoName").description("구매한 영상명"),
			fieldWithPath("data[].thumbnailUrl").description("영상의 썸네일 이미지 주소"),
			fieldWithPath("data[].star").description("영상 평균 별점"),
			fieldWithPath("data[].createdDate").description("영상 업로드 날짜"),
			fieldWithPath("data[].modifiedDate").description("영상 업데이트 날짜"),
			fieldWithPath("data[].channel").description("영상 업로더의 채널 정보"),
			fieldWithPath("data[].channel.memberId").description("업로더의 아이디"),
			fieldWithPath("data[].channel.channelName").description("업로더의 채널명"),
			fieldWithPath("data[].channel.imageUrl").description("채널을 소유한 회원의 프로필 이미지 주소")
		};

		actions
			.andDo(
				documentHandler
					.document(
						requestHeaders(
							headerWithName(AUTHORIZATION).description("액세스 토큰")
						),
						requestParameters(
							parameterWithName("page").description("구매한 강의 목록 페이지").optional(),
							parameterWithName("size").description("페이지의 데이터 수").optional(),
							parameterWithName("sort").description("정렬 기준(created-date, star, channel, name)").optional()
						),
						responseFields(
							RestDocsUtil.getPageResponseFields(responseFields)
						)
					)
			);
	}

	@Test
	@DisplayName("플레이리스트 채널별 필터링의 채널별 구매한 영상 조회 성공 테스트")
	void getPlaylistChannelDetails() throws Exception {
		//given
		List<PlaylistChannelDetailsResponse> responses = List.of(
			PlaylistChannelDetailsResponse.builder()
				.videoId(837L)
				.videoName("자바스크립트 강의")
				.description("착한 사람 눈에만 보이는 영상 설명")
				.thumbnailUrl("www.thumbnailImageUrl.com")
				.view(109)
				.star(8.3F)
				.build(),
			PlaylistChannelDetailsResponse.builder()
				.videoId(109L)
				.videoName("깃허브 강의")
				.description("착한 사람 눈에만 보이는 영상 설명")
				.thumbnailUrl("www.thumbnailImageUrl.com")
				.view(2048)
				.star(9.1F)
				.build(),
			PlaylistChannelDetailsResponse.builder()
				.videoId(390L)
				.videoName("알고리즘 강의")
				.description("착한 사람 눈에만 보이는 영상 설명")
				.thumbnailUrl("www.thumbnailImageUrl.com")
				.view(93)
				.star(3.9F)
				.build(),
			PlaylistChannelDetailsResponse.builder()
				.videoId(683L)
				.videoName("자바 강의")
				.description("착한 사람 눈에만 보이는 영상 설명")
				.thumbnailUrl("www.thumbnailImageUrl.com")
				.view(386)
				.star(6.8F)
				.build(),
			PlaylistChannelDetailsResponse.builder()
				.videoId(731L)
				.videoName("스프링 강의")
				.description("착한 사람 눈에만 보이는 영상 설명")
				.thumbnailUrl("www.thumbnailImageUrl.com")
				.view(137)
				.star(7.3F)
				.build(),
			PlaylistChannelDetailsResponse.builder()
				.videoId(591L)
				.videoName("리액트 강의")
				.description("착한 사람 눈에만 보이는 영상 설명")
				.thumbnailUrl("www.thumbnailImageUrl.com")
				.view(159)
				.star(5.9F)
				.build(),
			PlaylistChannelDetailsResponse.builder()
				.videoId(668L)
				.videoName("테스트 하는 법")
				.description("착한 사람 눈에만 보이는 영상 설명")
				.thumbnailUrl("www.thumbnailImageUrl.com")
				.view(866)
				.star(6.6F)
				.build(),
			PlaylistChannelDetailsResponse.builder()
				.videoId(749L)
				.videoName("파이썬 강의")
				.description("착한 사람 눈에만 보이는 영상 설명")
				.thumbnailUrl("www.thumbnailImageUrl.com")
				.view(947)
				.star(7.4F)
				.build(),
			PlaylistChannelDetailsResponse.builder()
				.videoId(333L)
				.videoName("C++ 강의")
				.description("착한 사람 눈에만 보이는 영상 설명")
				.thumbnailUrl("www.thumbnailImageUrl.com")
				.view(333)
				.star(3.3F)
				.build(),
			PlaylistChannelDetailsResponse.builder()
				.videoId(777L)
				.videoName("네트워크 기본 강의")
				.description("착한 사람 눈에만 보이는 영상 설명")
				.thumbnailUrl("www.thumbnailImageUrl.com")
				.view(777)
				.star(7.7F)
				.build()
		);

		PageImpl<PlaylistChannelDetailsResponse> page = new PageImpl<>(responses);

		given(memberService.getChannelDetailsForPlaylist(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).willReturn(page);

		//when
		ResultActions actions = mockMvc.perform(
			get("/members/playlists/channels/details")
				.header(AUTHORIZATION, TOKEN)
				.param("page", "1")
				.param("size", "10")
				.param("member-id","1")
				.accept(APPLICATION_JSON)
		);

		//then
		RestDocsUtil.assertPageResponse(actions, responses.size());

		actions
			.andDo(
				documentHandler.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("액세스 토큰")
					),
					requestParameters(
						parameterWithName("page").description("조회할 페이지 수"),
						parameterWithName("size").description("조회할 페이지의 데이터 수"),
						parameterWithName("member-id").description("조회할 채널 소유자의 회원 ID")
					),
					pageResponseFields(
						fieldWithPath("data[]").description("특정 채널에서 구매한 강의 목록"),
						fieldWithPath("data[].videoId").description("강의 ID"),
						fieldWithPath("data[].videoName").description("강의명"),
						fieldWithPath("data[].description").description("강의에 대한 설명"),
						fieldWithPath("data[].thumbnailUrl").description("강의의 썸네일 이미지 주소"),
						fieldWithPath("data[].view").description("강의의 조회 수"),
						fieldWithPath("data[].star").description("강의의 평점"),
						fieldWithPath("data[].createdDate").description("영상 업로드 날짜")
					)
				)
			);
	}

	@Test
	@DisplayName("플레이리스트 채널별 필터링의 채널 목록 조회 성공 테스트")
	void getPlaylistChannels() throws Exception {
		//given
		List<PlaylistChannelResponse> responses = List.of(
			PlaylistChannelResponse.builder()
				.memberId(1L)
				.channelName("채널명1")
				.imageUrl("www.profileImageUrl.com")
				.videoCount(4L)
				.subscribers(10)
				.isSubscribed(true)
				.build(),
			PlaylistChannelResponse.builder()
				.memberId(2L)
				.channelName("채널명2")
				.imageUrl("www.profileImageUrl.com")
				.videoCount(7L)
				.subscribers(89)
				.isSubscribed(false)
				.build(),
			PlaylistChannelResponse.builder()
				.memberId(3L)
				.channelName("채널명3")
				.imageUrl("www.profileImageUrl.com")
				.videoCount(4L)
				.subscribers(107)
				.isSubscribed(false)
				.build(),
			PlaylistChannelResponse.builder()
				.memberId(4L)
				.channelName("채널명4")
				.imageUrl("www.profileImageUrl.com")
				.videoCount(11L)
				.subscribers(88)
				.isSubscribed(true)
				.build(),
			PlaylistChannelResponse.builder()
				.memberId(5L)
				.channelName("채널명5")
				.imageUrl("www.profileImageUrl.com")
				.videoCount(3L)
				.subscribers(27)
				.isSubscribed(false)
				.build(),
			PlaylistChannelResponse.builder()
				.memberId(6L)
				.channelName("채널명6")
				.imageUrl("www.profileImageUrl.com")
				.videoCount(7L)
				.subscribers(339)
				.isSubscribed(true)
				.build(),
			PlaylistChannelResponse.builder()
				.memberId(7L)
				.channelName("채널명7")
				.imageUrl("www.profileImageUrl.com")
				.videoCount(9L)
				.subscribers(201)
				.isSubscribed(true)
				.build(),
			PlaylistChannelResponse.builder()
				.memberId(8L)
				.channelName("채널명8")
				.imageUrl("www.profileImageUrl.com")
				.videoCount(7L)
				.subscribers(10415)
				.isSubscribed(true)
				.build(),
			PlaylistChannelResponse.builder()
				.memberId(9L)
				.channelName("채널명9")
				.imageUrl("www.profileImageUrl.com")
				.videoCount(8L)
				.subscribers(19)
				.isSubscribed(false)
				.build(),
			PlaylistChannelResponse.builder()
				.memberId(10L)
				.channelName("채널명10")
				.imageUrl("www.profileImageUrl.com")
				.videoCount(5L)
				.subscribers(8910)
				.isSubscribed(true)
				.build()
		);

		PageImpl<PlaylistChannelResponse> page = new PageImpl<>(responses);

		given(memberService.getChannelForPlaylist(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).willReturn(page);

		//when
		ResultActions actions = mockMvc.perform(
			get("/members/playlists/channels")
				.header(AUTHORIZATION, TOKEN)
				.param("page","1")
				.param("size","16")
				.accept(APPLICATION_JSON)
		);

		//then
		RestDocsUtil.assertPageResponse(actions, responses.size());

		actions
			.andDo(
				documentHandler.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("액세스 토큰")
					),
					requestParameters(
						parameterWithName("page").description("조회할 강의를 구매한 채널 목록 페이지").optional(),
						parameterWithName("size").description("페이지의 데이터 수").optional()
					),
					pageResponseFields(
						fieldWithPath("data[]").description("강의를 구매한 채널 목록"),
						fieldWithPath("data[].memberId").description("해당 채널의 회원 ID"),
						fieldWithPath("data[].channelName").description("채널명"),
						fieldWithPath("data[].imageUrl").description("해당 채널의 회원 프로필 이미지"),
						fieldWithPath("data[].videoCount").description("해당 채널에서 구매한 영상의 수"),
						fieldWithPath("data[].isSubscribed").description("해당 채널의 구독 여부"),
						fieldWithPath("data[].subscribers").description("해당 채널의 구독자 수"),
						fieldWithPath("data[].list").description("비디오를 담을 빈 배열")
					)
				)
			);
	}

	@Test
	@DisplayName("시청 기록 조회 성공 테스트")
	void getWatchs() throws Exception {
		//given
		List<WatchsResponse> responses = List.of(
			WatchsResponse.builder()
				.videoId(791L)
				.videoName("알고리즘")
				.thumbnailFile(awsService.getFileUrl("test22", FileType.PROFILE_IMAGE))
				.modifiedDate(LocalDateTime.now())
				.channel(WatchsResponse.Channel.builder()
					.memberId(4325L)
					.channelName("채널1")
					.imageUrl("www.profileImageUrl.com")
					.build())
				.build(),
			WatchsResponse.builder()
				.videoId(791L)
				.videoName("리액트")
				.thumbnailFile(awsService.getFileUrl( "test22", FileType.PROFILE_IMAGE))
				.modifiedDate(LocalDateTime.now())
				.channel(WatchsResponse.Channel.builder()
					.memberId(4325L)
					.channelName("채널2")
					.imageUrl("www.profileImageUrl.com")
					.build())
				.build(),
			WatchsResponse.builder()
				.videoId(791L)
				.videoName("스프링")
				.thumbnailFile(awsService.getFileUrl("test22", FileType.PROFILE_IMAGE))
				.modifiedDate(LocalDateTime.now())
				.channel(WatchsResponse.Channel.builder()
					.memberId(4325L)
					.channelName("채널3")
					.imageUrl("www.profileImageUrl.com")
					.build())
				.build(),
			WatchsResponse.builder()
				.videoId(791L)
				.videoName("자바")
				.thumbnailFile(awsService.getFileUrl( "test22", FileType.PROFILE_IMAGE))
				.modifiedDate(LocalDateTime.now())
				.channel(WatchsResponse.Channel.builder()
					.memberId(4325L)
					.channelName("채널3")
					.imageUrl("www.profileImageUrl.com")
					.build())
				.build()
		);

		PageImpl<WatchsResponse> page = new PageImpl<>(responses);

		given(memberService.getWatchs(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).willReturn(page);

		//when
		ResultActions actions = mockMvc.perform(
			get("/members/watchs")
				.header(AUTHORIZATION, TOKEN)
				.param("page", "1")
				.param("size","16")
				.param("day", "30")
		);

		//then
		RestDocsUtil.assertPageResponse(actions, responses.size());

		FieldDescriptor[] responseFields = new FieldDescriptor[]{
			fieldWithPath("data[]").description("시청 기록"),
			fieldWithPath("data[].videoId").description("시청한 영상 ID"),
			fieldWithPath("data[].videoName").description("시청한 영상명"),
			fieldWithPath("data[].thumbnailFile").description("영상의 썸네일 이미지 주소"),
			fieldWithPath("data[].modifiedDate").description("영상 시청일"),
			fieldWithPath("data[].channel").description("영상 업로더의 채널 정보"),
			fieldWithPath("data[].channel.memberId").description("업로더의 아이디"),
			fieldWithPath("data[].channel.channelName").description("업로더의 채널명"),
			fieldWithPath("data[].channel.imageUrl").description("업로더의 프로필 이미지 주소")
		};

		actions
			.andDo(
				documentHandler
					.document(
						requestHeaders(
							headerWithName(AUTHORIZATION).description("액세스 토큰")
						),
						requestParameters(
							parameterWithName("page").description("조회할 페이지").optional(),
							parameterWithName("size").description("페이지의 데이터 수").optional(),
							parameterWithName("day").description("조회 기간 설정").optional()
						),
						responseFields(
							RestDocsUtil.getPageResponseFields(responseFields)
						)
					)
			);
	}

	@Test
	@DisplayName("닉네임 변경 성공 테스트")
	void updateNickname() throws Exception {
		//given
		MemberApiRequest.Nickname nickname = new MemberApiRequest.Nickname("testnickname");

		String content = objectMapper.writeValueAsString(nickname);

		//when
		ResultActions actions = mockMvc.perform(
			patch("/members")
				.header(AUTHORIZATION, TOKEN)
				.contentType(APPLICATION_JSON)
				.content(content)
		);

		actions
			.andDo(print())
			.andExpect(status().isNoContent());

		setConstraintClass(MemberApiRequest.Nickname.class);

		actions
			.andDo(
				documentHandler
					.document(
						requestHeaders(
							headerWithName(AUTHORIZATION).description("액세스 토큰")
						),
						requestFields(
							fieldWithPath("nickname").description("변경할 닉네임").attributes(getConstraint("nickname"))
						)
					)
			);
	}

	@Test
	@DisplayName("프로필 이미지 변경 성공 테스트")
	void updateImage() throws Exception {
		//given
		String imageName = "profile20230907140835";
		MemberApiRequest.Image request = new MemberApiRequest.Image(
			imageName, ImageType.JPG
		);

		String content = objectMapper.writeValueAsString(request);

		String presignedUrl = "http://www.uploadUrl.com";

		given(awsService.getImageUploadUrl(anyLong(), anyString(), any(FileType.class), any(ImageType.class)))
			.willReturn(presignedUrl);

		//when
		ResultActions actions = mockMvc.perform(
			patch("/members/image")
				.header(AUTHORIZATION, TOKEN)
				.contentType(APPLICATION_JSON)
				.content(content)
		);

		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(header().string(HttpHeaders.LOCATION, presignedUrl));

		setConstraintClass(MemberApiRequest.Image.class);

		actions
			.andDo(
				documentHandler
					.document(
						requestHeaders(
							headerWithName(AUTHORIZATION).description("액세스 토큰")
						),
						requestFields(
							fieldWithPath("imageName").description("이미지 확장자"),
							fieldWithPath("imageType").description("이미지 확장자").attributes(getConstraint("imageType"))
						),
						responseHeaders(
							headerWithName(LOCATION).description("이미지 업로드 URL")
						)
					)
			);
	}

	@Test
	@DisplayName("사용자 패스워드 변경 성공 테스트")
	void updatePassword() throws Exception {
		MemberApiRequest.Password request = new MemberApiRequest.Password(
			"abcde12345!", "12345abcde!"
		);

		String content = objectMapper.writeValueAsString(request);

		ResultActions actions = mockMvc.perform(
			patch("/members/password")
				.header(AUTHORIZATION, TOKEN)
				.contentType(APPLICATION_JSON)
				.content(content)
		);

		actions
			.andDo(print())
			.andExpect(status().isNoContent());

		setConstraintClass(MemberApiRequest.Password.class);

		actions
			.andDo(
				documentHandler
					.document(
						requestHeaders(
							headerWithName(AUTHORIZATION).description("액세스 토큰")
						),
						requestFields(
							fieldWithPath("prevPassword").description("이전 패스워드").attributes(getConstraint("prevPassword")),
							fieldWithPath("newPassword").description("변경할 패스워드").attributes(getConstraint("newPassword"))
						)
					)
			);
	}

	@Test
	@DisplayName("회원 탈퇴 성공 테스트")
	void deleteMember() throws Exception {
		ResultActions actions = mockMvc.perform(
			delete("/members")
				.header(AUTHORIZATION, TOKEN)
		);

		actions
			.andDo(print())
			.andExpect(status().isNoContent());

		actions
			.andDo(
				documentHandler
					.document(
						requestHeaders(
							headerWithName(AUTHORIZATION).description("액세스 토큰")
						)
					)
			);
	}

	@Test
	@DisplayName("프로필 이미지 삭제 성공 테스트")
	void deleteImage() throws Exception {
		ResultActions actions = mockMvc.perform(
			delete("/members/image")
				.header(AUTHORIZATION, TOKEN)
		);

		actions
			.andDo(print())
			.andExpect(status().isNoContent());

		actions
			.andDo(
				documentHandler
					.document(
						requestHeaders(
							headerWithName(AUTHORIZATION).description("액세스 토큰")
						)
					)
			);
	}

	@TestFactory
	@DisplayName("리워드 목록 조회 Validation 테스트")
	Collection<DynamicTest> getRewardsValidation() throws Exception {
		//given
		List<RewardsResponse> responses = List.of(RewardsResponse.builder().build());

		PageImpl<RewardsResponse> pages = new PageImpl<>(responses);

		String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(pages));

		given(memberService.getRewards(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).willReturn(pages);

		return List.of(
			DynamicTest.dynamicTest(
				"페이지와 사이즈 파라미터 값을 보내지 않아도 기본값으로 조회가 된다",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/rewards")
							.header(AUTHORIZATION, TOKEN)
							.accept(APPLICATION_JSON)
					);

					//then
					actions
						.andDo(print())
						.andExpect(status().isOk())
						.andExpect(content().string(apiResponse));
				}
			),
			DynamicTest.dynamicTest(
				"페이지가 양수가 아닌 경우",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/rewards")
							.header(AUTHORIZATION, TOKEN)
							.param("page", "0")
							.accept(APPLICATION_JSON)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("page"))
						.andExpect(jsonPath("$.data[0].value").value(0))
						.andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
				}
			),
			DynamicTest.dynamicTest(
				"사이즈가 양수가 아닌 경우",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/rewards")
							.header(AUTHORIZATION, TOKEN)
							.param("size", "0")
							.accept(APPLICATION_JSON)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("size"))
						.andExpect(jsonPath("$.data[0].value").value(0))
						.andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
				}
			)
		);
	}

	@TestFactory
	@DisplayName("구독 목록 조회 Validation 테스트")
	Collection<DynamicTest> getSubscribesValidation() throws Exception {
		//given
		List<SubscribesResponse> responses = List.of(
			SubscribesResponse.builder()
				.memberId(23L)
				.channelName("vlog channel")
				.subscribes(1004)
				.imageUrl("https://d2ouhv9pc4idoe.cloudfront.net/images/test")
				.build()
		);

		PageImpl<SubscribesResponse> page = new PageImpl<>(responses);

		String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(page));

		given(memberService.getSubscribes(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).willReturn(page);


		return List.of(
			DynamicTest.dynamicTest(
				"페이지와 사이즈 파라미터 값을 보내지 않아도 기본값으로 조회가 된다",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/subscribes")
							.header(AUTHORIZATION, TOKEN)
							.accept(APPLICATION_JSON)
					);

					//then
					actions
						.andDo(print())
						.andExpect(status().isOk())
						.andExpect(content().string(apiResponse));
				}
			),
			DynamicTest.dynamicTest(
				"페이지가 양수가 아닌 경우",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/subscribes")
							.header(AUTHORIZATION, TOKEN)
							.param("page", "0")
							.accept(APPLICATION_JSON)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("page"))
						.andExpect(jsonPath("$.data[0].value").value(0))
						.andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
				}
			),
			DynamicTest.dynamicTest(
				"사이즈가 양수가 아닌 경우",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/subscribes")
							.header(AUTHORIZATION, TOKEN)
							.param("size", "0")
							.accept(APPLICATION_JSON)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("size"))
						.andExpect(jsonPath("$.data[0].value").value(0))
						.andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
				}
			)
		);
	}

	@TestFactory
	@DisplayName("장바구니 목록 조회 Validation 테스트")
	Collection<DynamicTest> getCartsValidation() throws Exception {
		//given
		List<CartsResponse> responses = List.of(
			CartsResponse.builder()
				.videoId(151L)
				.videoName("리눅스 만드는 법")
				.thumbnailUrl("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
				.views(333)
				.createdDate(LocalDateTime.now())
				.price(100000)
				.channel(CartsResponse.Channel.builder()
					.memberId(3L)
					.channelName("Linus Torvalds")
					.subscribes(8391)
					.imageUrl("https://d2ouhv9pc4idoe.cloudfront.net/images/test")
					.build())
				.build()
		);

		PageImpl<CartsResponse> page = new PageImpl<>(responses);

		String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(page));

		given(memberService.getCarts(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).willReturn(page);

		return List.of(
			DynamicTest.dynamicTest(
				"페이지와 사이즈 파라미터 값을 보내지 않아도 기본값으로 조회가 된다",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/carts")
							.header(AUTHORIZATION, TOKEN)
							.accept(APPLICATION_JSON)
					);

					//then
					actions
						.andDo(print())
						.andExpect(status().isOk())
						.andExpect(content().string(apiResponse));
				}
			),
			DynamicTest.dynamicTest(
				"페이지가 양수가 아닌 경우",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/carts")
							.header(AUTHORIZATION, TOKEN)
							.param("page", "0")
							.accept(APPLICATION_JSON)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("page"))
						.andExpect(jsonPath("$.data[0].value").value(0))
						.andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
				}
			),
			DynamicTest.dynamicTest(
				"사이즈가 양수가 아닌 경우",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/carts")
							.header(AUTHORIZATION, TOKEN)
							.param("size", "0")
							.accept(APPLICATION_JSON)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("size"))
						.andExpect(jsonPath("$.data[0].value").value(0))
						.andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
				}
			)
		);
	}

	@TestFactory
	@DisplayName("주문 목록 조회 Validation 테스트")
	Collection<DynamicTest> getOrdersValidation() throws Exception {
		//given
		List<OrdersResponse> responses = List.of(
			OrdersResponse.builder()
				.orderId("aBzd031dpf414")
				.amount(300)
				.orderCount(4)
				.orderStatus(OrderStatus.ORDERED)
				.createdDate(LocalDateTime.now())
				.orderVideos(
					List.of(
						OrdersResponse.OrderVideo.builder()
							.videoId(1L)
							.videoName("구매한 영상명1")
							.thumbnailFile("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
							.channelName("영상 업로드 채널")
							.price(10000)
							.build()
					)
				)
				.build()
		);

		PageImpl<OrdersResponse> page = new PageImpl<>(responses);

		String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(page));

		given(memberService.getOrders(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).willReturn(page);

		return List.of(
			DynamicTest.dynamicTest(
				"페이지와 사이즈, 조회할 기간(월) 파라미터 값을 보내지 않아도 기본값으로 조회가 된다",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/orders")
							.header(AUTHORIZATION, TOKEN)
							.accept(APPLICATION_JSON)
					);

					//then
					actions
						.andDo(print())
						.andExpect(status().isOk())
						.andExpect(content().string(apiResponse));
				}
			),
			DynamicTest.dynamicTest(
				"페이지가 양수가 아닌 경우",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/orders")
							.header(AUTHORIZATION, TOKEN)
							.param("page", "0")
							.accept(APPLICATION_JSON)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("page"))
						.andExpect(jsonPath("$.data[0].value").value(0))
						.andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
				}
			),
			DynamicTest.dynamicTest(
				"사이즈가 양수가 아닌 경우",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/orders")
							.header(AUTHORIZATION, TOKEN)
							.param("size", "0")
							.accept(APPLICATION_JSON)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("size"))
						.andExpect(jsonPath("$.data[0].value").value(0))
						.andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
				}
			),
			DynamicTest.dynamicTest(
				"조회할 기간(월)이 양수가 아닌 경우",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/orders")
							.header(AUTHORIZATION, TOKEN)
							.param("month", "0")
							.accept(APPLICATION_JSON)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("month"))
						.andExpect(jsonPath("$.data[0].value").value(0))
						.andExpect(jsonPath("$.data[0].reason").value("최소 1개월 이상 부터 조회 가능합니다."));
				}
			),
			DynamicTest.dynamicTest(
				"조회할 기간(월)이 12개월 초과인 경우",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/orders")
							.header(AUTHORIZATION, TOKEN)
							.param("month", "13")
							.accept(APPLICATION_JSON)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("month"))
						.andExpect(jsonPath("$.data[0].value").value(13))
						.andExpect(jsonPath("$.data[0].reason").value("최대 12개월 까지 조회 가능합니다."));
				}
			)
		);
	}

	@TestFactory
	@DisplayName("구매한 강의 보관함 조회 Validation 테스트")
	Collection<DynamicTest> getPlaylistsValidation() throws Exception {
		//given
		List<PlaylistsResponse> responses = List.of(
			PlaylistsResponse.builder()
				.videoId(321L)
				.videoName("가볍게 배우는 알고리즘")
				.thumbnailUrl("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
				.star(4.7f)
				.modifiedDate(LocalDateTime.now())
				.channel(
					PlaylistsResponse.Channel.builder()
						.memberId(23L)
						.channelName("알고리즘 채널")
						.imageUrl("https://d2ouhv9pc4idoe.cloudfront.net/777/test")
						.build()
				)
				.build()
		);

		PageImpl<PlaylistsResponse> page = new PageImpl<>(responses);

		String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(page));

		given(memberService.getPlaylists(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString())).willReturn(page);

		return List.of(
			DynamicTest.dynamicTest(
				"페이지와 사이즈 파라미터 값을 보내지 않아도 기본값으로 조회가 된다",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/playlists")
							.header(AUTHORIZATION, TOKEN)
							.accept(APPLICATION_JSON)
					);

					//then
					actions
						.andDo(print())
						.andExpect(status().isOk())
						.andExpect(content().string(apiResponse));
				}
			),
			DynamicTest.dynamicTest(
				"페이지가 양수가 아닌 경우",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/playlists")
							.header(AUTHORIZATION, TOKEN)
							.param("page", "0")
							.accept(APPLICATION_JSON)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("page"))
						.andExpect(jsonPath("$.data[0].value").value(0))
						.andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
				}
			),
			DynamicTest.dynamicTest(
				"사이즈가 양수가 아닌 경우",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/playlists")
							.header(AUTHORIZATION, TOKEN)
							.param("size", "0")
							.accept(APPLICATION_JSON)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("size"))
						.andExpect(jsonPath("$.data[0].value").value(0))
						.andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
				}
			),
			DynamicTest.dynamicTest(
				"정렬 조건에 정확한 값이 들어오지 않은 경우",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/playlists")
							.header(AUTHORIZATION, TOKEN)
							.param("sort", "안되는 enum 값")
							.accept(APPLICATION_JSON)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.message").value("요청 값의 타입이 잘못되었습니다. 잘못된 값 : 안되는 enum 값"));
				}
			)
		);
	}

	@TestFactory
	@DisplayName("구매한 강의 보관함 조회(채널별) Validation 테스트")
	Collection<DynamicTest> getPlaylistChannelsValidation() throws Exception {
		//given
		List<PlaylistChannelResponse> responses = List.of(
			PlaylistChannelResponse.builder()
				.memberId(1L)
				.channelName("채널명1")
				.imageUrl("www.profileImageUrl.com")
				.videoCount(4L)
				.subscribers(10)
				.isSubscribed(true)
				.build()
		);

		PageImpl<PlaylistChannelResponse> page = new PageImpl<>(responses);

		String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(page));

		given(memberService.getChannelForPlaylist(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).willReturn(page);

		return List.of(
			DynamicTest.dynamicTest(
				"페이지와 사이즈 파라미터 값을 보내지 않아도 기본값으로 조회가 된다",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/playlists/channels")
							.header(AUTHORIZATION, TOKEN)
							.accept(APPLICATION_JSON)
					);

					//then
					actions
						.andDo(print())
						.andExpect(status().isOk())
						.andExpect(content().string(apiResponse));
				}
			),
			DynamicTest.dynamicTest(
				"페이지가 양수가 아닌 경우",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/playlists/channels")
							.header(AUTHORIZATION, TOKEN)
							.param("page", "0")
							.accept(APPLICATION_JSON)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("page"))
						.andExpect(jsonPath("$.data[0].value").value(0))
						.andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
				}
			),
			DynamicTest.dynamicTest(
				"사이즈가 양수가 아닌 경우",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/playlists/channels")
							.header(AUTHORIZATION, TOKEN)
							.param("size", "0")
							.accept(APPLICATION_JSON)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("size"))
						.andExpect(jsonPath("$.data[0].value").value(0))
						.andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
				}
			)
		);
	}

	@TestFactory
	@DisplayName("구매한 강의 보관함 조회(채널별 상세) Validation 테스트")
	Collection<DynamicTest> getPlaylistChannelDetailsValidation() throws Exception {
		//given
		List<PlaylistChannelDetailsResponse> responses = List.of(
			PlaylistChannelDetailsResponse.builder().build()
		);

		PageImpl<PlaylistChannelDetailsResponse> page = new PageImpl<>(responses);

		given(memberService.getChannelDetailsForPlaylist(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).willReturn(page);

		return List.of(
			DynamicTest.dynamicTest(
				"조회할 채널의 사용자의 id가 양수가 아닌 경우",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/playlists/channels/details")
							.header(AUTHORIZATION, TOKEN)
							.param("member-id", "0")
							.accept(APPLICATION_JSON)
					);

					//then
					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("memberId"))
						.andExpect(jsonPath("$.data[0].value").value(0))
						.andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
				}
			)
		);
	}

	@TestFactory
	@DisplayName("시청 기록 조회 Validation 테스트")
	Collection<DynamicTest> getWatchsValidation() throws Exception {
		//given
		PageImpl<WatchsResponse> page = new PageImpl<>(List.of(
			WatchsResponse.builder().build()
		));

		String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(page));

		given(memberService.getWatchs(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).willReturn(page);

		return List.of(
			DynamicTest.dynamicTest(
				"페이지와 사이즈, 조회할 기간(일) 파라미터 값을 보내지 않아도 기본값으로 조회가 된다",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/watchs")
							.header(AUTHORIZATION, TOKEN)
							.accept(APPLICATION_JSON)
					);

					//then
					actions
						.andDo(print())
						.andExpect(status().isOk())
						.andExpect(content().string(apiResponse));
				}
			),
			DynamicTest.dynamicTest(
				"페이지가 양수가 아닌 경우",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/watchs")
							.header(AUTHORIZATION, TOKEN)
							.param("page", "0")
							.accept(APPLICATION_JSON)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("page"))
						.andExpect(jsonPath("$.data[0].value").value(0))
						.andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
				}
			),
			DynamicTest.dynamicTest(
				"사이즈가 양수가 아닌 경우",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/watchs")
							.header(AUTHORIZATION, TOKEN)
							.param("size", "0")
							.accept(APPLICATION_JSON)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("size"))
						.andExpect(jsonPath("$.data[0].value").value(0))
						.andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
				}
			),
			DynamicTest.dynamicTest(
				"조회할 기간(일)이 양수가 아닌 경우",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/watchs")
							.header(AUTHORIZATION, TOKEN)
							.param("day", "0")
							.accept(APPLICATION_JSON)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("day"))
						.andExpect(jsonPath("$.data[0].value").value(0))
						.andExpect(jsonPath("$.data[0].reason").value("최소 1일 이상 부터 조회 가능합니다."));
				}
			),
			DynamicTest.dynamicTest(
				"조회할 기간(일)이 31일 초과인 경우",
				() -> {
					//when
					ResultActions actions = mockMvc.perform(
						get("/members/watchs")
							.header(AUTHORIZATION, TOKEN)
							.param("day", "31")
							.accept(APPLICATION_JSON)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("day"))
						.andExpect(jsonPath("$.data[0].value").value(31))
						.andExpect(jsonPath("$.data[0].reason").value("최대 30일 까지 조회 가능합니다."));
				}
			)
		);
	}

	@TestFactory
	@DisplayName("닉네임 변경 Validation 테스트")
	Collection<DynamicTest> updateNicknameValidation() throws Exception {

		return List.of(
			DynamicTest.dynamicTest(
				"변경할 닉네임을 입력하지 않은 경우",
				() -> {
					//given
					MemberApiRequest.Nickname nickname = new MemberApiRequest.Nickname();

					String content = objectMapper.writeValueAsString(nickname);

					//when
					ResultActions actions = mockMvc.perform(
						patch("/members")
							.header(AUTHORIZATION, TOKEN)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("nickname"))
						.andExpect(jsonPath("$.data[0].value").value("null"))
						.andExpect(jsonPath("$.data[0].reason").value("한글/숫자/영어를 선택하여 사용한 1 ~ 20자를 입력하세요."));
				}
			),
			DynamicTest.dynamicTest(
				"변경할 닉네임의 길이가 한 글자 미만인 경우",
				() -> {
					//given
					MemberApiRequest.Nickname nickname = new MemberApiRequest.Nickname("");

					String content = objectMapper.writeValueAsString(nickname);

					//when
					ResultActions actions = mockMvc.perform(
						patch("/members")
							.header(AUTHORIZATION, TOKEN)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("nickname"))
						.andExpect(jsonPath("$.data[0].value").value(""))
						.andExpect(jsonPath("$.data[0].reason").value("한글/숫자/영어를 선택하여 사용한 1 ~ 20자를 입력하세요."));
				}
			),
			DynamicTest.dynamicTest(
				"변경할 닉네임의 길이가 21 글자 이상인 경우",
				() -> {
					//given
					MemberApiRequest.Nickname nickname = new MemberApiRequest.Nickname("가나다라마바사아자차카타파하가나다라마바사아자");

					String content = objectMapper.writeValueAsString(nickname);

					//when
					ResultActions actions = mockMvc.perform(
						patch("/members")
							.header(AUTHORIZATION, TOKEN)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("nickname"))
						.andExpect(jsonPath("$.data[0].value").value("가나다라마바사아자차카타파하가나다라마바사아자"))
						.andExpect(jsonPath("$.data[0].reason").value("한글/숫자/영어를 선택하여 사용한 1 ~ 20자를 입력하세요."));
				}
			)
		);
	}

	@TestFactory
	@DisplayName("프로필 이미지 변경 Validation 테스트")
	Collection<DynamicTest> updateImageValidation() throws Exception {

		return List.of(
			DynamicTest.dynamicTest(
				"이미지 파일명을 입력하지 않은 경우",
				() -> {
					//given
					MemberApiRequest.Image image = new MemberApiRequest.Image();
					image.setImageType(ImageType.PNG);

					String content = objectMapper.writeValueAsString(image);

					//when
					ResultActions actions = mockMvc.perform(
						patch("/members/image")
							.header(AUTHORIZATION, TOKEN)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("imageName"))
						.andExpect(jsonPath("$.data[0].value").value("null"))
						.andExpect(jsonPath("$.data[0].reason").value("이미지 이름은 필수입니다."));
				}
			),
			DynamicTest.dynamicTest(
				"이미지 확장자 타입을 입력하지 않은 경우",
				() -> {
					//given
					MemberApiRequest.Image image = new MemberApiRequest.Image();
					image.setImageName("imageName");

					String content = objectMapper.writeValueAsString(image);

					//when
					ResultActions actions = mockMvc.perform(
						patch("/members/image")
							.header(AUTHORIZATION, TOKEN)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("imageType"))
						.andExpect(jsonPath("$.data[0].value").value("null"))
						.andExpect(jsonPath("$.data[0].reason").value("jpg, jpeg, png 확장자만 지원합니다."));
				}
			),
			DynamicTest.dynamicTest(
				"잘못된 이미지 확장자 타입인 경우",
				() -> {
					//given
					String content = "{\"imageName\":\"imageName\",\"imageType\":\"PPP\"}";

					//when
					ResultActions actions = mockMvc.perform(
						patch("/members/image")
							.header(AUTHORIZATION, TOKEN)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("imageType"))
						.andExpect(jsonPath("$.data[0].value").value("null"))
						.andExpect(jsonPath("$.data[0].reason").value("jpg, jpeg, png 확장자만 지원합니다."));
				}
			)
		);
	}

	@TestFactory
	@DisplayName("비밀번호 변경 Validation 테스트")
	Collection<DynamicTest> updatePasswordValidation() throws Exception {

		return List.of(
			DynamicTest.dynamicTest(
				"이전 비밀번호를 입력하지 않은 경우",
				() -> {
					//given
					MemberApiRequest.Password password = new MemberApiRequest.Password();
					password.setNewPassword("abcd1234!");

					String content = objectMapper.writeValueAsString(password);

					//when
					ResultActions actions = mockMvc.perform(
						patch("/members/password")
							.header(AUTHORIZATION, TOKEN)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("prevPassword"))
						.andExpect(jsonPath("$.data[0].value").value("null"))
						.andExpect(jsonPath("$.data[0].reason").value("문자, 숫자, 특수문자로 이루어진 9~20자를 입력하세요."));
				}
			),
			DynamicTest.dynamicTest(
				"변경할 비밀번호를 입력하지 않은 경우",
				() -> {
					//given
					MemberApiRequest.Password password = new MemberApiRequest.Password();
					password.setPrevPassword("abcd1234!");

					String content = objectMapper.writeValueAsString(password);

					//when
					ResultActions actions = mockMvc.perform(
						patch("/members/password")
							.header(AUTHORIZATION, TOKEN)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("newPassword"))
						.andExpect(jsonPath("$.data[0].value").value("null"))
						.andExpect(jsonPath("$.data[0].reason").value("문자, 숫자, 특수문자로 이루어진 9~20자를 입력하세요."));
				}
			),
			DynamicTest.dynamicTest(
				"비밀번호의 길이가 8글자 이하인 경우",
				() -> {
					//given
					MemberApiRequest.Password password = new MemberApiRequest.Password();
					password.setPrevPassword("abcd1234!");
					password.setNewPassword("abcd123!");

					String content = objectMapper.writeValueAsString(password);

					//when
					ResultActions actions = mockMvc.perform(
						patch("/members/password")
							.header(AUTHORIZATION, TOKEN)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("newPassword"))
						.andExpect(jsonPath("$.data[0].value").value("abcd123!"))
						.andExpect(jsonPath("$.data[0].reason").value("허용된 글자 수는 9자에서 20자 입니다."));
				}
			),
			DynamicTest.dynamicTest(
				"비밀번호의 길이가 21글자 이상인 경우",
				() -> {
					//given
					MemberApiRequest.Password password = new MemberApiRequest.Password();
					password.setPrevPassword("abcd1234!");
					password.setNewPassword("abcdefghijklmnopqrs1!");

					String content = objectMapper.writeValueAsString(password);

					//when
					ResultActions actions = mockMvc.perform(
						patch("/members/password")
							.header(AUTHORIZATION, TOKEN)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("newPassword"))
						.andExpect(jsonPath("$.data[0].value").value("abcdefghijklmnopqrs1!"))
						.andExpect(jsonPath("$.data[0].reason").value("허용된 글자 수는 9자에서 20자 입니다."));
				}
			),
			DynamicTest.dynamicTest(
				"특수문자가 포함되지 않은 경우",
				() -> {
					//given
					MemberApiRequest.Password password = new MemberApiRequest.Password();
					password.setPrevPassword("abcd1234!");
					password.setNewPassword("abcde12345");

					String content = objectMapper.writeValueAsString(password);

					//when
					ResultActions actions = mockMvc.perform(
						patch("/members/password")
							.header(AUTHORIZATION, TOKEN)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("newPassword"))
						.andExpect(jsonPath("$.data[0].value").value("abcde12345"))
						.andExpect(jsonPath("$.data[0].reason").value("문자, 숫자, 특수문자로 이루어진 9~20자를 입력하세요."));
				}
			),
			DynamicTest.dynamicTest(
				"숫자가 포함되지 않은 경우",
				() -> {
					//given
					MemberApiRequest.Password password = new MemberApiRequest.Password();
					password.setPrevPassword("abcd1234!");
					password.setNewPassword("abcdefgh!");

					String content = objectMapper.writeValueAsString(password);

					//when
					ResultActions actions = mockMvc.perform(
						patch("/members/password")
							.header(AUTHORIZATION, TOKEN)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("newPassword"))
						.andExpect(jsonPath("$.data[0].value").value("abcdefgh!"))
						.andExpect(jsonPath("$.data[0].reason").value("문자, 숫자, 특수문자로 이루어진 9~20자를 입력하세요."));
				}
			),
			DynamicTest.dynamicTest(
				"영어가 포함되지 않은 경우",
				() -> {
					//given
					MemberApiRequest.Password password = new MemberApiRequest.Password();
					password.setPrevPassword("abcd1234!");
					password.setNewPassword("12345678!");

					String content = objectMapper.writeValueAsString(password);

					//when
					ResultActions actions = mockMvc.perform(
						patch("/members/password")
							.header(AUTHORIZATION, TOKEN)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("newPassword"))
						.andExpect(jsonPath("$.data[0].value").value("12345678!"))
						.andExpect(jsonPath("$.data[0].reason").value("문자, 숫자, 특수문자로 이루어진 9~20자를 입력하세요."));
				}
			)
		);
	}
}
