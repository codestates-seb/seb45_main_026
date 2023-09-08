package com.server.domain.reply.controller;

import com.server.domain.reply.dto.MemberInfo;
import com.server.domain.reply.dto.ReplyInfo;
import com.server.domain.reply.dto.ReplyUpdateControllerApi;
import com.server.global.reponse.ApiSingleResponse;
import com.server.global.testhelper.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.cglib.core.Local;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static com.server.global.testhelper.RestDocsUtil.singleResponseFields;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class ReplyControllerTest extends ControllerTest {

    private final String BASE_URL = "/replies";

    @Test
    @DisplayName("댓글 수정 API")
    void updateReply() throws Exception {
        //given
        Long replyId = 1L;

        ReplyUpdateControllerApi request = ReplyUpdateControllerApi.builder()
                .content("update content")
                .star(5)
                .build();

        //when
        ResultActions actions = mockMvc.perform(patch(BASE_URL + "/{reply-id}", replyId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, TOKEN));

        //then
        actions
                .andDo(print())
                .andExpect(status().isNoContent()
        );

        //restDocs
        setConstraintClass(ReplyUpdateControllerApi.class);

        actions
                .andDo(documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("reply-id").description("수정할 댓글 ID")
                        ),
                        requestFields(
                                fieldWithPath("content").description("댓글 내용").optional().attributes(getConstraint("content")),
                                fieldWithPath("star").description("댓글 별점").optional().attributes(getConstraint("star"))
                        )
                ));
    }

    @Test
    @DisplayName("댓글 단건 조회 API")
    void getReply() throws Exception {
        //given
        Long replyId = 1L;

        MemberInfo member = MemberInfo.builder()
                .memberId(1L)
                .nickname("nickname")
                .imageUrl("imageUrl")
                .build();


        ReplyInfo response = ReplyInfo.builder()
                .replyId(replyId)
                .content("content")
                .star(5)
                .member(member)
                .createdDate(LocalDateTime.now())
                .build();

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(response, "댓글 단건 조회 성공"));

        given(replyService.getReply(anyLong())).willReturn(response);

        //when
        ResultActions actions = mockMvc.perform(get(BASE_URL + "/{reply-id}", replyId)
                .accept(APPLICATION_JSON)
                .header(AUTHORIZATION, TOKEN));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restDocs
        actions
                .andDo(documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰").optional()
                        ),
                        pathParameters(
                                parameterWithName("reply-id").description("조회할 댓글 ID")
                        ),
                        singleResponseFields(
                                fieldWithPath("data").description("댓글 데이터"),
                                fieldWithPath("data.replyId").description("댓글 ID"),
                                fieldWithPath("data.content").description("댓글 내용"),
                                fieldWithPath("data.star").description("댓글 별점"),
                                fieldWithPath("data.member").description("댓글 작성자"),
                                fieldWithPath("data.member.memberId").description("댓글 작성자 ID"),
                                fieldWithPath("data.member.nickname").description("댓글 작성자 닉네임"),
                                fieldWithPath("data.member.imageUrl").description("댓글 작성자 프로필 이미지"),
                                fieldWithPath("data.createdDate").description("댓글 작성일")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 삭제 API")
    void deleteReply() throws Exception {
        //given
        Long replyId = 1L;

        //when
        ResultActions actions = mockMvc.perform(delete(BASE_URL + "/{reply-id}", replyId)
                .header(AUTHORIZATION, TOKEN));

        //then
        actions
                .andDo(print())
                .andExpect(status().isNoContent());

        //restDocs
        actions
                .andDo(documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("reply-id").description("삭제할 댓글 ID")
                        )
                ));
    }

    @TestFactory
    @DisplayName("댓글 수정 시 validation 테스트")
    Collection<DynamicTest> updateReplyValidation() {
        //given
        Long replyId = 1L;

        return List.of(
                dynamicTest("모든 값이 null 이라도 응답받을 수 있다.", ()-> {
                    //given
                    ReplyUpdateControllerApi request = ReplyUpdateControllerApi.builder()
                            .content(null)
                            .star(null)
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(patch(BASE_URL + "/{reply-id}", replyId)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header(AUTHORIZATION, TOKEN));

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isNoContent());
                }),
                dynamicTest("replyId 가 양수가 아니면 검증에 실패한다.", ()-> {
                    //given
                    Long wrongReplyId = 0L;

                    ReplyUpdateControllerApi request = ReplyUpdateControllerApi.builder()
                            .content("update content")
                            .star(5)
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(patch(BASE_URL + "/{reply-id}", wrongReplyId)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header(AUTHORIZATION, TOKEN));

                    //then
                    actions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("replyId"))
                            .andExpect(jsonPath("$.data[0].value").value(wrongReplyId))
                            .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
                }),
                dynamicTest("content 가 공백이면 검증에 실패한다.", ()-> {
                    //given
                    String wrongContent = " ";

                    ReplyUpdateControllerApi request = ReplyUpdateControllerApi.builder()
                            .content(wrongContent)
                            .star(5)
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(patch(BASE_URL + "/{reply-id}", replyId)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header(AUTHORIZATION, TOKEN));

                    //then
                    actions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("content"))
                            .andExpect(jsonPath("$.data[0].value").value(wrongContent))
                            .andExpect(jsonPath("$.data[0].reason").value("수강평은 공백을 허용하지 않습니다."));

                }),
                dynamicTest("content 의 글자 수가 100글자를 넘으면 검증에 실패한다.", ()-> {
                    //given
                    String wrongContent = "11111111111111111111111111111111111111111111111111" +
                            "11111111111111111111111111111111111111111111111111" + "1";

                    ReplyUpdateControllerApi request = ReplyUpdateControllerApi.builder()
                            .content(wrongContent)
                            .star(5)
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(patch(BASE_URL + "/{reply-id}", replyId)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header(AUTHORIZATION, TOKEN));

                    //then
                    actions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("content"))
                            .andExpect(jsonPath("$.data[0].value").value(wrongContent))
                            .andExpect(jsonPath("$.data[0].reason").value("허용된 글자 수는 1자에서 100자 입니다."));
                }),
                dynamicTest("star 가 양수가 아니면 검증에 실패한다.", ()-> {
                    //given
                    Integer wrongStar = 0;

                    ReplyUpdateControllerApi request = ReplyUpdateControllerApi.builder()
                            .content("content")
                            .star(wrongStar)
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(patch(BASE_URL + "/{reply-id}", replyId)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .header(AUTHORIZATION, TOKEN));

                    //then
                    actions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("star"))
                            .andExpect(jsonPath("$.data[0].value").value(wrongStar))
                            .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
                })
        );
    }

    @Test
    @DisplayName("댓글 단건 조회 시 validation 테스트 - replyId 가 양수가 아니면 검증에 실패한다.")
    void getReplyValidation() throws Exception {
        //given
        Long wrongReplyId = 0L;

        //when
        ResultActions actions = mockMvc.perform(get(BASE_URL + "/{reply-id}", wrongReplyId)
                .accept(APPLICATION_JSON));

        //then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data[0].field").value("replyId"))
                .andExpect(jsonPath("$.data[0].value").value(wrongReplyId))
                .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
    }

    @Test
    @DisplayName("댓글 삭제 시 validation 테스트 - replyId 가 양수가 아니면 검증에 실패한다.")
    void deleteReplyValidation() throws Exception {
        //given
        Long wrongReplyId = 0L;

        //when
        ResultActions actions = mockMvc.perform(delete(BASE_URL + "/{reply-id}", wrongReplyId)
                .header(AUTHORIZATION, TOKEN)
        );

        //then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data[0].field").value("replyId"))
                .andExpect(jsonPath("$.data[0].value").value(wrongReplyId))
                .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
    }
}