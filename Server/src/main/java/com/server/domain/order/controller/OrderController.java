package com.server.domain.order.controller;

import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.order.controller.dto.request.OrderCreateApiRequest;
import com.server.domain.order.controller.dto.response.PaymentApiResponse;
import com.server.domain.order.service.OrderService;
import com.server.domain.order.service.dto.response.OrderResponse;
import com.server.domain.order.service.dto.response.PaymentServiceResponse;
import com.server.global.annotation.LoginId;
import com.server.global.reponse.ApiSingleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

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
            @LoginId Long memberId) {

        PaymentServiceResponse serviceResponse = orderService.requestFinalPayment(memberId, paymentKey, orderId, amount);

        PaymentApiResponse response = PaymentApiResponse.of(serviceResponse);

        return ResponseEntity.ok(ApiSingleResponse.ok(response, "결제 결과"));
    }

    @DeleteMapping("/{order-id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable("order-id") String orderId,
                                            @LoginId Long memberId) {

        orderService.deleteOrder(memberId, orderId);

        return ResponseEntity.noContent().build();
    }
}