package com.server.domain.video.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.server.domain.video.controller.dto.request.VideoSort;
import com.server.domain.video.service.dto.response.VideoCategoryResponse;
import com.server.domain.video.service.dto.response.VideoChannelResponse;
import com.server.domain.video.service.dto.response.VideoPageResponse;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import com.server.global.testhelper.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VideoControllerTest extends ControllerTest {

    private final String BASE_URL = "/videos";

    @Test
    @DisplayName("비디오의 전체 문제 조회 API")
    void getQuestions() throws Exception {

    }

    @Test
    @DisplayName("비디오의 전체 문제 풀기 API")
    void solveQuestions() {
    }

    @Test
    @DisplayName("비디오의 질문 전체 생성 API")
    void createQuestions() {
    }

    @Test
    @DisplayName("비디오 목록 조회 API")
    void getVideos() throws Exception {
        //given
        int page = 1;
        int size = 8;

        List<VideoPageResponse> responses = createVideoPageResponses(size);
        Page<VideoPageResponse> pageResponses = createPage(responses, page, size, 50);

        String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(pageResponses, "비디오 목록 조회 성공"));

        given(videoService.getVideos(anyLong(), anyInt(), anyInt(), anyString(), anyString(), anyBoolean()))
                .willReturn(pageResponses);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sort", "created-date")
                        .param("category", "spring")
                        .param("subscribe", "true")
                        .accept(APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        // restdocs
        actions.andDo(documentHandler.document(
                requestHeaders(
                        headerWithName("Authorization").description("Access Token").optional()
                ),
                requestParameters(
                        parameterWithName("page").description("페이지 번호").optional(),
                        parameterWithName("size").description("페이지 사이즈").optional(),
                        parameterWithName("sort").description(generateLinkCode(VideoSort.class)).optional(),
                        parameterWithName("category").description("카테고리").optional(),
                        parameterWithName("subscribe").description("구독 여부").optional()
                ),
                responseFields(
                        fieldWithPath("data").description("비디오 목록"),
                        fieldWithPath("data[].videoId").description("비디오 ID"),
                        fieldWithPath("data[].videoName").description("비디오 제목"),
                        fieldWithPath("data[].thumbnailUrl").description("섬네일 URL"),
                        fieldWithPath("data[].views").description("조회 수"),
                        fieldWithPath("data[].price").description("가격"),
                        fieldWithPath("data[].star").description("별점"),
                        fieldWithPath("data[].isPurchased").description("구매 여부"),
                        fieldWithPath("data[].categories").description("카테고리 목록"),
                        fieldWithPath("data[].categories[].categoryId").description("카테고리 ID"),
                        fieldWithPath("data[].categories[].categoryName").description("카테고리 이름"),
                        fieldWithPath("data[].channel").description("채널 정보"),
                        fieldWithPath("data[].channel.memberId").description("채널의 member ID"),
                        fieldWithPath("data[].channel.channelName").description("채널 이름"),
                        fieldWithPath("data[].channel.subscribes").description("구독자 수"),
                        fieldWithPath("data[].channel.isSubscribed").description("채널 구독 여부"),
                        fieldWithPath("data[].channel.imageUrl").description("채널 프로필 이미지 URL"),
                        fieldWithPath("data[].createdDate").description("채널 생성일"),
                        fieldWithPath("pageInfo").description("페이징 정보"),
                        fieldWithPath("pageInfo.page").description("현재 페이지"),
                        fieldWithPath("pageInfo.size").description("페이지 사이즈"),
                        fieldWithPath("pageInfo.totalPage").description("전체 페이지 수"),
                        fieldWithPath("pageInfo.totalSize").description("전체 개수"),
                        fieldWithPath("pageInfo.first").description("첫 페이지 여부"),
                        fieldWithPath("pageInfo.last").description("마지막 페이지 여부"),
                        fieldWithPath("pageInfo.hasNext").description("다음 페이지가 있는지"),
                        fieldWithPath("pageInfo.hasPrevious").description("이전 페이지가 있는지"),
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("status").description("응답 상태"),
                        fieldWithPath("message").description("응답 메시지")
                )
        ));

    }

    @Test
    @DisplayName("비디오 상세 조회 API")
    void getVideo() {
    }

    @Test
    @DisplayName("비디오 생성 URL 조회 API")
    void getVideoCreateUrl() {
    }

    @Test
    @DisplayName("비디오 생성 API")
    void createVideo() {
    }

    @Test
    @DisplayName("비디오 수정 API")
    void updateVideo() {
    }

    @Test
    @DisplayName("장바구니 담기 API")
    void changeCartAdd() {
    }

    @Test
    @DisplayName("장바구니 취소 API")
    void changeCartCancel() {
    }

    @Test
    @DisplayName("비디오 삭제 API")
    void deleteVideo() {
    }

    private List<VideoPageResponse> createVideoPageResponses(int size) {

        List<VideoPageResponse> responses = new ArrayList<>();

        for (int i = 1; i <= size; i++) {
            responses.add(VideoPageResponse.builder()
                    .videoId((long) i)
                    .videoName("비디오 제목" + i)
                    .thumbnailUrl("https://www.cloudfront.net/" + i + "/thumbnail")
                    .views(1000 * i)
                    .price(1000 * i)
                    .star(4.5f)
                    .isPurchased(true)
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

    private <T> Page<T> createPage(List<T> contents, int page, int size, int totalElements) {
        return new PageImpl<>(contents, PageRequest.of(page, size), totalElements);
    }


}