package com.server.domain.reply.controller;

import com.server.domain.reply.dto.MemberInfo;
import com.server.domain.reply.dto.ReplyInfo;
import com.server.domain.reply.dto.ReplyUpdateControllerApi;
import com.server.global.reponse.ApiSingleResponse;
import com.server.global.testhelper.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.core.Local;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static com.server.global.testhelper.RestDocsUtil.singleResponseFields;
import static org.junit.jupiter.api.Assertions.*;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        given(replyService.getReply(anyLong(), anyLong())).willReturn(response);

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
}