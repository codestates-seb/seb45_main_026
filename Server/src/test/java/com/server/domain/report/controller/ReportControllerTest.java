package com.server.domain.report.controller;

import com.server.domain.member.entity.MemberStatus;
import com.server.domain.report.controller.dto.request.MemberBlockApiRequest;
import com.server.domain.report.entity.ReportType;
import com.server.domain.report.service.dto.request.MemberBlockServiceRequest;
import com.server.domain.report.service.dto.response.*;
import com.server.domain.report.controller.dto.request.ReportSort;
import com.server.domain.video.entity.VideoStatus;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import com.server.global.testhelper.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.server.global.testhelper.RestDocsUtil.pageResponseFields;
import static com.server.global.testhelper.RestDocsUtil.singleResponseFields;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReportControllerTest extends ControllerTest {

    private final String BASE_URL = "/reports";

    @Test
    @DisplayName("비디오 신고 목록 조회 API")
    void getReportVideos() throws Exception {
        //given
        int page = 1;
        int size = 10;
        String sort = "last-reported-date";

        Page<VideoReportResponse> pageResponses = createPage(createVideoReportResponses(size), page, size, 100);

        String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(pageResponses, "비디오 신고 목록 조회 성공"));

        given(reportService.getReportVideos(anyInt(), anyInt(), anyString())).willReturn(pageResponses);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/videos")
                        .header(AUTHORIZATION, TOKEN)
                        .accept(APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sort", sort)
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restdocs
        actions.andDo(
                documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Access Token / 관리자만 가능")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호").optional(),
                                parameterWithName("size").description("페이지 크기").optional(),
                                parameterWithName("sort").description(generateLinkCode(ReportSort.class)).optional()
                        ),
                        pageResponseFields(
                                fieldWithPath("data").description("비디오 신고 목록"),
                                fieldWithPath("data[].videoId").description("비디오 ID"),
                                fieldWithPath("data[].videoName").description("비디오 제목"),
                                fieldWithPath("data[].videoStatus").description(generateLinkCode(VideoStatus.class)),
                                fieldWithPath("data[].reportCount").description("비디오 신고 횟수"),
                                fieldWithPath("data[].createdDate").description("비디오 생성일"),
                                fieldWithPath("data[].lastReportedDate").description("비디오 최근 신고일")
                        )
                )
        );
    }

    @Test
    @DisplayName("댓글 신고 목록 조회 API")
    void getReportReplies() throws Exception {
        //given
        int page = 1;
        int size = 10;
        String sort = "last-reported-date";

        Page<ReplyReportResponse> pageResponses = createPage(createReplyReportResponses(size), page, size, 100);

        String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(pageResponses, "댓글 신고 목록 조회 성공"));

        given(reportService.getReportReplies(anyInt(), anyInt(), anyString())).willReturn(pageResponses);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/replies")
                        .header(AUTHORIZATION, TOKEN)
                        .accept(APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sort", sort)
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restdocs
        actions.andDo(
                documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Access Token / 관리자만 가능")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호").optional(),
                                parameterWithName("size").description("페이지 크기").optional(),
                                parameterWithName("sort").description(generateLinkCode(ReportSort.class)).optional()
                        ),
                        pageResponseFields(
                                fieldWithPath("data").description("댓글 신고 목록"),
                                fieldWithPath("data[].videoId").description("댓글의 비디오 ID"),
                                fieldWithPath("data[].videoName").description("댓글의 비디오 제목"),
                                fieldWithPath("data[].replyId").description("신고된 댓글 id"),
                                fieldWithPath("data[].content").description("신고된 댓글 내용"),
                                fieldWithPath("data[].reportCount").description("댓글 신고 횟수"),
                                fieldWithPath("data[].createdDate").description("댓글 생성일"),
                                fieldWithPath("data[].lastReportedDate").description("댓글 최근 신고일")
                        )
                )
        );
    }

    @Test
    @DisplayName("채널 신고 목록 조회 API")
    void getReportChannels() throws Exception {
        //given
        int page = 1;
        int size = 10;
        String sort = "last-reported-date";

        Page<ChannelReportResponse> pageResponses = createPage(createChannelReportResponses(size), page, size, 100);

        String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(pageResponses, "채널 신고 목록 조회 성공"));

        given(reportService.getReportChannels(anyInt(), anyInt(), anyString())).willReturn(pageResponses);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/channels")
                        .header(AUTHORIZATION, TOKEN)
                        .accept(APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sort", sort)
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restdocs
        actions.andDo(
                documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Access Token / 관리자만 가능")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호").optional(),
                                parameterWithName("size").description("페이지 크기").optional(),
                                parameterWithName("sort").description(generateLinkCode(ReportSort.class)).optional()
                        ),
                        pageResponseFields(
                                fieldWithPath("data").description("채널 신고 목록"),
                                fieldWithPath("data[].memberId").description("신고된 채널의 멤버 ID"),
                                fieldWithPath("data[].channelName").description("신고된 채널 이름"),
                                fieldWithPath("data[].memberStatus").description(generateLinkCode(MemberStatus.class)),
                                fieldWithPath("data[].blockReason").description("차단 사유"),
                                fieldWithPath("data[].blockEndDate").description("차단 종료일"),
                                fieldWithPath("data[].reportCount").description("채널 신고 횟수"),
                                fieldWithPath("data[].createdDate").description("채널 생성일"),
                                fieldWithPath("data[].lastReportedDate").description("채널 최근 신고일")
                        )
                )
        );
    }

    @Test
    @DisplayName("공지사항 신고 목록 조회 API")
    void getReportAnnouncements() throws Exception {
        //given
        int page = 1;
        int size = 10;
        String sort = "last-reported-date";

        Page<AnnouncementReportResponse> pageResponses = createPage(createAnnouncementReportResponses(size), page, size, 100);

        String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(pageResponses, "공지사항 신고 목록 조회 성공"));

        given(reportService.getReportAnnouncements(anyInt(), anyInt(), anyString())).willReturn(pageResponses);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/announcements")
                        .header(AUTHORIZATION, TOKEN)
                        .accept(APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sort", sort)
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restdocs
        actions.andDo(
                documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Access Token / 관리자만 가능")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호").optional(),
                                parameterWithName("size").description("페이지 크기").optional(),
                                parameterWithName("sort").description(generateLinkCode(ReportSort.class)).optional()
                        ),
                        pageResponseFields(
                                fieldWithPath("data").description("공지사항 신고 목록"),
                                fieldWithPath("data[].announcementId").description("신고된 공지사항 ID"),
                                fieldWithPath("data[].content").description("신고된 공지사항 내용"),
                                fieldWithPath("data[].memberId").description("신고된 공지사항 채널의 멤버 ID"),
                                fieldWithPath("data[].reportCount").description("공지사항 신고 횟수"),
                                fieldWithPath("data[].createdDate").description("공지사항 생성일"),
                                fieldWithPath("data[].lastReportedDate").description("공지사항 최근 신고일")
                        )
                )
        );
    }

    @Test
    @DisplayName("비디오 신고 목록 세부 조회 API")
    void getReportVideoDetail() throws Exception {
        //given
        Long videoId = 1L;
        int page = 1;
        int size = 10;

        Page<ReportDetailResponse> pageResponses = createPage(createReportResponses(size), page, size, 100);

        String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(pageResponses, "비디오 신고 세부 내용 조회 성공"));

        given(reportService.getReportDetails(anyLong(), anyInt(), anyInt(), any(ReportType.class))).willReturn(pageResponses);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/videos/{video-id}", videoId)
                        .header(AUTHORIZATION, TOKEN)
                        .accept(APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restdocs
        actions.andDo(
                documentHandler.document(
                        pathParameters(
                                parameterWithName("video-id").description("비디오 ID")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Access Token / 관리자만 가능")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호").optional(),
                                parameterWithName("size").description("페이지 크기").optional()
                        ),
                        pageResponseFields(
                                fieldWithPath("data").description("신고 세부 내용"),
                                fieldWithPath("data[].reportId").description("신고 ID"),
                                fieldWithPath("data[].reportContent").description("신고 내용"),
                                fieldWithPath("data[].createdDate").description("신고 날짜"),
                                fieldWithPath("data[].memberId").description("신고자 ID"),
                                fieldWithPath("data[].email").description("신고자 이메일"),
                                fieldWithPath("data[].nickname").description("신고자 닉네임")
                        )
                )
        );
    }

    @Test
    @DisplayName("댓글 신고 목록 세부 조회 API")
    void getReportReplyDetail() throws Exception {
        //given
        Long replyId = 1L;
        int page = 1;
        int size = 10;

        Page<ReportDetailResponse> pageResponses = createPage(createReportResponses(size), page, size, 100);

        String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(pageResponses, "댓글 신고 세부 내용 조회 성공"));

        given(reportService.getReportDetails(anyLong(), anyInt(), anyInt(), any(ReportType.class))).willReturn(pageResponses);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/replies/{reply-id}", replyId)
                        .header(AUTHORIZATION, TOKEN)
                        .accept(APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restdocs
        actions.andDo(
                documentHandler.document(
                        pathParameters(
                                parameterWithName("reply-id").description("댓글 ID")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Access Token / 관리자만 가능")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호").optional(),
                                parameterWithName("size").description("페이지 크기").optional()
                        ),
                        pageResponseFields(
                                fieldWithPath("data").description("신고 세부 내용"),
                                fieldWithPath("data[].reportId").description("신고 ID"),
                                fieldWithPath("data[].reportContent").description("신고 내용"),
                                fieldWithPath("data[].createdDate").description("신고 날짜"),
                                fieldWithPath("data[].memberId").description("신고자 ID"),
                                fieldWithPath("data[].email").description("신고자 이메일"),
                                fieldWithPath("data[].nickname").description("신고자 닉네임")
                        )
                )
        );
    }

    @Test
    @DisplayName("채널 신고 목록 세부 조회 API")
    void getReportChannelDetail() throws Exception {
        //given
        Long channelId = 1L;
        int page = 1;
        int size = 10;

        Page<ReportDetailResponse> pageResponses = createPage(createReportResponses(size), page, size, 100);

        String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(pageResponses, "채널 신고 세부 내용 조회 성공"));

        given(reportService.getReportDetails(anyLong(), anyInt(), anyInt(), any(ReportType.class))).willReturn(pageResponses);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/channels/{channel-id}", channelId)
                        .header(AUTHORIZATION, TOKEN)
                        .accept(APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restdocs
        actions.andDo(
                documentHandler.document(
                        pathParameters(
                                parameterWithName("channel-id").description("채널 ID")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Access Token / 관리자만 가능")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호").optional(),
                                parameterWithName("size").description("페이지 크기").optional()
                        ),
                        pageResponseFields(
                                fieldWithPath("data").description("신고 세부 내용"),
                                fieldWithPath("data[].reportId").description("신고 ID"),
                                fieldWithPath("data[].reportContent").description("신고 내용"),
                                fieldWithPath("data[].createdDate").description("신고 날짜"),
                                fieldWithPath("data[].memberId").description("신고자 ID"),
                                fieldWithPath("data[].email").description("신고자 이메일"),
                                fieldWithPath("data[].nickname").description("신고자 닉네임")
                        )
                )
        );
    }

    @Test
    @DisplayName("공지사항 신고 목록 세부 조회 API")
    void getReportAnnouncementDetail() throws Exception {
        //given
        Long announcementId = 1L;
        int page = 1;
        int size = 10;

        Page<ReportDetailResponse> pageResponses = createPage(createReportResponses(size), page, size, 100);

        String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(pageResponses, "공지사항 신고 세부 내용 조회 성공"));

        given(reportService.getReportDetails(anyLong(), anyInt(), anyInt(), any(ReportType.class))).willReturn(pageResponses);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/announcements/{announcement-id}", announcementId)
                        .header(AUTHORIZATION, TOKEN)
                        .accept(APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restdocs
        actions.andDo(
                documentHandler.document(
                        pathParameters(
                                parameterWithName("announcement-id").description("공지사항 ID")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Access Token / 관리자만 가능")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호").optional(),
                                parameterWithName("size").description("페이지 크기").optional()
                        ),
                        pageResponseFields(
                                fieldWithPath("data").description("신고 세부 내용"),
                                fieldWithPath("data[].reportId").description("신고 ID"),
                                fieldWithPath("data[].reportContent").description("신고 내용"),
                                fieldWithPath("data[].createdDate").description("신고 날짜"),
                                fieldWithPath("data[].memberId").description("신고자 ID"),
                                fieldWithPath("data[].email").description("신고자 이메일"),
                                fieldWithPath("data[].nickname").description("신고자 닉네임")
                        )
                )
        );
    }

    @Test
    @DisplayName("멤버 차단 API")
    void blockMemberTrue() throws Exception {
        //given
        Long memberId = 1L;

        MemberBlockApiRequest request = new MemberBlockApiRequest(7, "차단 사유");

        given(reportService.blockMember(anyLong(), any(MemberBlockServiceRequest.class))).willReturn(true);

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(true, "회원 차단 성공"));

        //when
        ResultActions actions = mockMvc.perform(
                patch(BASE_URL + "/members/{member-id}", memberId)
                        .header(AUTHORIZATION, TOKEN)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restdocs
        setConstraintClass(MemberBlockApiRequest.class);

        actions.andDo(
                documentHandler.document(
                        pathParameters(
                                parameterWithName("member-id").description("차단할 회원 ID")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Access Token / 관리자만 가능")
                        ),
                        requestFields(
                                fieldWithPath("days").description("차단 일수").optional().attributes(getConstraint("days")),
                                fieldWithPath("blockReason").description("차단 사유").optional().attributes(getConstraint("blockReason"))
                        ),
                        singleResponseFields(
                                fieldWithPath("data").description("회원 차단 성공 여부")
                        )
                ));

    }

    @Test
    @DisplayName("멤버 해제 API")
    void blockMemberFalse() throws Exception {
        //given
        Long memberId = 1L;

        MemberBlockApiRequest request = new MemberBlockApiRequest(null, null);

        given(reportService.blockMember(anyLong(), any(MemberBlockServiceRequest.class))).willReturn(false);

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(false, "회원 차단 해제"));

        //when
        ResultActions actions = mockMvc.perform(
                patch(BASE_URL + "/members/{member-id}", memberId)
                        .header(AUTHORIZATION, TOKEN)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restdocs
        actions.andDo(
                documentHandler.document(
                        pathParameters(
                                parameterWithName("member-id").description("차단할 회원 ID")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Access Token / 관리자만 가능")
                        ),
                        singleResponseFields(
                                fieldWithPath("data").description("회원 차단 성공 여부")
                        )
                ));
    }

    private List<VideoReportResponse> createVideoReportResponses(int size) {

        List<VideoReportResponse> responses = new ArrayList<>();

        for (int i = 1; i <= size; i++) {
            VideoReportResponse response = VideoReportResponse.builder()
                    .videoId((long) i)
                    .videoName("비디오 제목")
                    .videoStatus(VideoStatus.CREATED)
                    .reportCount(2L)
                    .createdDate(LocalDateTime.now())
                    .lastReportedDate(LocalDateTime.now())
                    .build();

            responses.add(response);
        }

        return responses;
    }

    private List<ReplyReportResponse> createReplyReportResponses(int size) {

        List<ReplyReportResponse> responses = new ArrayList<>();

        for(int i = 1; i <= size; i++) {
            ReplyReportResponse response = ReplyReportResponse.builder()
                    .replyId((long) i)
                    .content("댓글 내용")
                    .videoId((long) i)
                    .videoName("비디오 제목")
                    .reportCount(2L)
                    .createdDate(LocalDateTime.now())
                    .lastReportedDate(LocalDateTime.now())
                    .build();

            responses.add(response);
        }

        return responses;
    }

    private List<ChannelReportResponse> createChannelReportResponses(int size) {

        List<ChannelReportResponse> responses = new ArrayList<>();

        for(int i = 1; i <= size; i++) {
            ChannelReportResponse response = ChannelReportResponse.builder()
                    .memberId((long) i)
                    .channelName("채널 이름")
                    .memberStatus(MemberStatus.BLOCKED)
                    .blockReason("차단 사유")
                    .blockEndDate(LocalDateTime.now())
                    .reportCount(2L)
                    .createdDate(LocalDateTime.now())
                    .lastReportedDate(LocalDateTime.now())
                    .build();

            responses.add(response);
        }

        return responses;
    }

    private List<AnnouncementReportResponse> createAnnouncementReportResponses(int size) {

        List<AnnouncementReportResponse> responses = new ArrayList<>();

        for(int i = 1; i <= size; i++) {
            AnnouncementReportResponse response = AnnouncementReportResponse.builder()
                    .announcementId((long) i)
                    .content("공지사항 내용")
                    .memberId((long) i)
                    .reportCount(2L)
                    .createdDate(LocalDateTime.now())
                    .lastReportedDate(LocalDateTime.now())
                    .build();

            responses.add(response);
        }

        return responses;
    }

    private List<ReportDetailResponse> createReportResponses(int size) {
        List<ReportDetailResponse> reportDetailResponse = new ArrayList<>();

        for (int i = 1; i <= size; i++) {
            reportDetailResponse.add(ReportDetailResponse.builder()
                    .reportId((long) i)
                    .reportContent("신고 내용")
                    .createdDate(LocalDateTime.now())
                    .memberId((long) i)
                    .email("reporter" + i + "@gmail.com")
                    .nickname("reporter" + i)
                    .build());
        }

        return reportDetailResponse;
    }
}