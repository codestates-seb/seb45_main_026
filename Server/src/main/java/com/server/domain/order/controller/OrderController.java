package com.server.domain.order.controller;

import com.server.domain.order.controller.dto.request.AdjustmentSort;
import com.server.domain.order.controller.dto.request.OrderCreateApiRequest;
import com.server.domain.order.controller.dto.response.PaymentApiResponse;
import com.server.domain.order.controller.dto.response.VideoCancelApiResponse;
import com.server.domain.order.service.OrderService;
import com.server.domain.adjustment.service.dto.response.AdjustmentResponse;
import com.server.domain.order.service.dto.response.OrderResponse;
import com.server.domain.order.service.dto.response.PaymentServiceResponse;
import com.server.domain.order.service.dto.response.CancelServiceResponse;
import com.server.global.annotation.LoginId;
import com.server.global.exception.businessexception.orderexception.AdjustmentDateException;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/orders")
@Validated
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
            @RequestParam(name = "payment-key") @NotBlank(message = "{validation.order.paymentKey}")
            String paymentKey,
            @RequestParam(name = "order-id") @NotBlank(message = "{validation.order.orderId}")
            String orderId,
            @RequestParam @Min(value = 0, message = "{validation.order.amount.min}")
            Integer amount,
            @LoginId Long loginMemberId) {

        LocalDateTime orderCompletedDate = LocalDateTime.now();

        PaymentServiceResponse serviceResponse = orderService.requestFinalPayment(
                loginMemberId,
                paymentKey,
                orderId,
                amount,
                orderCompletedDate);

        PaymentApiResponse response = PaymentApiResponse.of(serviceResponse);

        return ResponseEntity.ok(ApiSingleResponse.ok(response, "결제 결과"));
    }

    @DeleteMapping("/{order-id}")
    public ResponseEntity<ApiSingleResponse<VideoCancelApiResponse>> cancelOrder(
            @PathVariable("order-id") @NotBlank(message = "{validation.order.orderId}") String orderId,
            @LoginId Long loginMemberId) {

        CancelServiceResponse serviceResponse = orderService.cancelOrder(loginMemberId, orderId);

        return ResponseEntity.ok(
                ApiSingleResponse.ok(VideoCancelApiResponse.of(serviceResponse),
                        "주문 취소 결과"));
    }

    @DeleteMapping("/{order-id}/videos/{video-id}")
    public ResponseEntity<ApiSingleResponse<VideoCancelApiResponse>> cancelVideo(
            @PathVariable("order-id") @NotBlank(message = "{validation.order.orderId}") String orderId,
            @PathVariable("video-id") @Positive(message = "{validation.positive}") Long videoId,
            @LoginId Long loginMemberId) {

        CancelServiceResponse serviceResponse
                = orderService.cancelVideo(loginMemberId, orderId, videoId);

        return ResponseEntity.ok(
                ApiSingleResponse.ok(VideoCancelApiResponse.of(serviceResponse),
                        "비디오 취소 결과"));
    }

    @GetMapping("/adjustment")
    public ResponseEntity<ApiPageResponse<AdjustmentResponse>> adjustment(
            @RequestParam(defaultValue = "1") @Positive(message = "{validation.positive}") int page,
            @RequestParam(defaultValue = "10") @Positive(message = "{validation.positive}") int size,
            @RequestParam(required = false) @Min(value = 1) @Max(value = 12) Integer month,
            @RequestParam(required = false) @Min(value = 2020) Integer year,
            @RequestParam(defaultValue = "video-created-date") AdjustmentSort sort,
            @LoginId Long loginMemberId) {

        if(month != null && year == null) {
            throw new AdjustmentDateException();
        }

        Page<AdjustmentResponse> response = orderService.adjustment(loginMemberId, page - 1, size, month, year, sort.getSort());

        return ResponseEntity.ok(ApiPageResponse.ok(response, getAdjustmentMessage(month, year)));
    }

    @GetMapping("/total-adjustment")
    public ResponseEntity<ApiSingleResponse<Integer>> calculateAmount(
            @RequestParam(required = false) @Min(value = 1) @Max(value = 12) Integer month,
            @RequestParam(required = false) @Min(value = 2020) Integer year,
            @LoginId Long loginMemberId) {

        if(month != null && year == null) {
            throw new AdjustmentDateException();
        }
        
        //정산되었는지도 반환 필요

        Integer total = orderService.calculateAmount(loginMemberId, month, year);

        return ResponseEntity.ok(ApiSingleResponse.ok(total, getAdjustmentMessage(month, year)));
    }

    private String getAdjustmentMessage(Integer month, Integer year) {
        if(month == null && year == null) {
            return "전체 정산 내역";
        }

        if(month != null && year != null) {
            return year + "년 " + month + "월 정산 내역";
        }

        return year + "년 정산 내역";
    }
}
