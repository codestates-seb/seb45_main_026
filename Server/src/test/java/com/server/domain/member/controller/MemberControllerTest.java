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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.ResultActions;

import com.server.domain.member.aop.MemberStubAop;
import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Grade;
import com.server.domain.member.entity.Member;
import com.server.domain.member.service.dto.response.ProfileResponse;
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
	void getRewards() {
		//given

	}
}
