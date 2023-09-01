package com.server.domain.channel.controller;

import com.server.domain.announcement.service.dto.request.AnnouncementCreateServiceRequest;
import com.server.domain.announcement.service.dto.response.AnnouncementResponse;
import com.server.domain.channel.controller.dto.request.CreateAnnouncementApiRequest;
import com.server.domain.channel.service.dto.request.ChannelVideoGetServiceRequest;
import com.server.domain.channel.service.dto.response.ChannelVideoResponse;
import com.server.domain.video.controller.dto.request.VideoSort;
import com.server.domain.video.service.dto.response.VideoCategoryResponse;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.testhelper.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.server.global.testhelper.RestDocsUtil.pageResponseFields;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ChannelControllerTest extends ControllerTest {

    private final String BASE_URL = "/channels";

    @Test
    @DisplayName("채널의 공지사항 생성 API")
    void createAnnouncement() throws Exception {
        //given
        Long memberId = 1L;

        Long createdAnnouncementId = 1L;

        CreateAnnouncementApiRequest request = CreateAnnouncementApiRequest.builder()
                .content("announcement content")
                .build();

        given(announcementService
                .createAnnouncement(anyLong(), any(AnnouncementCreateServiceRequest.class)))
                .willReturn(createdAnnouncementId);

        //when
        ResultActions actions = mockMvc.perform(
                post(BASE_URL + "/{member-id}/announcements", memberId)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, TOKEN)
                        .content(objectMapper.writeValueAsString(request))
        );

        //then
        actions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/announcements/" + createdAnnouncementId));

        //restdocs
        actions.andDo(documentHandler.document(
                pathParameters(
                        parameterWithName("member-id").description("공지사항을 생성할 채널의 member ID")
                ),
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("Access Token")
                ),
                requestFields(
                        fieldWithPath("content").description("공지사항 내용")
                ),
                responseHeaders(
                        headerWithName("Location").description("생성된 공지사항의 URI")
                )
        ));

    }

    @Test
    @DisplayName("공지사항 목록 조회 API")
    void getAnnouncements() throws Exception {
        //given
        Long memberId = 1L;
        int page = 1;
        int size = 5;

        Page<AnnouncementResponse> pageResponses = createPage(
                createAnnouncementResponse(5),
                page - 1, size, 50);

        String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(pageResponses, "공지사항 목록 조회 성공"));

        given(announcementService.getAnnouncements(anyLong(), anyInt(), anyInt())).willReturn(pageResponses);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/{member-id}/announcements", memberId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .accept(APPLICATION_JSON)
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restdocs
        // restdocs
        actions.andDo(documentHandler.document(
                pathParameters(
                        parameterWithName("member-id").description("조회할 채널의 member ID")
                ),
                requestParameters(
                        parameterWithName("page").description("페이지 번호").optional(),
                        parameterWithName("size").description("페이지 사이즈").optional()
                ),
                pageResponseFields(
                        fieldWithPath("data").description("공지사항 목록"),
                        fieldWithPath("data[].announcementId").description("공지사항 ID"),
                        fieldWithPath("data[].content").description("공지사항 내용"),
                        fieldWithPath("data[].createdDate").description("공지사항 생성일")
                )
        ));
    }

    @Test
    @DisplayName("채널의 비디오 목록 조회 API")
    void getChannelVideos() throws Exception {
        //given
        Long memberId = 1L;
        int page = 1;
        int size = 8;
        String sort = "created-date";
        String category = "category";

        Page<ChannelVideoResponse> pageResponses =
                createPage(createChannelVideoResponse(8), page - 1, size, 50);

        String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(pageResponses, "채널 비디오 목록 조회 성공"));

        given(channelService.getChannelVideos(anyLong(), any(ChannelVideoGetServiceRequest.class)))
                .willReturn(pageResponses);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/{member-id}/videos", memberId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sort", sort)
                        .param("category", category)
                        .header(AUTHORIZATION, TOKEN)
                        .accept(APPLICATION_JSON)
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restdocs
        actions.andDo(
                documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰").optional()
                        ),
                        pathParameters(
                                parameterWithName("member-id").description("비디오 목록을 조회할 채널의 member ID")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호").optional(),
                                parameterWithName("size").description("페이지 사이즈").optional(),
                                parameterWithName("sort").description(generateLinkCode(VideoSort.class)).optional(),
                                parameterWithName("category").description("카테고리").optional()
                        ),
                        pageResponseFields(
                                fieldWithPath("data").description("비디오 목록"),
                                fieldWithPath("data[].videoId").description("비디오 ID"),
                                fieldWithPath("data[].videoName").description("비디오 이름"),
                                fieldWithPath("data[].thumbnailUrl").description("비디오 섬네일 URL"),
                                fieldWithPath("data[].views").description("비디오 조회수"),
                                fieldWithPath("data[].price").description("비디오 가격"),
                                fieldWithPath("data[].isPurchased").description("비디오 구매 여부"),
                                fieldWithPath("data[].categories").description("비디오 카테고리"),
                                fieldWithPath("data[].categories[].categoryId").description("카테고리 ID"),
                                fieldWithPath("data[].categories[].categoryName").description("카테고리 이름"),
                                fieldWithPath("data[].createdDate").description("비디오 생성일")
                        )
                )
        );
    }

    private List<ChannelVideoResponse> createChannelVideoResponse(int count) {
        List<ChannelVideoResponse> responses = new ArrayList<>();

        for(int i = 1; i <= count; i++) {
            ChannelVideoResponse response = ChannelVideoResponse.builder()
                    .videoId((long) i)
                    .videoName("video name" + i)
                    .thumbnailUrl("https://s3.ap-northeast-2.amazonaws.com/prometheus-images/" + i + "video name")
                    .views(1000)
                    .isPurchased(true)
                    .categories(createVideoCategoryResponse("category1", "category2"))
                    .createdDate(LocalDateTime.now())
                    .build();

            responses.add(response);
        }

        return responses;
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

    private List<AnnouncementResponse> createAnnouncementResponse(int count) {

        List<AnnouncementResponse> responses = new ArrayList<>();

        for(int i = 1; i <= count; i++) {
            AnnouncementResponse response = AnnouncementResponse.builder()
                    .announcementId((long) i)
                    .content("공지사항 내용" + i)
                    .createdDate(LocalDateTime.now())
                    .build();

            responses.add(response);
        }

        return responses;
    }
}