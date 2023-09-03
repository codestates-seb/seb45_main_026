package com.server.domain.category.controller;

import com.server.domain.category.service.dto.response.CategoryResponse;
import com.server.global.reponse.ApiSingleResponse;
import com.server.global.testhelper.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.server.global.testhelper.RestDocsUtil.singleResponseFields;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryControllerTest extends ControllerTest {

    @Test
    @DisplayName("카테고리 목록 조회 API")
    void getCategories() throws Exception {
        //given
        List<CategoryResponse> categoryResponses = List.of(
                CategoryResponse.builder()
                        .categoryId(1L)
                        .categoryName("category1")
                        .build(),
                CategoryResponse.builder()
                        .categoryId(2L)
                        .categoryName("category2")
                        .build()
        );

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(categoryResponses, "카테고리 목록 조회 성공"));

        given(categoryService.getCategories()).willReturn(categoryResponses);

        //when
        ResultActions actions = mockMvc.perform(get("/categories")
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse))
        ;

        //restDocs
        actions
                .andDo(documentHandler.document(
                        singleResponseFields(
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("data[].categoryId").description("카테고리 id"),
                                fieldWithPath("data[].categoryName").description("카테고리 이름")
                        )
                ));
    }
}