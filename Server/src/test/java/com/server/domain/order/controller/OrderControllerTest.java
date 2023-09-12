package com.server.domain.order.controller;

import com.server.domain.order.controller.dto.request.OrderCreateApiRequest;
import com.server.domain.order.controller.dto.response.PaymentApiResponse;
import com.server.domain.order.controller.dto.response.VideoCancelApiResponse;
import com.server.domain.order.service.dto.request.OrderCreateServiceRequest;
import com.server.domain.order.service.dto.response.OrderResponse;
import com.server.domain.order.service.dto.response.PaymentServiceResponse;
import com.server.domain.order.service.dto.response.CancelServiceResponse;
import com.server.global.reponse.ApiSingleResponse;
import com.server.global.testhelper.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.server.global.testhelper.RestDocsUtil.singleResponseFields;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest extends ControllerTest {

    private final String BASE_URL = "/orders";

    @Test
    @DisplayName("주문 생성 API")
    void createOrder() throws Exception {
        //given
        OrderCreateApiRequest request = OrderCreateApiRequest.builder()
                .reward(1000)
                .videoIds(List.of(1L, 2L))
                .build();

        OrderResponse response = OrderResponse.builder()
                .orderId("f47ac10b-58cc-4372-a567-0e02b2c3d479")
                .totalAmount(50000)
                .build();

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(response, "주문 요청 정보"));

        given(orderService.createOrder(anyLong(), any(OrderCreateServiceRequest.class))).willReturn(response);

        //when
        ResultActions actions = mockMvc.perform(post(BASE_URL)
                .header(AUTHORIZATION, TOKEN)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(apiResponse)
        );

        //restDocs
        setConstraintClass(OrderCreateApiRequest.class);

        actions.andDo(documentHandler.document(
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                ),
                requestFields(
                        fieldWithPath("reward").description("사용할 리워드").attributes(getConstraint("reward")),
                        fieldWithPath("videoIds").description("결제할 비디오 id 리스트").attributes(getConstraint("videoIds"))
                ),
                singleResponseFields(
                        fieldWithPath("data").description("주문 요청 데이터"),
                        fieldWithPath("data.orderId").description("주문 ID"),
                        fieldWithPath("data.totalAmount").description("총 결제 금액")
                )
        ));
    }

    @Test
    @DisplayName("주문 성공 API")
    void success() throws Exception {
        //given
        String orderId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        String paymentKey = "paymentKey";
        Integer amount = 45000;

        PaymentServiceResponse serviceResponse = createPaymentServiceResponse();

        String apiResponse = objectMapper.writeValueAsString(ApiSingleResponse.ok(
                PaymentApiResponse.of(serviceResponse),
                "결제 결과")
        );

        given(orderService.requestFinalPayment(anyLong(), anyString(), anyString(), anyInt(), any(LocalDateTime.class))).willReturn(serviceResponse);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/success")
                        .header(AUTHORIZATION, TOKEN)
                        .accept(APPLICATION_JSON)
                        .param("order-id", orderId)
                        .param("payment-key", paymentKey)
                        .param("amount", String.valueOf(amount))
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(apiResponse)
        );

        //restDocs
        actions.andDo(documentHandler.document(
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                ),
                requestParameters(
                        parameterWithName("order-id").description("주문 ID"),
                        parameterWithName("payment-key").description("결제 키"),
                        parameterWithName("amount").description("총 결제 요청 금액")
                ),
                singleResponseFields(
                        fieldWithPath("data").description("결제 결과"),
                        fieldWithPath("data.orderName").description("주문 상품명"),
                        fieldWithPath("data.status").description("결제 상태"),
                        fieldWithPath("data.totalAmount").description("총 결제 금액")
                )
        ));
    }

    @Test
    @DisplayName("주문 취소 API")
    void cancelOrder() throws Exception {
        //given
        String orderId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";

        CancelServiceResponse serviceResponse = CancelServiceResponse.builder()
                .totalRequest(5000)
                .totalCancelAmount(4000)
                .totalCancelReward(500)
                .usedReward(500)
                .build();

        VideoCancelApiResponse apiResponse = VideoCancelApiResponse.of(serviceResponse);

        given(orderService.cancelOrder(anyLong(), anyString())).willReturn(serviceResponse);

        //when
        ResultActions actions = mockMvc.perform(
                delete(BASE_URL + "/{order-id}", orderId)
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(
                        objectMapper.writeValueAsString(ApiSingleResponse.ok(apiResponse, "주문 취소 결과"))));

        //restDocs
        actions.andDo(documentHandler.document(
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                ),
                pathParameters(
                        parameterWithName("order-id").description("주문 ID")
                ),
                singleResponseFields(
                        fieldWithPath("data").description("주문 취소 결과"),
                        fieldWithPath("data.totalRequest").description("취소 요청 금액"),
                        fieldWithPath("data.totalCancelAmount").description("취소된 결제 금액"),
                        fieldWithPath("data.totalCancelReward").description("취소된 리워드"),
                        fieldWithPath("data.usedReward").description("사용된 리워드 환불때문에 취소 못한 금액")
                )
        ));
    }

    @Test
    @DisplayName("개별 비디오 취소 API")
    void cancelVideo() throws Exception {
        //given
        String orderId = "fafnalf123-fadsnfl24-45bbaslfdasdf";
        Long videoId = 1L;

        CancelServiceResponse serviceResponse = CancelServiceResponse.builder()
                .totalRequest(5000)
                .totalCancelAmount(4000)
                .totalCancelReward(500)
                .usedReward(500)
                .build();

        VideoCancelApiResponse apiResponse = VideoCancelApiResponse.of(serviceResponse);

        given(orderService.cancelVideo(anyLong(), anyString(), anyLong())).willReturn(serviceResponse);

        //when
        ResultActions actions = mockMvc.perform(
                delete(BASE_URL + "/{order-id}/videos/{video-id}", orderId, videoId)
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(
                        objectMapper.writeValueAsString(ApiSingleResponse.ok(apiResponse, "비디오 취소 결과"))));

        //restDocs
        actions.andDo(documentHandler.document(
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                ),
                pathParameters(
                        parameterWithName("order-id").description("주문 ID"),
                        parameterWithName("video-id").description("비디오 ID")
                ),
                singleResponseFields(
                        fieldWithPath("data").description("비디오 취소 결과"),
                        fieldWithPath("data.totalRequest").description("취소 요청 금액"),
                        fieldWithPath("data.totalCancelAmount").description("취소된 결제 금액"),
                        fieldWithPath("data.totalCancelReward").description("취소된 리워드"),
                        fieldWithPath("data.usedReward").description("사용된 리워드 환불때문에 취소 못한 금액")
                )
        ));
    }
    
    @TestFactory
    @DisplayName("주문 요청 시 validation 테스트")
    Collection<DynamicTest> createOrderValidation() {
        //given

        return List.of(
                dynamicTest("reward 가 null 일 때 검증에 실패한다.", ()-> {
                    //given
                    OrderCreateApiRequest request = OrderCreateApiRequest.builder()
                            .reward(null)
                            .videoIds(List.of(1L, 2L))
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(
                            post(BASE_URL)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("reward"))
                            .andExpect(jsonPath("$.data[0].value").value("null"))
                            .andExpect(jsonPath("$.data[0].reason").value("사용할 reward 은 필수 입력 값입니다."));
                }),
                dynamicTest("reward 가 0 일 때 검증에 성공한다.", ()-> {
                    //given
                    OrderCreateApiRequest request = OrderCreateApiRequest.builder()
                            .reward(0)
                            .videoIds(List.of(1L, 2L))
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(
                            post(BASE_URL)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isOk());
                }),
                dynamicTest("reward 가 음수일 때 검증에 실패한다.", ()-> {
                    //given
                    OrderCreateApiRequest request = OrderCreateApiRequest.builder()
                            .reward(-1)
                            .videoIds(List.of(1L, 2L))
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(
                            post(BASE_URL)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("reward"))
                            .andExpect(jsonPath("$.data[0].value").value("-1"))
                            .andExpect(jsonPath("$.data[0].reason").value("사용할 reward 는 0원 이상이어야 합니다."));
                }),
                dynamicTest("videoIds 가 null 일 때 검증에 실패한다.", ()-> {
                    //given
                    OrderCreateApiRequest request = OrderCreateApiRequest.builder()
                            .reward(0)
                            .videoIds(null)
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(
                            post(BASE_URL)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("videoIds"))
                            .andExpect(jsonPath("$.data[0].value").value("null"))
                            .andExpect(jsonPath("$.data[0].reason").value("비디오 id 값은 필수입니다."));
                }),
                dynamicTest("videoIds 가 빈 배열일 때 검증에 실패한다.", ()-> {
                    //given
                    OrderCreateApiRequest request = OrderCreateApiRequest.builder()
                            .reward(0)
                            .videoIds(new ArrayList<>())
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(
                            post(BASE_URL)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("videoIds"))
                            .andExpect(jsonPath("$.data[0].value").value("[]"))
                            .andExpect(jsonPath("$.data[0].reason").value("비디오 id 값은 최소 1개 이상이어야 합니다."));
                }),
                dynamicTest("videoIds 가 양수가 아닐 때 검증에 실패한다.", ()-> {
                    //given
                    OrderCreateApiRequest request = OrderCreateApiRequest.builder()
                            .reward(0)
                            .videoIds(List.of(0L, 1L))
                            .build();

                    //when
                    ResultActions actions = mockMvc.perform(
                            post(BASE_URL)
                                    .header(AUTHORIZATION, TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    );

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("videoIds"))
                            .andExpect(jsonPath("$.data[0].value").value("[0, 1]"))
                            .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
                })
        );
    }

    @TestFactory
    @DisplayName("주문 성공 시 validation 테스트")
    Collection<DynamicTest> successValidation() {
        //given
        String orderId = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        String paymentKey = "paymentKey";
        Integer amount = 45000;
        PaymentServiceResponse serviceResponse = createPaymentServiceResponse();

        given(orderService.requestFinalPayment(anyLong(), anyString(), anyString(), anyInt(), any(LocalDateTime.class)))
                .willReturn(serviceResponse);

        return List.of(
                dynamicTest("amount 가 0 이면 검증에 성공한다.", ()-> {
                    //given
                    Integer correctAmount = 0;

                    //when
                    ResultActions actions = mockMvc.perform(
                            get(BASE_URL + "/success")
                                    .header(AUTHORIZATION, TOKEN)
                                    .accept(APPLICATION_JSON)
                                    .param("order-id", orderId)
                                    .param("payment-key", paymentKey)
                                    .param("amount", String.valueOf(correctAmount))
                    );

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isOk());
                }),
                dynamicTest("payment-key 가 null 이면 검증에 실패한다.", ()-> {
                    //when
                    ResultActions actions = mockMvc.perform(
                            get(BASE_URL + "/success")
                                    .header(AUTHORIZATION, TOKEN)
                                    .accept(APPLICATION_JSON)
                                    .param("order-id", orderId)
                                    .param("amount", String.valueOf(amount))
                    );

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("payment-key"))
                            .andExpect(jsonPath("$.data[0].value").value("null"))
                            .andExpect(jsonPath("$.data[0].reason").value("payment-key 값은 필수입니다."));
                }),
                dynamicTest("payment-key 가 공백이면 검증에 실패한다.", ()-> {
                    //given
                    String wrongPaymentKey = " ";

                    //when
                    ResultActions actions = mockMvc.perform(
                            get(BASE_URL + "/success")
                                    .header(AUTHORIZATION, TOKEN)
                                    .accept(APPLICATION_JSON)
                                    .param("order-id", orderId)
                                    .param("payment-key", wrongPaymentKey)
                                    .param("amount", String.valueOf(amount))
                    );

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("paymentKey"))
                            .andExpect(jsonPath("$.data[0].value").value(wrongPaymentKey))
                            .andExpect(jsonPath("$.data[0].reason").value("결제 키는 필수입니다."));
                }),
                dynamicTest("order-id 가 null 이면 검증에 실패한다.", ()-> {
                    //when
                    ResultActions actions = mockMvc.perform(
                            get(BASE_URL + "/success")
                                    .header(AUTHORIZATION, TOKEN)
                                    .accept(APPLICATION_JSON)
                                    .param("payment-key", paymentKey)
                                    .param("amount", String.valueOf(amount))
                    );

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("order-id"))
                            .andExpect(jsonPath("$.data[0].value").value("null"))
                            .andExpect(jsonPath("$.data[0].reason").value("order-id 값은 필수입니다."));
                }),
                dynamicTest("order-id 가 공백이면 검증에 실패한다.", ()-> {
                    //given
                    String wrongOrderId = " ";

                    //when
                    ResultActions actions = mockMvc.perform(
                            get(BASE_URL + "/success")
                                    .header(AUTHORIZATION, TOKEN)
                                    .accept(APPLICATION_JSON)
                                    .param("order-id", wrongOrderId)
                                    .param("payment-key", paymentKey)
                                    .param("amount", String.valueOf(amount))
                    );

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("orderId"))
                            .andExpect(jsonPath("$.data[0].value").value(wrongOrderId))
                            .andExpect(jsonPath("$.data[0].reason").value("주문번호는 필수입니다."));
                }),
                dynamicTest("amount 가 음수면 검증에 실패한다.", ()-> {
                    //given
                    Integer wrongAmount = -1;

                    //when
                    ResultActions actions = mockMvc.perform(
                            get(BASE_URL + "/success")
                                    .header(AUTHORIZATION, TOKEN)
                                    .accept(APPLICATION_JSON)
                                    .param("order-id", orderId)
                                    .param("payment-key", paymentKey)
                                    .param("amount", String.valueOf(wrongAmount))
                    );

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("amount"))
                            .andExpect(jsonPath("$.data[0].value").value(wrongAmount))
                            .andExpect(jsonPath("$.data[0].reason").value("주문 금액은 0원 이상이어야 합니다."));
                }),
                dynamicTest("amount 가 null 이면 검증에 실패한다.", ()-> {
                    //when
                    ResultActions actions = mockMvc.perform(
                            get(BASE_URL + "/success")
                                    .header(AUTHORIZATION, TOKEN)
                                    .accept(APPLICATION_JSON)
                                    .param("order-id", orderId)
                                    .param("payment-key", paymentKey)
                    );

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("amount"))
                            .andExpect(jsonPath("$.data[0].value").value("null"))
                            .andExpect(jsonPath("$.data[0].reason").value("amount 값은 필수입니다."));
                })
        );
    }

    @TestFactory
    @DisplayName("주문 취소 시 validation 테스트")
    Collection<DynamicTest> cancelOrderValidation() {
        //given

        return List.of(
                dynamicTest("order-id 값이 공백이면 검증에 실패한다.", ()-> {
                    //given
                    String wrongOrderId = " ";

                    //when
                    ResultActions actions = mockMvc.perform(
                            delete(BASE_URL + "/{order-id}", wrongOrderId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .accept(APPLICATION_JSON)
                    );

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("orderId"))
                            .andExpect(jsonPath("$.data[0].value").value(wrongOrderId))
                            .andExpect(jsonPath("$.data[0].reason").value("주문번호는 필수입니다."));
                })
        );
    }

    @TestFactory
    @DisplayName("비디오 주문 취소 시 validation 테스트")
    Collection<DynamicTest> cancelVideoValidation() {
        //given
        String orderId = "order-id";
        Long videoId = 1L;

        return List.of(
                dynamicTest("order-id 값이 공백이면 검증에 실패한다.", ()-> {
                    //given
                    String wrongOrderId = " ";

                    //when
                    ResultActions actions = mockMvc.perform(
                            delete(BASE_URL + "/{order-id}/videos/{video-id}", wrongOrderId, videoId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .accept(APPLICATION_JSON)
                    );

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("orderId"))
                            .andExpect(jsonPath("$.data[0].value").value(wrongOrderId))
                            .andExpect(jsonPath("$.data[0].reason").value("주문번호는 필수입니다."));
                }),
                dynamicTest("videoId 가 양수가 아니면 검증에 실패한다.", ()-> {
                    //given
                    Long wrongVideoId = 0L;

                    //when
                    ResultActions actions = mockMvc.perform(
                            delete(BASE_URL + "/{order-id}/videos/{video-id}", orderId, wrongVideoId)
                                    .header(AUTHORIZATION, TOKEN)
                                    .accept(APPLICATION_JSON)
                    );

                    //then
                    actions
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.data[0].field").value("videoId"))
                            .andExpect(jsonPath("$.data[0].value").value(wrongVideoId))
                            .andExpect(jsonPath("$.data[0].reason").value("해당 값은 양수만 가능합니다."));
                })
        );
    }

    private PaymentServiceResponse createPaymentServiceResponse() {
        return PaymentServiceResponse.builder()
                .orderName("2 t-shirt")
                .status("DONE")
                .totalAmount(45000)
                .build();
    }
}