package com.server.domain.announcement.controller;

import com.server.domain.announcement.controller.dto.request.AnnouncementUpdateApiRequest;
import com.server.domain.announcement.service.dto.response.AnnouncementResponse;
import com.server.global.reponse.ApiSingleResponse;
import com.server.global.testhelper.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AnnouncementControllerTest extends ControllerTest {

    private final String BASE_URL = "/announcements";

    @Test
    @DisplayName("공지사항 단건 조회 API")
    void getAnnouncement() throws Exception {
        //given
        Long announcementId = 1L;

        AnnouncementResponse response = AnnouncementResponse.builder()
                .announcementId(announcementId)
                .content("공지사항 내용")
                .createdDate(LocalDateTime.now())
                .build();

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(response, "공지사항 조회 성공"));

        given(announcementService.getAnnouncement(anyLong())).willReturn(response);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL +"/{announcement-id}", announcementId)
                        .accept(APPLICATION_JSON)
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        // restdocs
        actions.andDo(documentHandler.document(
                pathParameters(
                        parameterWithName("announcement-id").description("조회할 공지사항의 ID")
                ),
                responseFields(
                        fieldWithPath("data.announcementId").description("공지사항 아이디"),
                        fieldWithPath("data.content").description("공지사항 내용"),
                        fieldWithPath("data.createdDate").description("공지사항 생성일"),
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("status").description("응답 상태"),
                        fieldWithPath("message").description("응답 메시지")
                )
        ));
    }

    @Test
    @DisplayName("공지사항 수정 API")
    void updateAnnouncement() throws Exception {
        //given
        Long announcementId = 1L;

        AnnouncementUpdateApiRequest request = AnnouncementUpdateApiRequest.builder()
                .content("공지사항 내용")
                .build();

        //when
        ResultActions actions = mockMvc.perform(
                patch(BASE_URL + "/{announcement-id}", announcementId)
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, TOKEN)
                        .content(objectMapper.writeValueAsString(request))
        );

        //then
        actions.andDo(print())
                .andExpect(status().isNoContent());

        // restdocs
        setConstraintClass(AnnouncementUpdateApiRequest.class);

        actions.andDo(documentHandler.document(
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("Access Token")
                ),
                pathParameters(
                        parameterWithName("announcement-id").description("수정할 공지사항의 ID")
                ),
                requestFields(
                        fieldWithPath("content").description("공지사항 내용")
                )
        ));
    }

    @Test
    @DisplayName("공지사항 삭제 API")
    void deleteAnnouncement() throws Exception {
        //given
        Long announcementId = 1L;

        //when
        ResultActions actions = mockMvc.perform(
                delete(BASE_URL + "/{announcement-id}", announcementId)
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        actions.andDo(print())
                .andExpect(status().isNoContent());

        // restdocs
        setConstraintClass(AnnouncementUpdateApiRequest.class);

        actions.andDo(documentHandler.document(
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("Access Token")
                ),
                pathParameters(
                        parameterWithName("announcement-id").description("삭제할 공지사항의 ID")
                )
        ));
    }
}