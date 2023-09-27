package com.server.domain.report.controller;

import com.server.domain.member.entity.MemberStatus;
import com.server.domain.report.service.dto.response.AdminMemberResponse;
import com.server.domain.report.service.dto.response.AdminVideoResponse;
import com.server.domain.video.entity.VideoStatus;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminControllerTest extends ControllerTest {

    final String BASE_URL = "/admin";

    @Test
    @DisplayName("멤버 목록 조회 API")
    void getMembers() throws Exception {
        //given
        String keyword = "키워드";
        int page = 1;
        int size = 5;

        Page<AdminMemberResponse> pageResponses = createPage(createAdminMemberResponses(size), page, size, 100);

        given(reportService.getMembers(anyString(), anyInt(), anyInt())).willReturn(pageResponses);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/members")
                        .header(AUTHORIZATION, TOKEN)
                        .accept(APPLICATION_JSON)
                        .param("keyword", keyword)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(ApiPageResponse.ok(pageResponses, "멤버 목록 조회 성공"))));

        //restDocs
        actions.andDo(
                documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Access Token / 관리자만 가능")
                        ),
                        requestParameters(
                                parameterWithName("keyword").description("검색 키워드 / 이메일, 닉네임, 채널명 통합 검색"),
                                parameterWithName("page").description("페이지 번호").optional(),
                                parameterWithName("size").description("페이지 크기").optional()
                        ),
                        pageResponseFields(
                                fieldWithPath("data").description("멤버 목록"),
                                fieldWithPath("data[].memberId").description("멤버 ID"),
                                fieldWithPath("data[].email").description("멤버 이메일"),
                                fieldWithPath("data[].nickname").description("멤버 닉네임"),
                                fieldWithPath("data[].memberStatus").description(generateLinkCode(MemberStatus.class)),
                                fieldWithPath("data[].channelName").description("채널명"),
                                fieldWithPath("data[].blockReason").description("차단 사유"),
                                fieldWithPath("data[].blockEndDate").description("차단 종료일"),
                                fieldWithPath("data[].createdDate").description("가입일")
                        )
                )
        );
    }

    @Test
    @DisplayName("관리자용 비디오 목록 조회 API")
    void getVideos() throws Exception {
        //given
        String email = "test@test.com";
        String keyword = "키워드";
        int page = 1;
        int size = 5;

        Page<AdminVideoResponse> pageResponses = createPage(createAdminVideoResponses(size), page, size, 100);

        given(reportService.getVideos(anyString(), anyString(), anyInt(), anyInt())).willReturn(pageResponses);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/videos")
                        .header(AUTHORIZATION, TOKEN)
                        .accept(APPLICATION_JSON)
                        .param("email", email)
                        .param("keyword", keyword)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(ApiPageResponse.ok(pageResponses, "비디오 목록 조회 성공"))));

        //restDocs
        actions.andDo(
                documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Access Token / 관리자만 가능")
                        ),
                        requestParameters(
                                parameterWithName("email").description("멤버 이메일").optional(),
                                parameterWithName("keyword").description("비디오명 검색").optional(),
                                parameterWithName("page").description("페이지 번호").optional(),
                                parameterWithName("size").description("페이지 크기").optional()
                        ),
                        pageResponseFields(
                                fieldWithPath("data").description("비디오 목록"),
                                fieldWithPath("data[].videoId").description("비디오 ID"),
                                fieldWithPath("data[].videoName").description("비디오 제목"),
                                fieldWithPath("data[].videoStatus").description(generateLinkCode(VideoStatus.class)),
                                fieldWithPath("data[].memberId").description("멤버 ID"),
                                fieldWithPath("data[].email").description("멤버 이메일"),
                                fieldWithPath("data[].channelName").description("멤버의 채널 이름"),
                                fieldWithPath("data[].createdDate").description("비디오 등록일")
                        )
                )
        );

    }

    private List<AdminMemberResponse> createAdminMemberResponses(int size) {
        List<AdminMemberResponse> responses = new ArrayList<>();

        for(int i = 1; i <= size; i++) {

            String blockReason = "없음";
            LocalDateTime blockEndDate = LocalDateTime.now();
            MemberStatus memberStatus = MemberStatus.ACTIVE;

            if(i % 2 == 0) {
                blockReason = "차단 사유";
                blockEndDate = LocalDateTime.now().plusDays(7);
                memberStatus = MemberStatus.BLOCKED;
            }

            AdminMemberResponse response = AdminMemberResponse.builder()
                    .memberId((long) i)
                    .email("test" + i + "@test.com")
                    .nickname("테스트" + i)
                    .memberStatus(memberStatus)
                    .channelName("채널" + i)
                    .blockReason(blockReason)
                    .blockEndDate(blockEndDate)
                    .createdDate(LocalDateTime.now())
                    .build();

            responses.add(response);
        }

        return responses;
    }

    private List<AdminVideoResponse> createAdminVideoResponses(int size) {
        List<AdminVideoResponse> responses = new ArrayList<>();

        for(int i = 1; i <= size; i++) {

            AdminVideoResponse response = AdminVideoResponse.builder()
                    .videoId((long) i)
                    .videoName("비디오 제목")
                    .videoStatus(VideoStatus.CREATED)
                    .memberId((long) i)
                    .email("test" + i + "@test.com")
                    .channelName("채널" + i)
                    .createdDate(LocalDateTime.now())
                    .build();

            responses.add(response);
        }

        return responses;
    }
}