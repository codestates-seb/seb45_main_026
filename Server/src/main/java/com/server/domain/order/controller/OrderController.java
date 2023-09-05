package com.server.domain.order.controller;

import com.server.domain.order.controller.dto.request.OrderCreateApiRequest;
import com.server.domain.order.controller.dto.response.PaymentApiResponse;
import com.server.domain.order.controller.dto.response.VideoCancelApiResponse;
import com.server.domain.order.service.OrderService;
import com.server.domain.order.service.dto.response.OrderResponse;
import com.server.domain.order.service.dto.response.PaymentServiceResponse;
import com.server.domain.order.service.dto.response.VideoCancelServiceResponse;
import com.server.global.annotation.LoginId;
import com.server.global.reponse.ApiSingleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ApiSingleResponse<OrderResponse>> createOrder(@RequestBody @Valid OrderCreateApiRequest request,
                                                                        @LoginId Long memberId) {

        OrderResponse response = orderService.createOrder(memberId, request.toServiceRequest());

        return ResponseEntity.ok(ApiSingleResponse.ok(response, "주문 요청 정보"));
    }

    @GetMapping("/success")
    public ResponseEntity<ApiSingleResponse<PaymentApiResponse>> success(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Integer amount,
            @LoginId Long loginMemberId) {

        PaymentServiceResponse serviceResponse = orderService.requestFinalPayment(loginMemberId, paymentKey, orderId, amount);

        PaymentApiResponse response = PaymentApiResponse.of(serviceResponse);

        return ResponseEntity.ok(ApiSingleResponse.ok(response, "결제 결과"));
    }

    @DeleteMapping("/{order-id}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable("order-id") String orderId,
            @LoginId Long loginMemberId) {

        orderService.cancelOrder(loginMemberId, orderId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{order-id}/videos/{video-id}")
    public ResponseEntity<ApiSingleResponse<VideoCancelApiResponse>> cancelVideo(
            @PathVariable("order-id") String orderId,
            @PathVariable("video-id") Long videoId,
            @LoginId Long loginMemberId) {

        VideoCancelServiceResponse serviceResponse
                = orderService.cancelVideo(loginMemberId, orderId, videoId);

        return ResponseEntity.ok(
                ApiSingleResponse.ok(VideoCancelApiResponse.of(serviceResponse),
                        "비디오 취소 결과"));
    }
}
