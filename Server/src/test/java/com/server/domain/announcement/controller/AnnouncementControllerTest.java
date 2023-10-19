package com.server.domain.announcement.controller;

import com.server.domain.announcement.controller.dto.request.AnnouncementUpdateApiRequest;
import com.server.domain.announcement.service.dto.response.AnnouncementResponse;
import com.server.domain.report.controller.dto.request.ReportCreateApiRequest;
import com.server.global.reponse.ApiSingleResponse;
import com.server.global.testhelper.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static com.server.global.testhelper.RestDocsUtil.singleResponseFields;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class AnnouncementControllerTest extends ControllerTest {

    private final String BASE_URL = "/announcements";

    @Test
    @DisplayName("공지사항 단건 조회 API")
    void getAnnouncement() throws Exception {
        //given
        Long announcementId = 1L;

        AnnouncementResponse response = getAnnouncementResponse(announcementId);

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

    private AnnouncementResponse getAnnouncementResponse(Long announcementId) {
        return AnnouncementResponse.builder()
                .announcementId(announcementId)
                .content("공지사항 내용")
                .createdDate(LocalDateTime.now())
                .build();
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

    @Test
    @DisplayName("공지사항 최초 신고 API")
    void reportAnnouncement() throws Exception {
        //given
        Long announcementId = 1L;

        ReportCreateApiRequest request = ReportCreateApiRequest.builder()
                .reportContent("신고 내용")
                .build();

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(true, "공지사항 신고 성공"));

        given(announcementService.reportAnnouncement(anyLong(), anyLong(), anyString())).willReturn(true);

        //when
        ResultActions actions = mockMvc.perform(
                post(BASE_URL + "/{announcement-id}/reports", announcementId)
                        .header(AUTHORIZATION, TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restdocs
        actions
                .andDo(documentHandler.document(
                        pathParameters(
                                parameterWithName("announcement-id").description("신고할 공지사항 ID")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                fieldWithPath("reportContent").description("신고 내용")
                        ),
                        singleResponseFields(
                                fieldWithPath("data").description("공지사항 신고 성공 여부")
                        )
                ));
    }

    @TestFactory
    @DisplayName("공지사항 단건 조회 validation 테스트")
    Collection<DynamicTest> getAnnouncementValidation() {
        //given
        Long announcementId = 1L;

        return List.of(
                dynamicTest("공지사항 id 가 양수가 아니면 검증에 실패한다.", ()-> {
                    //given
                    Long wrongAnnouncementId = 0L;

                    //when
                    ResultActions actions = mockMvc.perform(
                            get(BASE_URL +"/{announcement-id}", wrongAnnouncementId)
                                    .accept(APPLICATION_JSON)
                    );

                    //then
                    actions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("announcementId"))
                            .andExpect(jsonPath("$.data[0].value").value(wrongAnnouncementId))
                            .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
                })
        );
    }

    @TestFactory
    @DisplayName("공지사항 수정 validation 테스트")
    Collection<DynamicTest> updateAnnouncementValidation() {
        //given
        Long announcementId = 1L;

        return List.of(
                dynamicTest("공지사항 id 가 양수가 아니면 검증에 실패한다.", ()-> {
                    //given
                    Long wrongAnnouncementId = 0L;

                    AnnouncementUpdateApiRequest request = AnnouncementUpdateApiRequest.builder()
                            .content("공지사항 내용")
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(
                            patch(BASE_URL + "/{announcement-id}", wrongAnnouncementId)
                                    .contentType(APPLICATION_JSON)
                                    .header(AUTHORIZATION, TOKEN)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    actions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("announcementId"))
                            .andExpect(jsonPath("$.data[0].value").value(wrongAnnouncementId))
                            .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
                }),
                dynamicTest("공지사항 내용이 null 이면 검증에 실패한다.", ()-> {
                    //given
                    AnnouncementUpdateApiRequest request = AnnouncementUpdateApiRequest.builder()
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
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("content"))
                            .andExpect(jsonPath("$.data[0].value").value("null"))
                            .andExpect(jsonPath("$.data[0].reason").value("공지사항 내용은 필수입니다."));
                }),
                dynamicTest("공지사항 내용이 공백이면 검증에 실패한다.", ()-> {
                    //given
                    String wrongContent = " ";

                    AnnouncementUpdateApiRequest request = AnnouncementUpdateApiRequest.builder()
                            .content(wrongContent)
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
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("content"))
                            .andExpect(jsonPath("$.data[0].value").value(wrongContent))
                            .andExpect(jsonPath("$.data[0].reason").value("공지사항 내용은 필수입니다."));
                })
        );
    }

    @Test
    @DisplayName("공지사항 삭제 validation 테스트 - 공지사항 id 가 양수가 아니면 검증에 실패한다.")
    void deleteAnnouncementValidation() throws Exception {
        //given
        Long wrongAnnouncementId = 0L;

        //when
        ResultActions actions = mockMvc.perform(
                delete(BASE_URL + "/{announcement-id}", wrongAnnouncementId)
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data[0].field").value("announcementId"))
                .andExpect(jsonPath("$.data[0].value").value(wrongAnnouncementId))
                .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));

    }
}