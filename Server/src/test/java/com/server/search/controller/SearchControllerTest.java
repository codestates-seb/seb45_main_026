package com.server.search.controller;


import static com.server.global.testhelper.RestDocsUtil.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.server.domain.video.controller.dto.request.AnswersCreateApiRequest;
import com.server.domain.video.controller.dto.request.VideoSort;
import com.server.domain.video.service.dto.request.VideoGetServiceRequest;
import com.server.domain.video.service.dto.response.VideoCategoryResponse;
import com.server.domain.video.service.dto.response.VideoChannelResponse;
import com.server.domain.video.service.dto.response.VideoPageResponse;
import com.server.global.reponse.ApiPageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
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

	@Test
	@DisplayName("비디오 검색 결과 API")
	void videoSearch() throws Exception {
		//given
		int page = 1;
		int size = 8;

		List<VideoPageResponse> responses = createVideoPageResponses(size);
		Page<VideoPageResponse> pageResponses = createPage(responses, page, size, 50);

		String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(pageResponses, "비디오 목록 검색 성공"));

		given(videoService.searchVideos(anyString(), any(VideoGetServiceRequest.class)))
				.willReturn(pageResponses);

		//when
		ResultActions actions = mockMvc.perform(
				get("/search/videos")
						.param("keyword", "spring")
						.param("page", String.valueOf(page))
						.param("size", String.valueOf(size))
						.param("sort", "created-date")
						.param("category", "spring")
						.param("subscribe", "true")
						.param("free", "false")
						.param("is-purchased", "true")
						.accept(APPLICATION_JSON)
						.header(AUTHORIZATION, TOKEN)
		);

		//then
		actions.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(apiResponse));

		// restdocs
		actions.andDo(documentHandler.document(
				requestHeaders(
						headerWithName(AUTHORIZATION).description("Access Token").optional()
				),
				requestParameters(
						parameterWithName("keyword").description("검색 키워드"),
						parameterWithName("page").description("페이지 번호").optional(),
						parameterWithName("size").description("페이지 사이즈").optional(),
						parameterWithName("sort").description(generateLinkCode(VideoSort.class)).optional(),
						parameterWithName("category").description("카테고리").optional(),
						parameterWithName("subscribe").description("구독 여부").optional(),
						parameterWithName("free").description("무료/유료 여부").optional(),
						parameterWithName("is-purchased").description("구매한 비디오도 표시하는지 여부").optional()
				),
				pageResponseFields(
						fieldWithPath("data").description("비디오 목록"),
						fieldWithPath("data[].videoId").description("비디오 ID"),
						fieldWithPath("data[].videoName").description("비디오 제목"),
						fieldWithPath("data[].thumbnailUrl").description("섬네일 URL"),
						fieldWithPath("data[].views").description("조회 수"),
						fieldWithPath("data[].price").description("가격"),
						fieldWithPath("data[].star").description("별점"),
						fieldWithPath("data[].isPurchased").description("구매 여부"),
						fieldWithPath("data[].isInCart").description("장바구니 추가 여부"),
						fieldWithPath("data[].description").description("비디오 설명"),
						fieldWithPath("data[].categories").description("카테고리 목록"),
						fieldWithPath("data[].categories[].categoryId").description("카테고리 ID"),
						fieldWithPath("data[].categories[].categoryName").description("카테고리 이름"),
						fieldWithPath("data[].channel").description("채널 정보"),
						fieldWithPath("data[].channel.memberId").description("채널의 member ID"),
						fieldWithPath("data[].channel.channelName").description("채널 이름"),
						fieldWithPath("data[].channel.subscribes").description("구독자 수"),
						fieldWithPath("data[].channel.isSubscribed").description("채널 구독 여부"),
						fieldWithPath("data[].channel.imageUrl").description("채널 프로필 이미지 URL"),
						fieldWithPath("data[].createdDate").description("채널 생성일")
				)
		));
	}

	@TestFactory
	@DisplayName("비디오 검색 시 validation 테스트")
	Collection<DynamicTest> videoSearchValidation() throws Exception {
		//given
		String keyword = "spring";
		int page = 1;
		int size = 12;

		List<VideoPageResponse> responses = createVideoPageResponses(size);
		Page<VideoPageResponse> pageResponses = createPage(responses, page, size, 50);

		String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(pageResponses, "비디오 목록 검색 성공"));

		given(videoService.searchVideos(anyString(), any(VideoGetServiceRequest.class)))
				.willReturn(pageResponses);


		return List.of(
				dynamicTest("키워드를 제외하고 쿼리 값을 보내지 않아도 조회할 수 있다.", ()-> {
					//when
					ResultActions actions = mockMvc.perform(
							get("/search/videos")
									.contentType(APPLICATION_JSON)
									.accept(APPLICATION_JSON)
									.header(AUTHORIZATION, TOKEN)
									.param("keyword", keyword)
					);

					//then
					actions.andDo(print())
							.andExpect(status().isOk())
							.andExpect(content().string(apiResponse));
				}),
				dynamicTest("키워드가 null 이면 검증에 실패한다.", ()-> {
					//when
					ResultActions actions = mockMvc.perform(
							get("/search/videos")
									.contentType(APPLICATION_JSON)
									.accept(APPLICATION_JSON)
									.header(AUTHORIZATION, TOKEN)
					);

					//then
					actions.andDo(print())
							.andExpect(status().isBadRequest())
							.andExpect(jsonPath("$.data[0].field").value("keyword"))
							.andExpect(jsonPath("$.data[0].value").value("null"))
							.andExpect(jsonPath("$.data[0].reason").value("keyword 값은 필수입니다."));
				}),
				dynamicTest("page 가 양수가 아니면 검증에 실패한다.", ()-> {
					//given
					int wrongPage = 0;

					//when
					ResultActions actions = mockMvc.perform(
							get("/search/videos")
									.contentType(APPLICATION_JSON)
									.accept(APPLICATION_JSON)
									.header(AUTHORIZATION, TOKEN)
									.param("keyword", keyword)
									.param("page", String.valueOf(wrongPage))
					);

					//then
					actions.andDo(print())
							.andExpect(status().isBadRequest())
							.andExpect(jsonPath("$.data[0].field").value("page"))
							.andExpect(jsonPath("$.data[0].value").value(wrongPage))
							.andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
				}),
				dynamicTest("size 가 양수가 아니면 검증에 실패한다.", ()-> {
					//given
					int wrongSize = 0;

					//when
					ResultActions actions = mockMvc.perform(
							get("/search/videos")
									.contentType(APPLICATION_JSON)
									.accept(APPLICATION_JSON)
									.header(AUTHORIZATION, TOKEN)
									.param("keyword", keyword)
									.param("size", String.valueOf(wrongSize))
					);

					//then
					actions.andDo(print())
							.andExpect(status().isBadRequest())
							.andExpect(jsonPath("$.data[0].field").value("size"))
							.andExpect(jsonPath("$.data[0].value").value(wrongSize))
							.andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));

				})
		);
	}

	private List<VideoPageResponse> createVideoPageResponses(int size) {

		List<VideoPageResponse> responses = new ArrayList<>();

		for (int i = 1; i <= size; i++) {
			responses.add(VideoPageResponse.builder()
					.videoId((long) i)
					.videoName("spring " + i)
					.thumbnailUrl("https://www.cloudfront.net/" + i + "/thumbnail")
					.views(1000 * i)
					.price(1000 * i)
					.star(4.5f)
					.isPurchased(true)
					.isInCart(false)
					.channel(createVideoChannelResponse())
					.categories(createVideoCategoryResponse("java", "react"))
					.createdDate(LocalDateTime.now())
					.build());
		}

		return responses;
	}

	private VideoChannelResponse createVideoChannelResponse() {
		return VideoChannelResponse.builder()
				.memberId(1L)
				.channelName("채널 이름")
				.isSubscribed(true)
				.subscribes(1000)
				.imageUrl("https://www.cloudfront.net/images/" + 1)
				.build();
	}

	private List<VideoCategoryResponse> createVideoCategoryResponse(String... categoryNames) {
		List<VideoCategoryResponse> responses = new ArrayList<>();

		for (int i = 1; i <= categoryNames.length; i++) {
			responses.add(VideoCategoryResponse.builder()
					.categoryId((long) i)
					.categoryName(categoryNames[i - 1])
					.build());
		}

		return responses;
	}
}
