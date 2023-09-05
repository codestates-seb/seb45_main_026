package com.server.domain.channel.controller;

import com.server.domain.announcement.service.dto.request.AnnouncementCreateServiceRequest;
import com.server.domain.announcement.service.dto.response.AnnouncementResponse;
import com.server.domain.channel.controller.dto.request.CreateAnnouncementApiRequest;
import com.server.domain.channel.service.dto.ChannelInfo;
import com.server.domain.channel.service.dto.ChannelUpdate;
import com.server.domain.channel.service.dto.request.ChannelVideoGetServiceRequest;
import com.server.domain.channel.service.dto.response.ChannelVideoResponse;
import com.server.domain.video.controller.dto.request.VideoSort;
import com.server.domain.video.service.dto.response.VideoCategoryResponse;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import com.server.global.testhelper.ControllerTest;
import com.server.module.s3.service.dto.FileType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.server.global.testhelper.RestDocsUtil.pageResponseFields;
import static com.server.global.testhelper.RestDocsUtil.singleResponseFields;
import static org.junit.jupiter.api.DynamicTest.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ChannelControllerTest extends ControllerTest {

    private final String BASE_URL = "/channels";

    @Test
    @DisplayName("채널 조회 API")
    void getChannel() throws Exception {
        //given
        Long memberId = 1L;

        ChannelInfo response = ChannelInfo.builder()
                .memberId(memberId)
                .channelName("channel Name")
                .subscribers(1000)
                .isSubscribed(true)
                .description("channel description")
                .imageUrl("https://fsafasf.cloudfront.net/1/profile/sksjsksh")
                .createdDate(LocalDateTime.now())
                .build();

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(response, "채널 조회가 완료되었습니다"));

        given(channelService.getChannel(anyLong(), anyLong())).willReturn(response);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/{member-id}", memberId)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restdocs
        actions.andDo(documentHandler.document(
                pathParameters(
                        parameterWithName("member-id").description("조회할 채널의 member ID")
                ),
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("Access Token").optional()
                ),
                singleResponseFields(
                        fieldWithPath("data").description("채널 정보"),
                        fieldWithPath("data.memberId").description("채널의 member ID"),
                        fieldWithPath("data.channelName").description("채널의 이름"),
                        fieldWithPath("data.subscribers").description("채널의 구독자 수"),
                        fieldWithPath("data.isSubscribed").description("로그인 사용자의 채널의 구독 여부"),
                        fieldWithPath("data.description").description("채널의 설명"),
                        fieldWithPath("data.imageUrl").description("채널의 이미지 URL"),
                        fieldWithPath("data.createdDate").description("채널의 생성일")
                )
        ));
    }

    @Test
    @DisplayName("채널 수정 API")
    void updateChannelInfo() throws Exception {
        //given
        Long memberId = 1L;

        ChannelUpdate request = ChannelUpdate.builder()
                .channelName("channel Name")
                .description("channel description")
                .build();

        //when
        ResultActions actions = mockMvc.perform(
                patch(BASE_URL + "/{member-id}", memberId)
                        .header(AUTHORIZATION, TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        //then
        actions.andDo(print())
                .andExpect(status().isNoContent());

        //restdocs
        setConstraintClass(ChannelUpdate.class);

        actions.andDo(documentHandler.document(
                pathParameters(
                        parameterWithName("member-id").description("수정할 채널의 member ID")
                ),
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("Access Token")
                ),
                requestFields(
                        fieldWithPath("channelName").description("수정할 채널의 이름")
                                .attributes(getConstraint("channelName")).optional(),
                        fieldWithPath("description").description("수정할 채널의 설명")
                                .attributes(getConstraint("description")).optional()
                )
        ));
    }

    @Test
    @DisplayName("구독 업데이트 API - 구독을 할 때")
    void updateSubscribe() throws Exception {
        //given
        Long memberId = 1L;

        String apiResponse =
                objectMapper.writeValueAsString(ApiSingleResponse.ok(true, "구독상태가 업데이트되었습니다."));

        given(channelService.updateSubscribe(anyLong(), anyLong())).willReturn(true);

        //when
        ResultActions actions = mockMvc.perform(
                patch(BASE_URL + "/{member-id}/subscribe", memberId)
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse)
        );

        //restdocs
        actions.andDo(documentHandler.document(
                pathParameters(
                        parameterWithName("member-id").description("구독할 채널의 member ID")
                ),
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("Access Token")
                ),
                singleResponseFields(
                        fieldWithPath("data").description("업데이트 후 구독 상태")
                )
        ));
    }

    @Test
    @DisplayName("구독 업데이트 API - 구독을 취소할 때")
    void updateUnSubscribe() throws Exception {
        //given
        Long memberId = 1L;

        String apiResponse =
                objectMapper.writeValueAsString(ApiSingleResponse.ok(false, "구독상태가 업데이트되었습니다."));

        given(channelService.updateSubscribe(anyLong(), anyLong())).willReturn(false);

        //when
        ResultActions actions = mockMvc.perform(
                patch(BASE_URL + "/{member-id}/subscribe", memberId)
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse)
                );

        //restdocs
        actions.andDo(documentHandler.document(
                pathParameters(
                        parameterWithName("member-id").description("구독할 채널의 member ID")
                ),
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("Access Token")
                ),
                singleResponseFields(
                        fieldWithPath("data").description("업데이트 후 구독 상태")
                )
        ));
    }

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
                        .param("free", "false")
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
                                parameterWithName("category").description("카테고리").optional(),
                                parameterWithName("free").description("무료/유료 여부").optional()
                        ),
                        pageResponseFields(
                                fieldWithPath("data").description("비디오 목록"),
                                fieldWithPath("data[].videoId").description("비디오 ID"),
                                fieldWithPath("data[].videoName").description("비디오 이름"),
                                fieldWithPath("data[].thumbnailUrl").description("비디오 섬네일 URL"),
                                fieldWithPath("data[].views").description("비디오 조회수"),
                                fieldWithPath("data[].price").description("비디오 가격"),
                                fieldWithPath("data[].isPurchased").description("비디오 구매 여부"),
                                fieldWithPath("data[].description").description("비디오 설명"),
                                fieldWithPath("data[].categories").description("비디오 카테고리"),
                                fieldWithPath("data[].categories[].categoryId").description("카테고리 ID"),
                                fieldWithPath("data[].categories[].categoryName").description("카테고리 이름"),
                                fieldWithPath("data[].createdDate").description("비디오 생성일")
                        )
                )
        );
    }

    @TestFactory
    @DisplayName("채널 수정 validation 테스트")
    Collection<DynamicTest> updateChannelInfoValidation() {

        return List.of(
                dynamicTest("channelName 및 description 이 Null 이라도 검증에 통과한다.", ()-> {
                    //given
                    Long memberId = 1L;

                    ChannelUpdate request = ChannelUpdate.builder()
                            .build();

                    //when
                    ResultActions resultActions = mockMvc.perform(
                            patch(BASE_URL + "/{member-id}", memberId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    resultActions.andDo(print())
                            .andExpect(status().isNoContent())
                    ;
                }),
                dynamicTest("memberId 가 양수가 아닐 때 검증에 실패한다.", ()-> {
                    //given
                    Long invalidMemberId = -1L;

                    ChannelUpdate request = ChannelUpdate.builder()
                            .channelName("channel Name")
                            .description("channel description")
                            .build();

                    //when
                    ResultActions resultActions = mockMvc.perform(
                            patch(BASE_URL + "/{member-id}", invalidMemberId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("memberId"))
                            .andExpect(jsonPath("$.data[0].value").value(invalidMemberId))
                            .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
                }),
                dynamicTest("channelName 이 공백이면 검증에 실패한다.", ()-> {
                    //given
                    Long memberId = 1L;

                    ChannelUpdate request = ChannelUpdate.builder()
                            .channelName("")
                            .build();

                    //when
                    ResultActions resultActions = mockMvc.perform(
                            patch(BASE_URL + "/{member-id}", memberId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("channelName"))
                            .andExpect(jsonPath("$.data[0].value").value(""))
                            .andExpect(jsonPath("$.data[0].reason").value("허용된 글자 수는 1자에서 20자 입니다."));
                }),
                dynamicTest("channelName 이 21자 이상이면 검증에 실패한다.", ()-> {
                    //given
                    Long memberId = 1L;
                    String wrongChannelName = "123451234512345123451";

                    ChannelUpdate request = ChannelUpdate.builder()
                            .channelName(wrongChannelName)
                            .build();

                    //when
                    ResultActions resultActions = mockMvc.perform(
                            patch(BASE_URL + "/{member-id}", memberId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("channelName"))
                            .andExpect(jsonPath("$.data[0].value").value(wrongChannelName))
                            .andExpect(jsonPath("$.data[0].reason").value("허용된 글자 수는 1자에서 20자 입니다."));
                }),
                dynamicTest("channelName 에 특수문자가 있으면 검증에 실패한다.", ()-> {
                    //given
                    Long memberId = 1L;
                    String wrongChannelName = "!123";

                    ChannelUpdate request = ChannelUpdate.builder()
                            .channelName(wrongChannelName)
                            .build();

                    //when
                    ResultActions resultActions = mockMvc.perform(
                            patch(BASE_URL + "/{member-id}", memberId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("channelName"))
                            .andExpect(jsonPath("$.data[0].value").value(wrongChannelName))
                            .andExpect(jsonPath("$.data[0].reason").value("채널 이름은 한글, 영문, 숫자만 가능합니다."));
                })
        );
    }

    @Test
    @DisplayName("채널 조회 validation 테스트 - memberId 가 양수가 아닐 때 검증에 실패한다.")
    void getChannelValidation() throws Exception {
        //given
        Long invalidMemberId = -1L;

        //when
        ResultActions resultActions = mockMvc.perform(
                get(BASE_URL + "/{member-id}", invalidMemberId)
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data[0].field").value("memberId"))
                .andExpect(jsonPath("$.data[0].value").value("-1"))
                .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
    }

    @Test
    @DisplayName("구독 업데이트 validation 테스트 - memberId 가 양수가 아닐 때 검증에 실패한다.")
    void updateSubscribeValidation() throws Exception {
        //given
        Long invalidMemberId = -1L;

        //when
        ResultActions resultActions = mockMvc.perform(
                patch(BASE_URL + "/{member-id}/subscribe", invalidMemberId)
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        resultActions.andDo(print())
                .andExpect(jsonPath("$.data[0].field").value("memberId"))
                .andExpect(jsonPath("$.data[0].value").value("-1"))
                .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
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

    @TestFactory
    @DisplayName("채널 내 비디오 조회 validation 테스트")
    Collection<DynamicTest> getChannelVideosValidation() {

        Long memberId = 1L;

        return List.of(
                dynamicTest("memberId 가 양수가 아니면 검증에 실패한다.", ()-> {
                    //given
                    Long invalidMemberId = -1L;

                    //when
                    ResultActions resultActions = mockMvc.perform(
                            get(BASE_URL + "/{member-id}/videos", invalidMemberId)
                                    .header(AUTHORIZATION, TOKEN)
                    );

                    //then
                    resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("memberId"))
                            .andExpect(jsonPath("$.data[0].value").value("-1"))
                            .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
                }),
                dynamicTest("page 가 양수가 아니면 검증에 실패한다.", ()-> {
                    //when
                    ResultActions resultActions = mockMvc.perform(
                            get(BASE_URL + "/{member-id}/videos", memberId)
                                    .param("page", "-1")
                                    .header(AUTHORIZATION, TOKEN)
                    );

                    //then
                    resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("page"))
                            .andExpect(jsonPath("$.data[0].value").value("-1"))
                            .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
                }),
                dynamicTest("size 가 양수가 아니면 검증에 실패한다", ()-> {
                    //when
                    ResultActions resultActions = mockMvc.perform(
                            get(BASE_URL + "/{member-id}/videos", memberId)
                                    .param("size", "-1")
                                    .header(AUTHORIZATION, TOKEN)
                    );

                    //then
                    resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("size"))
                            .andExpect(jsonPath("$.data[0].value").value("-1"))
                            .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
                })
        );
    }

    @TestFactory
    @DisplayName("공지사항 생성 시 validation 테스트")
    Collection<DynamicTest> createAnnouncementValidation() {
        //given
        Long memberId = 1L;

        given(announcementService
                .createAnnouncement(anyLong(), any(AnnouncementCreateServiceRequest.class)))
                .willReturn(1L);

        return List.of(
                dynamicTest("memberId 가 양수가 아니면 검증에 실패한다.", ()-> {
                    //given
                    Long invalidMemberId = 0L;

                    CreateAnnouncementApiRequest request = CreateAnnouncementApiRequest.builder()
                            .content("announcement content")
                            .build();

                    //when
                    ResultActions resultActions = mockMvc.perform(
                            post(BASE_URL + "/{member-id}/announcements", invalidMemberId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("memberId"))
                            .andExpect(jsonPath("$.data[0].value").value(invalidMemberId))
                            .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
                }),
                dynamicTest("공지사항 내용이 null 이면 검증에 실패한다.", ()-> {
                    //given
                    CreateAnnouncementApiRequest request = CreateAnnouncementApiRequest.builder()
                            .build();

                    //when
                    ResultActions resultActions = mockMvc.perform(
                            post(BASE_URL + "/{member-id}/announcements", memberId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("content"))
                            .andExpect(jsonPath("$.data[0].value").value("null"))
                            .andExpect(jsonPath("$.data[0].reason").value("공지사항 내용은 필수입니다."));
                }),
                dynamicTest("공지사항 내용이 공백이면 검증에 실패한다.", ()-> {
                    //given
                    Long invalidMemberId = 0L;

                    CreateAnnouncementApiRequest request = CreateAnnouncementApiRequest.builder()
                            .content("")
                            .build();

                    //when
                    ResultActions resultActions = mockMvc.perform(
                            post(BASE_URL + "/{member-id}/announcements", memberId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    resultActions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("content"))
                            .andExpect(jsonPath("$.data[0].value").value(""))
                            .andExpect(jsonPath("$.data[0].reason").value("공지사항 내용은 필수입니다."));
                })
        );
    }
    
    @TestFactory
    @DisplayName("공지사항 조회 시 validation 테스트")
    Collection<DynamicTest> getAnnouncementsValidation() {
        //given
        Long memberId = 1L;
    
        return List.of(
                dynamicTest("memberId 가 양수가 아니면 검증에 실패한다.", ()-> {
                    //given
                    Long invalidMemberId = 0L;

                    //when
                    ResultActions actions = mockMvc.perform(
                            get(BASE_URL + "/{member-id}/announcements", invalidMemberId)
                                    .accept(APPLICATION_JSON)
                    );

                    //then
                    actions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("memberId"))
                            .andExpect(jsonPath("$.data[0].value").value(invalidMemberId))
                            .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
                }),
                dynamicTest("page 가 양수가 아니면 검증에 실패한다.", ()-> {
                    //given
                    int page = 0;

                    //when
                    ResultActions actions = mockMvc.perform(
                            get(BASE_URL + "/{member-id}/announcements", memberId)
                                    .param("page", String.valueOf(page))
                                    .accept(APPLICATION_JSON)
                    );

                    //then
                    actions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("page"))
                            .andExpect(jsonPath("$.data[0].value").value(page))
                            .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
                }),dynamicTest("size 가 양수가 아니면 검증에 실패한다.", ()-> {
                    //given
                    int size = 0;

                    //when
                    ResultActions actions = mockMvc.perform(
                            get(BASE_URL + "/{member-id}/announcements", memberId)
                                    .param("size", String.valueOf(size))
                                    .accept(APPLICATION_JSON)
                    );

                    //then
                    actions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("size"))
                            .andExpect(jsonPath("$.data[0].value").value(size))
                            .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
                })
        );
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