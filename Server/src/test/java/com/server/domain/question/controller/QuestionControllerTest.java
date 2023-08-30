package com.server.domain.question.controller;

import com.server.domain.answer.entity.AnswerStatus;
import com.server.domain.question.controller.dto.request.AnswerCreateApiRequest;
import com.server.domain.question.controller.dto.request.QuestionUpdateApiRequest;
import com.server.domain.question.service.dto.request.AnswerCreateServiceRequest;
import com.server.domain.question.service.dto.response.QuestionResponse;
import com.server.global.reponse.ApiSingleResponse;
import com.server.global.testhelper.ControllerTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
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

class QuestionControllerTest extends ControllerTest {

    @Test
    @DisplayName("문제 단건 조회 API")
    void getQuestion() throws Exception {
        //given
        Long questionId = 1L;

        QuestionResponse questionResponse = createQuestionResponse(questionId);

        given(questionService.getQuestion(anyLong(), anyLong()))
                .willReturn(questionResponse);

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(questionResponse, "문제 조회 성공"));

        //when
        ResultActions actions = mockMvc.perform(
                get("/questions/{question-id}", questionId)
                        .header(AUTHORIZATION, TOKEN)
                        .contentType(APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse))
        ;

        //restDocs
        actions
                .andDo(
                        documentHandler.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("question-id").description("조회할 질문 ID")
                                ),
                                responseFields(
                                        fieldWithPath("data").description("질문 데이터"),
                                        fieldWithPath("data.questionId").description("질문 ID"),
                                        fieldWithPath("data.position").description("질문 순서"),
                                        fieldWithPath("data.content").description("질문 내용"),
                                        fieldWithPath("data.myAnswer").description("나의 답변"),
                                        fieldWithPath("data.questionAnswer").description("정답"),
                                        fieldWithPath("data.answerStatus").description(generateLinkCode(AnswerStatus.class)),
                                        fieldWithPath("data.description").description("질문에 대한 답변 설명"),
                                        fieldWithPath("data.selections").description("질문에 대한 선택지"),
                                        fieldWithPath("data.solvedDate").description("질문 풀이 날짜"),
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("status").description("응답 상태"),
                                        fieldWithPath("message").description("응답 메시지")
                                )
                        )
                );
    }

    @Test
    @DisplayName("문제 수정 API")
    void updateQuestion() throws Exception {
        //given
        Long questionId = 1L;

        QuestionUpdateApiRequest request = QuestionUpdateApiRequest.builder()
                .position(1)
                .content("content")
                .questionAnswer("1")
                .description("this is apple")
                .selections(List.of("selection1", "selection2", "selection3", "selection4", "selection5"))
                .build();

        //when
        ResultActions actions = mockMvc.perform(
                patch("/questions/{question-id}", questionId)
                        .header(AUTHORIZATION, TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isNoContent())
        ;

        //restDocs
        setConstraintClass(QuestionUpdateApiRequest.class);

        actions
                .andDo(
                        documentHandler.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("question-id").description("수정할 문제 ID")
                                ),
                                requestFields(
                                        fieldWithPath("position").description("문제 순서").optional().attributes(getConstraint("position")),
                                        fieldWithPath("content").description("문제 내용").optional().attributes(getConstraint("content")),
                                        fieldWithPath("questionAnswer").description("정답").optional().attributes(getConstraint("questionAnswer")),
                                        fieldWithPath("description").description("문제에 대한 답변 설명").optional().attributes(getConstraint("description")),
                                        fieldWithPath("selections").description("문제에 대한 선택지").optional().attributes(getConstraint("selections"))
                                )
                        )
                );
    }

    @Test
    @DisplayName("문제 삭제 API")
    void deleteQuestion() throws Exception {
        //given
        Long questionId = 1L;

        //when
        ResultActions actions = mockMvc.perform(
                delete("/questions/{question-id}", questionId)
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isNoContent())
        ;

        //restDocs
        actions
                .andDo(
                        documentHandler.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("question-id").description("삭제할 문제 ID")
                                )
                        )
                );
    }

    @Test
    @DisplayName("문제 단건 풀기 API")
    void solveQuestion() throws Exception {
        //given
        Long questionId = 1L;

        AnswerCreateApiRequest request = AnswerCreateApiRequest.builder()
                .myAnswer("apple")
                .build();

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(true, "문제 제출 성공"));

        given(questionService.solveQuestion(anyLong(), any(AnswerCreateServiceRequest.class)))
                .willReturn(true);

        //when
        ResultActions actions = mockMvc.perform(
                post("/questions/{question-id}/answers", questionId)
                        .header(AUTHORIZATION, TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restDocs
        setConstraintClass(AnswerCreateApiRequest.class);

        actions
                .andDo(
                        documentHandler.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("question-id").description("풀 문제 ID")
                                ),
                                requestFields(
                                        fieldWithPath("myAnswer").description("나의 답변").attributes(getConstraint("myAnswer"))
                                ),
                                responseFields(
                                        fieldWithPath("data").description("문제 풀이 결과"),
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("status").description("응답 상태"),
                                        fieldWithPath("message").description("응답 메시지")
                                )
                        )
                );
    }

    private QuestionResponse createQuestionResponse(Long questionId) {
        return QuestionResponse.builder()
                .questionId(questionId)
                .position(1)
                .content("content")
                .myAnswer("1")
                .questionAnswer("2")
                .selections(List.of("selection1", "selection2", "selection3", "selection4", "selection5"))
                .answerStatus(AnswerStatus.WRONG)
                .description("this is apple")
                .solvedDate(LocalDateTime.now())
                .build();
    }
}