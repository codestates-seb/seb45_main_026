package com.server.domain.member.controller;

import static com.server.auth.util.AuthConstant.*;
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
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.ResultActions;

import com.server.domain.member.aop.MemberStubAop;
import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Grade;
import com.server.domain.member.entity.Member;
import com.server.domain.member.service.dto.response.ProfileResponse;
import com.server.domain.member.service.dto.response.RewardsResponse;
import com.server.domain.member.service.dto.response.SubscribesResponse;
import com.server.domain.reward.entity.RewardType;
import com.server.global.reponse.ApiSingleResponse;
import com.server.global.testhelper.ControllerTest;

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
			.imageUrl(awsService.getImageUrl("test"))
			.grade(Grade.PLATINUM)
			.reward(777)
			.createdDate(LocalDateTime.now())
			.build();

		String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(response, "프로필 조회 성공"));

		given(memberService.getMember(Mockito.anyLong())).willReturn(response);

		// when
		ResultActions actions = mockMvc.perform(
			get("/members")
				.header(AUTHORIZATION, "Bearer aaa.bbb.ccc")
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

		given(memberService.getRewards(Mockito.anyLong())).willReturn(page);

		//when
		ResultActions actions = mockMvc.perform(
			get("/members/rewards")
				.header(AUTHORIZATION, "Bearer aaa.bbb.ccc")
				.param("page","1")
				.accept(APPLICATION_JSON)
		);

		//then
		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.pageInfo.page").value(1))
			.andExpect(jsonPath("$.pageInfo.size").value(responses.size()));

		actions
			.andDo(
				documentHandler.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("액세스 토큰")
					),
					requestParameters(
						parameterWithName("page").description("리워드 목록 페이지")
					),
					responseFields(
						fieldWithPath("data[]").description("리워드 목록"),
						fieldWithPath("data[].entityId").description("리워드를 획득한 엔티티의 ID"),
						fieldWithPath("data[].rewardType").description("리워드 타입"),
						fieldWithPath("data[].rewardPoint").description("지급된 리워드"),
						fieldWithPath("data[].date").description("리워드 지급 날짜"),
						fieldWithPath("pageInfo").description("페이지네이션 정보"),
						fieldWithPath("pageInfo.page").description("현재 페이지"),
						fieldWithPath("pageInfo.size").description("페이지 사이즈"),
						fieldWithPath("pageInfo.totalPage").description("전체 페이지 수"),
						fieldWithPath("pageInfo.totalSize").description("전체 데이터 개수"),
						fieldWithPath("pageInfo.first").description("첫 페이지 여부"),
						fieldWithPath("pageInfo.last").description("마지막 페이지 여부"),
						fieldWithPath("pageInfo.hasNext").description("다음 페이지 존재 여부"),
						fieldWithPath("pageInfo.hasPrevious").description("이전 페이지 존재 여부"),
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("status").description("응답 상태"),
						fieldWithPath("message").description("응답 메시지")
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

		given(memberService.getSubscribes(Mockito.anyLong())).willReturn(page);

		//when
		ResultActions actions = mockMvc.perform(
			get("/members/subscribes")
				.header(AUTHORIZATION, "Bearer aaa.bbb.ccc")
				.param("page","1")
				.accept(APPLICATION_JSON)
		);

		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.pageInfo.page").value(1))
			.andExpect(jsonPath("$.pageInfo.size").value(responses.size()));

		actions
			.andDo(
				documentHandler.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("액세스 토큰")
					),
					requestParameters(
						parameterWithName("page").description("구독 목록 페이지")
					),
					responseFields(
						fieldWithPath("data[]").description("구독 목록"),
						fieldWithPath("data[].memberId").description("구독한 채널의 ID"),
						fieldWithPath("data[].channelName").description("구독한 채널명"),
						fieldWithPath("data[].subscribes").description("채널의 구독자 수"),
						fieldWithPath("data[].imageUrl").description("채널 소유자의 프로필 이미지"),
						fieldWithPath("pageInfo").description("페이지네이션 정보"),
						fieldWithPath("pageInfo.page").description("현재 페이지"),
						fieldWithPath("pageInfo.size").description("페이지 사이즈"),
						fieldWithPath("pageInfo.totalPage").description("전체 페이지 수"),
						fieldWithPath("pageInfo.totalSize").description("전체 데이터 개수"),
						fieldWithPath("pageInfo.first").description("첫 페이지 여부"),
						fieldWithPath("pageInfo.last").description("마지막 페이지 여부"),
						fieldWithPath("pageInfo.hasNext").description("다음 페이지 존재 여부"),
						fieldWithPath("pageInfo.hasPrevious").description("이전 페이지 존재 여부"),
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("status").description("응답 상태"),
						fieldWithPath("message").description("응답 메시지")
					)
				)
			);
	}

	@Test
	@DisplayName("장바구니(찜 목록) 조회 성공 테스트")
	void getCarts() throws Exception {
		//given
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

		given(memberService.getSubscribes(Mockito.anyLong())).willReturn(page);

		//when
		ResultActions actions = mockMvc.perform(
			get("/members/subscribes")
				.header(AUTHORIZATION, "Bearer aaa.bbb.ccc")
				.param("page","1")
				.accept(APPLICATION_JSON)
		);

		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.pageInfo.page").value(1))
			.andExpect(jsonPath("$.pageInfo.size").value(responses.size()));

		actions
			.andDo(
				documentHandler.document(
					requestHeaders(
						headerWithName(AUTHORIZATION).description("액세스 토큰")
					),
					requestParameters(
						parameterWithName("page").description("구독 목록 페이지")
					),
					responseFields(
						fieldWithPath("data[]").description("구독 목록"),
						fieldWithPath("data[].memberId").description("구독한 채널의 ID"),
						fieldWithPath("data[].channelName").description("구독한 채널명"),
						fieldWithPath("data[].subscribes").description("채널의 구독자 수"),
						fieldWithPath("data[].imageUrl").description("채널 소유자의 프로필 이미지"),
						fieldWithPath("pageInfo").description("페이지네이션 정보"),
						fieldWithPath("pageInfo.page").description("현재 페이지"),
						fieldWithPath("pageInfo.size").description("페이지 사이즈"),
						fieldWithPath("pageInfo.totalPage").description("전체 페이지 수"),
						fieldWithPath("pageInfo.totalSize").description("전체 데이터 개수"),
						fieldWithPath("pageInfo.first").description("첫 페이지 여부"),
						fieldWithPath("pageInfo.last").description("마지막 페이지 여부"),
						fieldWithPath("pageInfo.hasNext").description("다음 페이지 존재 여부"),
						fieldWithPath("pageInfo.hasPrevious").description("이전 페이지 존재 여부"),
						fieldWithPath("code").description("응답 코드"),
						fieldWithPath("status").description("응답 상태"),
						fieldWithPath("message").description("응답 메시지")
					)
				)
			);
	}
}
