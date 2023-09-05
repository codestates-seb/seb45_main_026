package com.server.domain.order.controller;

import com.server.domain.order.controller.dto.request.OrderCreateApiRequest;
import com.server.domain.order.controller.dto.response.PaymentApiResponse;
import com.server.domain.order.controller.dto.response.VideoCancelApiResponse;
import com.server.domain.order.service.dto.request.OrderCreateServiceRequest;
import com.server.domain.order.service.dto.response.OrderResponse;
import com.server.domain.order.service.dto.response.PaymentServiceResponse;
import com.server.domain.order.service.dto.response.VideoCancelServiceResponse;
import com.server.global.reponse.ApiSingleResponse;
import com.server.global.testhelper.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

import static com.server.global.testhelper.RestDocsUtil.singleResponseFields;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        given(orderService.requestFinalPayment(anyLong(), anyString(), anyString(), anyInt())).willReturn(serviceResponse);

        //when
        ResultActions actions = mockMvc.perform(
                get(BASE_URL + "/success")
                        .header(AUTHORIZATION, TOKEN)
                        .accept(APPLICATION_JSON)
                        .param("orderId", orderId)
                        .param("paymentKey", paymentKey)
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
                        parameterWithName("orderId").description("주문 ID"),
                        parameterWithName("paymentKey").description("결제 키"),
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

        //when
        ResultActions actions = mockMvc.perform(
                delete(BASE_URL + "/{order-id}", orderId)
                        .header(AUTHORIZATION, TOKEN)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isNoContent());

        //restDocs
        actions.andDo(documentHandler.document(
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                ),
                pathParameters(
                        parameterWithName("order-id").description("주문 ID")
                )
        ));
    }

    @Test
    @DisplayName("개별 비디오 취소 API")
    void cancelVideo() throws Exception {
        //given
        String orderId = "fafnalf123-fadsnfl24-45bbaslfdasdf";
        Long videoId = 1L;

        VideoCancelServiceResponse serviceResponse = VideoCancelServiceResponse.builder()
                .requestAmount(5000)
                .totalCancelAmount(4500)
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
                        fieldWithPath("data.requestAmount").description("취소 요청 금액"),
                        fieldWithPath("data.totalCancelAmount").description("총 취소 금액"),
                        fieldWithPath("data.usedReward").description("사용된 리워드(리워드에 사용되어 취소못한 금액)")
                )
        ));

    }

    private PaymentServiceResponse createPaymentServiceResponse() {
        return PaymentServiceResponse.builder()
                .orderName("2 t-shirt")
                .status("DONE")
                .totalAmount(45000)
                .build();
    }
}