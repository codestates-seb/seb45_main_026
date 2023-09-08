package com.server.search.controller;


import static com.server.global.testhelper.RestDocsUtil.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.server.global.reponse.ApiSingleResponse;
import com.server.global.testhelper.ControllerTest;
import com.server.search.engine.dto.ChannelSearchResponse;
import com.server.search.engine.dto.VideoChannelSearchResponse;
import com.server.search.engine.dto.VideoSearchResponse;

public class SearchControllerTest extends ControllerTest {
	private static final String THUMBNAIL = "www.thumbnail.com";
	private static final String PROFILE = "www.profileImage.com";

	@Test
	@DisplayName("검색 성공 테스트")
	void search() throws Exception {
		// given
		String keyword = "검";
		String limit = "3";

		VideoChannelSearchResponse response =
			VideoChannelSearchResponse.builder()
				.videos(
					List.of(
						VideoSearchResponse.builder()
							.videoId(1L)
							.videoName("이렇게붙어있어도검색이됩니다")
							.thumbnailUrl(THUMBNAIL)
							.build(),
						VideoSearchResponse.builder()
							.videoId(2L)
							.videoName("공백과 특수문자는 검색이 안됩니다")
							.thumbnailUrl(THUMBNAIL)
							.build(),
						VideoSearchResponse.builder()
							.videoId(3L)
							.videoName("이1것2도3검4색5해6보7시8지9!")
							.thumbnailUrl(THUMBNAIL)
							.build()
					)
				)
				.channels(
					List.of(
						ChannelSearchResponse.builder()
							.memberId(1L)
							.channelName("검색되는 채널명")
							.imageUrl(PROFILE)
							.build(),
						ChannelSearchResponse.builder()
							.memberId(1L)
							.channelName("검이 들어가는 채널명(O)")
							.imageUrl(PROFILE)
							.build(),
						ChannelSearchResponse.builder()
							.memberId(1L)
							.channelName("거엄색 안되는 채널명(X)")
							.imageUrl(PROFILE)
							.build()
					)
				)
				.build();

		String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(response));

		given(searchEngine.searchVideosAndChannels(Mockito.anyString(), Mockito.anyInt()))
			.willReturn(response);

		ResultActions actions = mockMvc.perform(
			get("/search")
				.param("keyword", keyword)
				.param("limit", limit)
				.accept(MediaType.APPLICATION_JSON)
		);

		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().string(apiResponse));

		actions
			.andDo(
				documentHandler.document(
					requestParameters(
						parameterWithName("keyword").description("검색할 키워드 (최소 한글자, 공백 및 특수 문자 불가능)"),
						parameterWithName("limit").description("검색할 개수 (예: limit = 3, 비디오, 채널 각 3개 검색)").optional()
					),
					singleResponseFields(
						fieldWithPath("data").description("전체 검색 결과"),
						fieldWithPath("data.videos[]").description("비디오 검색 결과"),
						fieldWithPath("data.videos[].videoId").description("비디오 아이디"),
						fieldWithPath("data.videos[].videoName").description("비디오 이름"),
						fieldWithPath("data.videos[].thumbnailUrl").description("비디오 썸네일 URL"),
						fieldWithPath("data.channels[]").description("비디오 검색 결과"),
						fieldWithPath("data.channels[].memberId").description("채널을 소유한 회원의 아이디"),
						fieldWithPath("data.channels[].channelName").description("채널 이름"),
						fieldWithPath("data.channels[].imageUrl").description("회원의 프로필 이미지 URL")
					)
				)
			);
	}
}
