package com.server.domain.question.controller;

import com.server.domain.answer.entity.AnswerStatus;
import com.server.domain.question.controller.dto.request.AnswerCreateApiRequest;
import com.server.domain.question.controller.dto.request.QuestionUpdateApiRequest;
import com.server.domain.question.service.dto.request.AnswerCreateServiceRequest;
import com.server.domain.question.service.dto.response.QuestionResponse;
import com.server.global.reponse.ApiSingleResponse;
import com.server.global.testhelper.ControllerTest;
import org.junit.jupiter.api.*;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static com.server.global.testhelper.RestDocsUtil.singleResponseFields;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
                                singleResponseFields(
                                        fieldWithPath("data").description("질문 데이터"),
                                        fieldWithPath("data.questionId").description("질문 ID"),
                                        fieldWithPath("data.position").description("질문 순서"),
                                        fieldWithPath("data.content").description("질문 내용"),
                                        fieldWithPath("data.myAnswer").description("나의 답변"),
                                        fieldWithPath("data.questionAnswer").description("정답"),
                                        fieldWithPath("data.answerStatus").description(generateLinkCode(AnswerStatus.class)),
                                        fieldWithPath("data.description").description("질문에 대한 답변 설명"),
                                        fieldWithPath("data.selections").description("질문에 대한 선택지"),
                                        fieldWithPath("data.choice").description("객관식 여부"),
                                        fieldWithPath("data.solvedDate").description("질문 풀이 날짜")
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
                .selections(List.of("selection1", "selection2", "selection3", "selection4"))
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
                                singleResponseFields(
                                        fieldWithPath("data").description("문제 풀이 결과")
                                )
                        )
                );
    }

    @Test
    @DisplayName("문제 단건 조회 validation 테스트 - questionId 가 양수가 아니면 검증에 실패한다.")
    void getQuestionValidation() throws Exception {
        //given
        Long wrongQuestionId = 0L;

        //when
        ResultActions actions = mockMvc.perform(
                get("/questions/{question-id}", wrongQuestionId)
                        .header(AUTHORIZATION, TOKEN)
                        .contentType(APPLICATION_JSON)
        );

        //then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data[0].field").value("questionId"))
                .andExpect(jsonPath("$.data[0].value").value(wrongQuestionId))
                .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
    }

    @TestFactory
    @DisplayName("문제 수정 시 validation 테스트")
    Collection<DynamicTest> updateQuestionValidation() {
        //given
        Long questionId = 1L;

        return List.of(
                dynamicTest("모든 값은 필수가 아니다", ()-> {
                    //given
                    QuestionUpdateApiRequest request = QuestionUpdateApiRequest.builder().
                            build();

                    //when
                    ResultActions actions = mockMvc.perform(
                            patch("/questions/{question-id}", questionId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    actions.andDo(print())
                            .andExpect(status().isNoContent());
                }),
                dynamicTest("questionId 가 양수가 아니면 검증에 실패한다.", ()-> {
                    //given
                    Long wrongQuestionId = 0L;

                    QuestionUpdateApiRequest request = QuestionUpdateApiRequest.builder()
                            .position(1)
                            .content("content")
                            .questionAnswer("1")
                            .description("this is apple")
                            .selections(List.of("selection1", "selection2", "selection3", "selection4"))
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(
                            patch("/questions/{question-id}", wrongQuestionId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    actions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("questionId"))
                            .andExpect(jsonPath("$.data[0].value").value(wrongQuestionId))
                            .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
                }),
                dynamicTest("position 값이 있을 때 양수가 아니면 검증에 실패한다.", ()-> {
                    //given
                    int wrongPosition = 0;

                    QuestionUpdateApiRequest request = QuestionUpdateApiRequest.builder()
                            .position(wrongPosition)
                            .content("content")
                            .questionAnswer("1")
                            .description("this is apple")
                            .selections(List.of("selection1", "selection2", "selection3", "selection4"))
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(
                            patch("/questions/{question-id}", questionId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    actions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("position"))
                            .andExpect(jsonPath("$.data[0].value").value(wrongPosition))
                            .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
                }),
                dynamicTest("content 값이 있을 때 공백이면 검증에 실패한다.", ()-> {
                    //given
                    String wrongContent = " ";

                    QuestionUpdateApiRequest request = QuestionUpdateApiRequest.builder()
                            .position(1)
                            .content(wrongContent)
                            .questionAnswer("1")
                            .description("this is apple")
                            .selections(List.of("selection1", "selection2", "selection3", "selection4"))
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(
                            patch("/questions/{question-id}", questionId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    actions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("content"))
                            .andExpect(jsonPath("$.data[0].value").value(wrongContent))
                            .andExpect(jsonPath("$.data[0].reason").value("문제의 내용은 공백을 허용하지 않습니다."));
                }),
                dynamicTest("questionAnswer 값이 있을 때 공백이면 검증에 실패한다.", ()-> {
                    //given
                    String questionAnswer = " ";

                    QuestionUpdateApiRequest request = QuestionUpdateApiRequest.builder()
                            .position(1)
                            .content("content")
                            .questionAnswer(questionAnswer)
                            .description("this is apple")
                            .selections(List.of("selection1", "selection2", "selection3", "selection4"))
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(
                            patch("/questions/{question-id}", questionId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    actions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("questionAnswer"))
                            .andExpect(jsonPath("$.data[0].value").value(questionAnswer))
                            .andExpect(jsonPath("$.data[0].reason").value("문제의 답은 공백을 허용하지 않습니다."));
                }),
                dynamicTest("selections 값이 있을 때 빈 배열이면 검증에 실패한다.", ()-> {
                    //given
                    QuestionUpdateApiRequest request = QuestionUpdateApiRequest.builder()
                            .position(1)
                            .content("content")
                            .questionAnswer("1")
                            .description("this is apple")
                            .selections(List.of())
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(
                            patch("/questions/{question-id}", questionId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    actions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("selections"))
                            .andExpect(jsonPath("$.data[0].value").value("[]"))
                            .andExpect(jsonPath("$.data[0].reason").value("선택지를 추가하려면 최소 1개, 최대 4개까지 가능합니다."));
                }),
                dynamicTest("selections 가 5개 이상이면 검증에 실패한다.", ()-> {
                    //given
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
                    actions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("selections"))
                            .andExpect(jsonPath("$.data[0].value").value("[selection1, selection2, selection3, selection4, selection5]"))
                            .andExpect(jsonPath("$.data[0].reason").value("선택지를 추가하려면 최소 1개, 최대 4개까지 가능합니다."));
                }),
                dynamicTest("selections 내부 값이 공백이면 검증에 실패한다.", ()-> {
                    //given
                    QuestionUpdateApiRequest request = QuestionUpdateApiRequest.builder()
                            .position(1)
                            .content("content")
                            .questionAnswer("1")
                            .description("this is apple")
                            .selections(List.of(" ", "selection2", "selection3", "selection4"))
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(
                            patch("/questions/{question-id}", questionId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    actions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("selections"))
                            .andExpect(jsonPath("$.data[0].value").value("[ , selection2, selection3, selection4]"))
                            .andExpect(jsonPath("$.data[0].reason").value("선택지의 내용은 공백을 허용하지 않습니다."));
                })
        );
    }

    @Test
    @DisplayName("문제 삭제 시 validation 테스트")
    void deleteQuestionValidation() throws Exception {
        //given
        Long wrongQuestionId = 0L;

        //when
        ResultActions actions = mockMvc.perform(
                delete("/questions/{question-id}", wrongQuestionId)
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        actions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data[0].field").value("questionId"))
                .andExpect(jsonPath("$.data[0].value").value(wrongQuestionId))
                .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
    }

    @TestFactory
    @DisplayName("개별 문제 풀기 validation 테스트")
    Collection<DynamicTest> solveQuestionValidation() {
        //given
        Long questionId = 1L;

        return List.of(
                dynamicTest("questionId 가 양수가 아니면 검증에 실패한다.", ()-> {
                    //given
                    Long wrongQuestionId = 0L;

                    AnswerCreateApiRequest request = AnswerCreateApiRequest.builder()
                            .myAnswer("1")
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(
                            post("/questions/{question-id}/answers", wrongQuestionId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    actions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("questionId"))
                            .andExpect(jsonPath("$.data[0].value").value(wrongQuestionId))
                            .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
                }),
                dynamicTest("myAnswer 이 null 이면 검증에 실패한다.", ()-> {
                    //given
                    AnswerCreateApiRequest request = AnswerCreateApiRequest.builder()
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(
                            post("/questions/{question-id}/answers", questionId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    actions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("myAnswer"))
                            .andExpect(jsonPath("$.data[0].value").value("null"))
                            .andExpect(jsonPath("$.data[0].reason").value("나의 답변 내용은 필수입니다."));
                }),
                dynamicTest("myAnswer 이 공백이면 검증에 실패한다.", ()-> {
                    //given
                    String wrongMyAnswer = " ";

                    AnswerCreateApiRequest request = AnswerCreateApiRequest.builder()
                            .myAnswer(wrongMyAnswer)
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(
                            post("/questions/{question-id}/answers", questionId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    actions.andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("myAnswer"))
                            .andExpect(jsonPath("$.data[0].value").value(wrongMyAnswer))
                            .andExpect(jsonPath("$.data[0].reason").value("나의 답변 내용은 필수입니다."));
                })




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