package com.server.domain.video.controller;

import com.server.domain.answer.entity.AnswerStatus;
import com.server.domain.question.service.dto.response.QuestionResponse;
import com.server.domain.video.controller.dto.request.*;
import com.server.domain.video.service.dto.request.VideoCreateServiceRequest;
import com.server.domain.video.service.dto.request.VideoCreateUrlServiceRequest;
import com.server.domain.video.service.dto.request.VideoGetServiceRequest;
import com.server.domain.video.service.dto.response.*;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import com.server.global.testhelper.ControllerTest;
import com.server.module.s3.service.dto.ImageType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VideoControllerTest extends ControllerTest {

    private final String BASE_URL = "/videos";

    @Test
    @DisplayName("비디오의 전체 문제 조회 API")
    void getQuestions() throws Exception {
        //given
        Long videoId = 1L;

        List<QuestionResponse> responses = createQuestionResponses(5);

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(responses, "문제 목록 조회 성공"));

        given(questionService.getQuestions(anyLong(), anyLong()))
                .willReturn(responses);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/{video-id}/questions", videoId)
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restDocs
        // restdocs
        actions.andDo(documentHandler.document(
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                ),
                pathParameters(
                        parameterWithName("video-id").description("문제를 조회할 비디오 ID")
                ),
                responseFields(
                        fieldWithPath("data").description("문제 리스트"),
                        fieldWithPath("data[].questionId").description("문제 ID"),
                        fieldWithPath("data[].position").description("문제 순서"),
                        fieldWithPath("data[].content").description("문제 내용"),
                        fieldWithPath("data[].myAnswer").description("나의 답변"),
                        fieldWithPath("data[].questionAnswer").description("정답"),
                        fieldWithPath("data[].answerStatus").description(generateLinkCode(AnswerStatus.class)),
                        fieldWithPath("data[].description").description("문제에 대한 답변 설명"),
                        fieldWithPath("data[].selections").description("문제에 대한 선택지"),
                        fieldWithPath("data[].solvedDate").description("문제 풀이 날짜"),
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("status").description("응답 상태"),
                        fieldWithPath("message").description("응답 메시지")
                )
        ));
    }

    @Test
    @DisplayName("비디오의 전체 문제 풀기 API")
    void solveQuestions() throws Exception {
        //given
        Long videoId = 1L;

        List<String> myAnswers = List.of("apple", "1", "3", "banana", "2");
        AnswersCreateApiRequest request = new AnswersCreateApiRequest(myAnswers);

        List<Boolean> results = List.of(true, true, true, false, false);
        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(results, "문제 제출 성공"));

        given(questionService.solveQuestions(anyLong(), anyLong(), anyList()))
                .willReturn(results);

        //when
        ResultActions actions = mockMvc.perform(
                post(BASE_URL + "/{video-id}/answers", videoId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restDocs
        setConstraintClass(AnswersCreateApiRequest.class);

        actions
                .andDo(documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("myAnswers").description("나의 답변 리스트").attributes(getConstraint("myAnswers"))
                        ),
                        responseFields(
                                fieldWithPath("data").description("문제 풀이 결과"),
                                fieldWithPath("data[]").description("문제 풀이 결과"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("비디오의 문제 전체 생성 API")
    void createQuestions() throws Exception {
        //given
        Long videoId = 1L;

        List<QuestionCreateApiRequest> requests = createQuestionCreateApiRequests(5);

        List<Long> createdIds = List.of(1L, 2L, 3L, 4L, 5L);

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.of(createdIds, CREATED, "문제 생성 성공"));

        given(questionService.createQuestions(anyLong(), anyLong(), anyList()))
                .willReturn(createdIds);

        //when
        ResultActions actions = mockMvc.perform(
                post(BASE_URL + "/{video-id}/questions", videoId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests))
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(apiResponse));

        //restDocs
        setConstraintClass(QuestionCreateApiRequest.class);

        actions
                .andDo(documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("[]").description("문제 생성 요청 리스트"),
                                fieldWithPath("[].content").description("문제 내용").attributes(getConstraint("content")),
                                fieldWithPath("[].position").description("문제 순서").attributes(getConstraint("position")),
                                fieldWithPath("[].questionAnswer").description("정답").attributes(getConstraint("questionAnswer")),
                                fieldWithPath("[].description").description("문제에 대한 답변 설명").attributes(getConstraint("description")),
                                fieldWithPath("[].selections").description("문제에 대한 선택지").attributes(getConstraint("selections"))
                        ),
                        responseFields(
                                fieldWithPath("data").description("문제 ID 리스트"),
                                fieldWithPath("data[]").description("문제 ID"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
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

        given(videoService.getVideos(anyLong(), any(VideoGetServiceRequest.class)))
                .willReturn(pageResponses);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sort", "created-date")
                        .param("category", "spring")
                        .param("subscribe", "true")
                        .param("free", "false")
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
                        parameterWithName("page").description("페이지 번호").optional(),
                        parameterWithName("size").description("페이지 사이즈").optional(),
                        parameterWithName("sort").description(generateLinkCode(VideoSort.class)).optional(),
                        parameterWithName("category").description("카테고리").optional(),
                        parameterWithName("subscribe").description("구독 여부").optional(),
                        parameterWithName("free").description("무료 여부").optional()
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
    void getVideo() throws Exception {
        //given
        Long videoId = 1L;

        VideoDetailResponse response = createVideoResponse(videoId);

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(response, "비디오 조회 성공"));

        given(videoService.getVideo(anyLong(), anyLong())).willReturn(response);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/{video-id}", videoId)
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restDocs
        actions
                .andDo(documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Access Token").optional()
                        ),
                        pathParameters(
                                parameterWithName("video-id").description("조회할 비디오 ID")
                        ),
                        responseFields(
                                fieldWithPath("data").description("비디오 정보"),
                                fieldWithPath("data.videoId").description("비디오 ID"),
                                fieldWithPath("data.videoName").description("비디오 제목"),
                                fieldWithPath("data.description").description("비디오 설명"),
                                fieldWithPath("data.thumbnailUrl").description("섬네일 URL"),
                                fieldWithPath("data.videoUrl").description("비디오 URL"),
                                fieldWithPath("data.views").description("조회 수"),
                                fieldWithPath("data.star").description("별점"),
                                fieldWithPath("data.price").description("가격"),
                                fieldWithPath("data.reward").description("리워드"),
                                fieldWithPath("data.isPurchased").description("구매 여부"),
                                fieldWithPath("data.isReplied").description("댓글 여부"),
                                fieldWithPath("data.categories").description("카테고리 목록"),
                                fieldWithPath("data.categories[].categoryId").description("카테고리 ID"),
                                fieldWithPath("data.categories[].categoryName").description("카테고리 이름"),
                                fieldWithPath("data.channel").description("채널 정보"),
                                fieldWithPath("data.channel.memberId").description("채널의 member ID"),
                                fieldWithPath("data.channel.channelName").description("채널 이름"),
                                fieldWithPath("data.channel.subscribes").description("구독자 수"),
                                fieldWithPath("data.channel.isSubscribed").description("채널 구독 여부"),
                                fieldWithPath("data.channel.imageUrl").description("채널 프로필 이미지 URL"),
                                fieldWithPath("data.createdDate").description("비디오 생성일"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("비디오 생성 URL 조회 API")
    void getVideoCreateUrl() throws Exception {
        //given
        VideoCreateUrlApiRequest request = VideoCreateUrlApiRequest.builder()
                .imageType(ImageType.JPG)
                .fileName("videoName")
                .build();

        VideoCreateUrlResponse response = VideoCreateUrlResponse.builder()
                .videoUrl("https://s3.ap-northeast-2.amazonaws.com/prometheus-videos/1")
                .thumbnailUrl("https://s3.ap-northeast-2.amazonaws.com/prometheus-images/1")
                .build();

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(response, "put url 생성 성공"));

        given(videoService.getVideoCreateUrl(anyLong(), any(VideoCreateUrlServiceRequest.class))).willReturn(response);

        //when
        ResultActions actions = mockMvc.perform(
                post(BASE_URL + "/presigned-url")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, TOKEN)
                        .content(objectMapper.writeValueAsString(request))
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restDocs
        setConstraintClass(VideoCreateUrlApiRequest.class);

        actions
                .andDo(documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                fieldWithPath("imageType").description(generateLinkCode(ImageType.class)).attributes(getConstraint("imageType")),
                                fieldWithPath("fileName").description("파일 이름").attributes(getConstraint("fileName"))
                        ),
                        responseFields(
                                fieldWithPath("data").description("put url 정보"),
                                fieldWithPath("data.videoUrl").description("비디오 put url"),
                                fieldWithPath("data.thumbnailUrl").description("섬네일 put url"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("비디오 생성 API")
    void createVideo() throws Exception {
        //given
        Long videoId = 1L;

        VideoCreateApiRequest request = VideoCreateApiRequest.builder()
                .videoName("videoName")
                .price(10000)
                .description("description")
                .categories(List.of("java", "react"))
                .build();

        given(videoService.createVideo(anyLong(), any(VideoCreateServiceRequest.class))).willReturn(videoId);

        //when
        ResultActions actions = mockMvc.perform(
                post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, TOKEN)
                        .content(objectMapper.writeValueAsString(request))
        );

        //then
        actions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/videos/" + videoId));

        //restdocs
        setConstraintClass(VideoCreateApiRequest.class);

        actions.andDo(
                documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                fieldWithPath("videoName").description("비디오 제목").attributes(getConstraint("videoName")),
                                fieldWithPath("price").description("가격").attributes(getConstraint("price")),
                                fieldWithPath("description").description("비디오 설명").attributes(getConstraint("description")),
                                fieldWithPath("categories").description("카테고리 목록").attributes(getConstraint("categories"))
                        ),
                        responseHeaders(
                                headerWithName("Location").description("생성된 비디오 URL")
                        )
                )
        );
    }

    @Test
    @DisplayName("비디오 수정 API")
    void updateVideo() throws Exception {
        //given
        Long videoId = 1L;

        VideoUpdateApiRequest request = VideoUpdateApiRequest.builder()
                .description("description")
                .build();

        //when
        ResultActions actions = mockMvc.perform(
                patch(BASE_URL + "/{videoId}", videoId)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION, TOKEN)
                        .content(objectMapper.writeValueAsString(request))
        );

        //then
        actions.andDo(print())
                .andExpect(status().isNoContent());

        //restdocs
        setConstraintClass(VideoUpdateApiRequest.class);

        actions.andDo(
                documentHandler.document(
                        pathParameters(
                                parameterWithName("videoId").description("수정할 비디오 ID")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                fieldWithPath("description").description("비디오 설명").attributes(getConstraint("description"))
                        )
                )
        );
    }

    @Test
    @DisplayName("장바구니 담기 API")
    void changeCartAdd() throws Exception {
        //given
        Long videoId = 1L;

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(true, "장바구니 담기 성공"));

        given(videoService.changeCart(anyLong(), anyLong())).willReturn(true);

        //when
        ResultActions actions = mockMvc.perform(
                patch(BASE_URL + "/{videoId}/carts", videoId)
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restdocs
        actions.andDo(
                documentHandler.document(
                        pathParameters(
                                parameterWithName("videoId").description("장바구니에 담을 비디오 ID")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Access Token")
                        ),
                        responseFields(
                                fieldWithPath("data").description("장바구니 담기 여부"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                )
        );
    }

    @Test
    @DisplayName("장바구니 취소 API")
    void changeCartCancel() throws Exception {
        //given
        Long videoId = 1L;

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(false, "장바구니 취소 성공"));

        given(videoService.changeCart(anyLong(), anyLong())).willReturn(false);

        //when
        ResultActions actions = mockMvc.perform(
                patch(BASE_URL + "/{videoId}/carts", videoId)
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        actions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restdocs
        actions.andDo(
                documentHandler.document(
                        pathParameters(
                                parameterWithName("videoId").description("장바구니 취소할 비디오 ID")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Access Token")
                        ),
                        responseFields(
                                fieldWithPath("data").description("장바구니 취소 여부"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                )
        );
    }

    @Test
    @DisplayName("비디오 삭제 API")
    void deleteVideo() throws Exception {
        //given
        Long videoId = 1L;

        //when
        ResultActions actions = mockMvc.perform(
                delete(BASE_URL + "/{videoId}", videoId)
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        actions.andDo(print())
                .andExpect(status().isNoContent())
        ;

        //restdocs
        actions.andDo(
                documentHandler.document(
                        pathParameters(
                                parameterWithName("videoId").description("삭제할 비디오 ID")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Access Token")
                        )
                )
        );
    }

    private List<QuestionResponse> createQuestionResponses(int size) {

        List<QuestionResponse> responses = new ArrayList<>();

        for (int i = 1; i <= size; i++) {
            responses.add(QuestionResponse.builder()
                    .questionId((long) i)
                    .position(i)
                    .content("content" + i)
                    .myAnswer(String.valueOf(i))
                    .questionAnswer("2")
                    .selections(List.of("selection1", "selection2", "selection3", "selection4", "selection5"))
                    .answerStatus(AnswerStatus.CORRECT)
                    .description("description" + i)
                    .solvedDate(LocalDateTime.now())
                    .build());
        }

        return responses;
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

    private List<QuestionCreateApiRequest> createQuestionCreateApiRequests(int count) {

        List<QuestionCreateApiRequest> requests = new ArrayList<>();

        for(int i = 1; i <= count; i++) {
            QuestionCreateApiRequest request = QuestionCreateApiRequest.builder()
                    .position(i)
                    .content("content" + i)
                    .questionAnswer("answer" + i)
                    .description("description" + i)
                    .selections(List.of("selection1", "selection2", "selection3"))
                    .build();

            requests.add(request);
        }

        return requests;
    }

    private VideoDetailResponse createVideoResponse(Long videoId) {
        return VideoDetailResponse.builder()
                .videoId(videoId)
                .videoName("videoName")
                .description("description")
                .thumbnailUrl("https://www.cloudfront.net/thumbnail/" + 1)
                .videoUrl("https://www.cloudfront.net/video/" + 1)
                .views(100)
                .price(1000)
                .reward(10)
                .star(4.5f)
                .isPurchased(true)
                .isReplied(true)
                .categories(createVideoCategoryResponse("java", "react"))
                .channel(createVideoChannelResponse())
                .createdDate(LocalDateTime.now())
                .build();
    }
}