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
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import com.server.domain.member.service.dto.response.RewardsResponse;
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
				.entityId(1L)
				.rewardType(RewardType.VIDEO)
				.rewardPoint(100)
				.createdDate(LocalDateTime.now())
				.build(),
			RewardsResponse.builder()
				.entityId(33L)
				.rewardType(RewardType.QUIZ)
				.rewardPoint(10)
				.createdDate(LocalDateTime.now())
				.build(),
			RewardsResponse.builder()
				.entityId(114L)
				.rewardType(RewardType.VIDEO)
				.rewardPoint(300)
				.createdDate(LocalDateTime.now())
				.build(),
			RewardsResponse.builder()
				.entityId(418L)
				.rewardType(RewardType.QUIZ)
				.rewardPoint(5)
				.createdDate(LocalDateTime.now())
				.build()
		);

		PageImpl<RewardsResponse> page = new PageImpl<>(responses);

		given(memberService.getRewards(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).willReturn(page);

		//when
		ResultActions actions = mockMvc.perform(
			get("/members/rewards")
				.header(AUTHORIZATION, TOKEN)
				.param("page","1")
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
						parameterWithName("page").description("조회할 리워드 목록 페이지")
					),
					pageResponseFields(
						fieldWithPath("data[]").description("리워드 목록"),
						fieldWithPath("data[].entityId").description("리워드를 획득한 엔티티의 ID"),
						fieldWithPath("data[].rewardType").description("리워드 타입"),
						fieldWithPath("data[].rewardPoint").description("지급된 리워드"),
						fieldWithPath("data[].createdDate").description("리워드 지급 날짜")
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
						parameterWithName("page").description("조회할 구독 목록 페이지")
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
						parameterWithName("page").description("조회할 장바구니 페이지")
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
				.reward(300)
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
				.reward(400)
				.orderCount(6)
				.orderStatus(OrderStatus.CANCELED)
				.createdDate(LocalDateTime.now())
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
				.reward(200)
				.orderCount(3)
				.orderStatus(OrderStatus.COMPLETED)
				.createdDate(LocalDateTime.now())
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
				.reward(100)
				.orderCount(7)
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
				.param("month", "12")
				.accept(APPLICATION_JSON)
		);

		//then
		RestDocsUtil.assertPageResponse(actions, responses.size());

		FieldDescriptor[] responseFields = new FieldDescriptor[]{
			fieldWithPath("data[]").description("결제 목록"),
			fieldWithPath("data[].orderId").description("결제 번호"),
			fieldWithPath("data[].reward").description("획득한 리워드"),
			fieldWithPath("data[].orderCount").description("결제한 강의 수"),
			fieldWithPath("data[].orderStatus").description("주문 상태"),
			fieldWithPath("data[].createdDate").description("결제일"),
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
							parameterWithName("page").description("조회할 결제 목록 페이지"),
							parameterWithName("month").description("조회할 범위 지정(월 단위)")
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
				.thumbnailFile("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
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
				.thumbnailFile("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
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
				.thumbnailFile("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
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
				.thumbnailFile("https://d2ouhv9pc4idoe.cloudfront.net/9999/test")
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

		given(memberService.getPlaylists(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString())).willReturn(page);

		//when
		ResultActions actions = mockMvc.perform(
			get("/members/playlists")
				.header(AUTHORIZATION, TOKEN)
				.param("page",  "1")
				.param("size", "10")
				.param("sort", "star")
				.accept(APPLICATION_JSON)
		);

		RestDocsUtil.assertPageResponse(actions, responses.size());

		FieldDescriptor[] responseFields = new FieldDescriptor[]{
			fieldWithPath("data[]").description("구매한 강의 목록"),
			fieldWithPath("data[].videoId").description("구매한 영상 ID"),
			fieldWithPath("data[].videoName").description("구매한 영상명"),
			fieldWithPath("data[].thumbnailFile").description("영상의 썸네일 이미지 주소"),
			fieldWithPath("data[].star").description("영상 평균 별점"),
			fieldWithPath("data[].modifiedDate").description("영상 업데이트 날짜"),
			fieldWithPath("data[].channel").description("영상 업로더의 채널 정보"),
			fieldWithPath("data[].channel.memberId").description("업로더의 아이디"),
			fieldWithPath("data[].channel.channelName").description("업로더의 채널명")
		};

		actions
			.andDo(
				documentHandler
					.document(
						requestHeaders(
							headerWithName(AUTHORIZATION).description("액세스 토큰")
						),
						requestParameters(
							parameterWithName("page").description("구매한 강의 목록 페이지"),
							parameterWithName("size").description("페이지의 데이터 수"),
							parameterWithName("sort").description("정렬 기준(최신순, 평점순, 채널별, 이름순)")
						),
						responseFields(
							RestDocsUtil.getPageResponseFields(responseFields)
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
				.thumbnailFile(awsService.getImageUrl("test22"))
				.modifiedDate(LocalDateTime.now())
				.channel(WatchsResponse.Channel.builder()
					.memberId(4325L)
					.channelName("채널1")
					.build())
				.build(),
			WatchsResponse.builder()
				.videoId(791L)
				.videoName("리액트")
				.thumbnailFile(awsService.getImageUrl("test22"))
				.modifiedDate(LocalDateTime.now())
				.channel(WatchsResponse.Channel.builder()
					.memberId(4325L)
					.channelName("채널2")
					.build())
				.build(),
			WatchsResponse.builder()
				.videoId(791L)
				.videoName("스프링")
				.thumbnailFile(awsService.getImageUrl("test22"))
				.modifiedDate(LocalDateTime.now())
				.channel(WatchsResponse.Channel.builder()
					.memberId(4325L)
					.channelName("채널3")
					.build())
				.build(),
			WatchsResponse.builder()
				.videoId(791L)
				.videoName("자바")
				.thumbnailFile(awsService.getImageUrl("test22"))
				.modifiedDate(LocalDateTime.now())
				.channel(WatchsResponse.Channel.builder()
					.memberId(4325L)
					.channelName("채널3")
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
				.param("day", "14")
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
			fieldWithPath("data[].channel.channelName").description("업로더의 채널명")
		};

		actions
			.andDo(
				documentHandler
					.document(
						requestHeaders(
							headerWithName(AUTHORIZATION).description("액세스 토큰")
						),
						requestParameters(
							parameterWithName("page").description("조회할 페이지"),
							parameterWithName("day").description("조회 기간 설정")
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
		MemberApiRequest.Image request = new MemberApiRequest.Image(
			"imageName", ImageType.JPG
		);

		String content = objectMapper.writeValueAsString(request);

		String presignedUrl = "http://www.uploadUrl.com";

		given(awsService.getUploadImageUrl(request.getImageName(), request.getImageType()))
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
							fieldWithPath("imageName").description("업로드할 이미지명").attributes(getConstraint("imageName")),
							fieldWithPath("imageType").description("이미지 확장자").attributes(getConstraint("imageType"))
						),
						responseHeaders(
							headerWithName(LOCATION).description("이미지 업로드 URL")
						)
					)
			);
	}

	@Test
	@DisplayName("패스워드 변경 성공 테스트")
	void updatePassword() throws Exception {
		MemberApiRequest.Password request = new MemberApiRequest.Password(
			"abcd1234", "1234abcd"
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
}
