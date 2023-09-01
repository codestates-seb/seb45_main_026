package com.server.domain.channel.controller;

import com.server.domain.announcement.service.dto.request.AnnouncementCreateServiceRequest;
import com.server.domain.announcement.service.dto.response.AnnouncementResponse;
import com.server.domain.channel.service.dto.ChannelResponse;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.testhelper.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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

        ChannelResponse.CreateAnnouncementApiRequest request = ChannelResponse.CreateAnnouncementApiRequest.builder()
                .content("announcement content")
                .build();

        given(announcementService
                .createAnnouncement(anyLong(), ArgumentMatchers.any(AnnouncementCreateServiceRequest.class)))
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
                responseFields(
                        fieldWithPath("data").description("공지사항 목록"),
                        fieldWithPath("data[].announcementId").description("공지사항 ID"),
                        fieldWithPath("data[].content").description("공지사항 내용"),
                        fieldWithPath("data[].createdDate").description("공지사항 생성일"),
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