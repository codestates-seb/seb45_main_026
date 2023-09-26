package com.server.domain.adjustment.controller;

import com.server.domain.adjustment.controller.dto.request.AccountUpdateApiRequest;
import com.server.domain.adjustment.domain.AdjustmentStatus;
import com.server.domain.adjustment.service.dto.response.AccountResponse;
import com.server.domain.adjustment.service.dto.response.AdjustmentResponse;
import com.server.domain.adjustment.service.dto.response.MonthAdjustmentResponse;
import com.server.domain.adjustment.service.dto.response.ToTalAdjustmentResponse;
import com.server.domain.order.controller.dto.request.AdjustmentSort;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import com.server.global.testhelper.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static com.server.global.testhelper.RestDocsUtil.pageResponseFields;
import static com.server.global.testhelper.RestDocsUtil.singleResponseFields;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdjustmentControllerTest extends ControllerTest {

    private final String BASE_URL = "/adjustments";

    @Test
    @DisplayName("주문 정산 API")
    void adjustment() throws Exception {
        //given
        int page = 1;
        int size = 5;
        int month = 9;
        int year = 2023;
        String sort = "video-created-date";

        Page<AdjustmentResponse> pageResponse = createPage(createAdjustmentResponse(size), page, size, 100);

        given(adjustmentService.adjustment(anyLong(), anyInt(), anyInt(), anyInt(), anyInt(), anyString()))
                .willReturn(pageResponse);

        String apiResponse = objectMapper.writeValueAsString(ApiPageResponse.ok(pageResponse, year + "년 " + month + "월 정산 내역"));

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL)
                        .header(AUTHORIZATION, TOKEN)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("month", String.valueOf(month))
                        .param("year", String.valueOf(year))
                        .param("sort", sort)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restDocs
        actions.andDo(
                documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호").optional(),
                                parameterWithName("size").description("페이지 크기").optional(),
                                parameterWithName("month").description("정산 월").optional(),
                                parameterWithName("year").description("정산 년도").optional(),
                                parameterWithName("sort").description(generateLinkCode(AdjustmentSort.class)).optional()
                        ),
                        pageResponseFields(
                                fieldWithPath("data").description("정산 내역"),
                                fieldWithPath("data[].videoId").description("비디오 ID"),
                                fieldWithPath("data[].videoName").description("비디오 이름"),
                                fieldWithPath("data[].totalSaleAmount").description("기간 내 총 판매 금액"),
                                fieldWithPath("data[].refundAmount").description("기간 내 환불 금액")
                        )
                )
        );
    }

    @Test
    @DisplayName("연/월별 총 판매 금액 조회 API - 특정 달")
    void calculateAmount() throws Exception {
        //given
        int month = 9;
        int year = 2023;

        ToTalAdjustmentResponse response = createToTalAdjustmentResponse(9, 2023);

        given(adjustmentService.totalAdjustment(anyLong(), anyInt(), anyInt()))
                .willReturn(response);

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(response, year + "년 " + month + "월 정산 내역"));

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/total-adjustment")
                        .header(AUTHORIZATION, TOKEN)
                        .param("month", String.valueOf(month))
                        .param("year", String.valueOf(year))
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restDocs
        actions.andDo(
                documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        ),
                        requestParameters(
                                parameterWithName("month").description("정산 월").optional(),
                                parameterWithName("year").description("정산 년도").optional()
                        ),
                        singleResponseFields(
                                fieldWithPath("data").description("정산 내역"),
                                fieldWithPath("data.amount").description("정산 금액"),
                                fieldWithPath("data.adjustmentStatus").description(generateLinkCode(AdjustmentStatus.class)),
                                fieldWithPath("data.reason").description("정산 실패 시 사유"),
                                fieldWithPath("data.monthData").description("월별 정산 내역"),
                                fieldWithPath("data.monthData[].month").description("월"),
                                fieldWithPath("data.monthData[].year").description("년"),
                                fieldWithPath("data.monthData[].amount").description("정산 금액")
                        )
                )
        );
    }

    @Test
    @DisplayName("연/월별 총 판매 금액 조회 API - 전체 연도")
    void calculateAmountYear() throws Exception {
        //given
        int year = 2023;

        ToTalAdjustmentResponse response = createToTalAdjustmentResponse(null, 2023);

        given(adjustmentService.totalAdjustment(anyLong(), isNull(), anyInt()))
                .willReturn(response);

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(response, year + "년 정산 내역"));

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/total-adjustment")
                        .header(AUTHORIZATION, TOKEN)
                        .param("year", String.valueOf(year))
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse));

        //restDocs
        actions.andDo(
                documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        ),
                        requestParameters(
                                parameterWithName("year").description("정산 년도").optional()
                        ),
                        singleResponseFields(
                                fieldWithPath("data").description("정산 내역"),
                                fieldWithPath("data.amount").description("정산 금액"),
                                fieldWithPath("data.adjustmentStatus").description(generateLinkCode(AdjustmentStatus.class)),
                                fieldWithPath("data.reason").description("정산 실패 시 사유"),
                                fieldWithPath("data.monthData").description("월별 정산 내역"),
                                fieldWithPath("data.monthData[].month").description("월"),
                                fieldWithPath("data.monthData[].year").description("년"),
                                fieldWithPath("data.monthData[].amount").description("정산 금액")
                        )
                )
        );
    }

    @Test
    @DisplayName("계좌 정보 조회 API")
    void getAccount() throws Exception {
        //given
        AccountResponse response = AccountResponse.builder()
                .name("홍길*")
                .account("123-****-12314")
                .bank("국민은행")
                .build();

        given(adjustmentService.getAccount(anyLong())).willReturn(response);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/account")
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(ApiSingleResponse.ok(response, "계좌 정보 조회 성공"))));

        //restDocs
        actions.andDo(
                documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        ),
                        singleResponseFields(
                                fieldWithPath("data").description("계좌 정보"),
                                fieldWithPath("data.name").description("예금주"),
                                fieldWithPath("data.account").description("계좌번호"),
                                fieldWithPath("data.bank").description("은행")
                        )
                )
        );
    }

    @Test
    @DisplayName("계좌 정보 조회 API - 계좌정보를 등록하지 않았을 때")
    void getAccountNull() throws Exception {
        //given
        AccountResponse response = AccountResponse.builder()
                .name("계좌 정보가 없습니다.")
                .account("계좌 정보가 없습니다.")
                .bank("계좌 정보가 없습니다.")
                .build();

        given(adjustmentService.getAccount(anyLong())).willReturn(response);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/account")
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(ApiSingleResponse.ok(response, "계좌 정보가 없습니다."))));

        //restDocs
        actions.andDo(
                documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        ),
                        singleResponseFields(
                                fieldWithPath("data").description("계좌 정보"),
                                fieldWithPath("data.name").description("예금주"),
                                fieldWithPath("data.account").description("계좌번호"),
                                fieldWithPath("data.bank").description("은행")
                        )
                )
        );
    }

    @Test
    @DisplayName("계좌번호 변경 API")
    void updateAccount() throws Exception {
        //given
        AccountUpdateApiRequest request = AccountUpdateApiRequest.builder()
                .name("홍길동")
                .account("123-123-123123")
                .bank("국민은행")
                .build();

        //when
        ResultActions actions = mockMvc.perform(
                put(BASE_URL + "/account")
                        .header(AUTHORIZATION, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isNoContent());

        //restdocs
        setConstraintClass(AccountUpdateApiRequest.class);

        actions.andDo(
                documentHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("name").description("예금주").attributes(getConstraint("name")),
                                fieldWithPath("account").description("계좌번호").attributes(getConstraint("account")),
                                fieldWithPath("bank").description("은행").attributes(getConstraint("bank"))
                        )
                )
        );


    }

    private ToTalAdjustmentResponse createToTalAdjustmentResponse(Integer month, Integer year) {

        List<MonthAdjustmentResponse> monthAdjustmentResponses = new ArrayList<>();

        if(month != null) {
            for(int i = 1; i <= month; i++) {
                monthAdjustmentResponses.add(MonthAdjustmentResponse.builder()
                        .month(i)
                        .year(year)
                        .amount(10000)
                        .build());
            }
        }
        else {

            for(int i = 1; i <= 9; i++) {
                monthAdjustmentResponses.add(MonthAdjustmentResponse.builder()
                        .month(i)
                        .year(year)
                        .amount(10000)
                        .build());
            }
        }

        if(month == null || year == null) {
            return ToTalAdjustmentResponse.of(90000, AdjustmentStatus.TOTAL, "이유", monthAdjustmentResponses);
        }

        return ToTalAdjustmentResponse.of(10000, AdjustmentStatus.NO_ADJUSTMENT, "이유", monthAdjustmentResponses);
    }

    private List<AdjustmentResponse> createAdjustmentResponse(int size) {

        List<AdjustmentResponse> responses = new ArrayList<>();

        for(int i = 1; i <= size; i++) {
            responses.add(AdjustmentResponse.builder()
                    .videoId((long) i)
                    .videoName("비디오 " + i)
                    .totalSaleAmount(100000)
                    .refundAmount(1000)
                    .build());
        }

        return responses;
    }
}