package com.server.domain.order.service;

import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.order.entity.Order;
import com.server.domain.order.repository.OrderRepository;
import com.server.domain.order.service.dto.request.OrderCreateServiceRequest;
import com.server.domain.order.service.dto.response.OrderResponse;
import com.server.domain.order.service.dto.response.OrderVideoResponse;
import com.server.domain.order.service.dto.response.PaymentServiceResponse;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.orderexception.RewardNotEnoughException;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final MemberRepository memberRepository;
    private final VideoRepository videoRepository;
    private final OrderRepository orderRepository;

    public OrderService(MemberRepository memberRepository, VideoRepository videoRepository, OrderRepository orderRepository) {
        this.memberRepository = memberRepository;
        this.videoRepository = videoRepository;
        this.orderRepository = orderRepository;
    }

    public boolean isPaid(Member member, Video video) {
        //todo : member 가 video 를 구매한 적이 있는지 확인하는 로직
        return true;
    }

    public List<OrderVideoResponse> purchasedVideoList(Member member) {
        //todo : member 가 구매한 video 리스트를 반환하는 로직
        return new ArrayList<>();
    }

    @Transactional
    public PaymentServiceResponse requestFinalPayment(String paymentKey, Long orderId, Long amount) {

        //todo: orderId 에 저장된 price 와 amount 가 같은지 확인하는 로직
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));

        RestTemplate restTemplate = new RestTemplate();

        String paymentSecretKey = "test_sk_26DlbXAaV0odbdDk9Kq3qY50Q9RB:";

        String encodedAuth = new String(Base64.getEncoder().encode(paymentSecretKey.getBytes(UTF_8)));

        HttpHeaders headers = new HttpHeaders();

        headers.setBasicAuth(encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        JSONObject param = new JSONObject();
        param.put("orderId", orderId);
        param.put("amount", amount);

        //todo: 장바구니에서 삭제하는 로직

        //todo: order 상태를 결제완료로 변경하는 로직

        //todo: member reward 차감하는 로직

        return restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey,
                new HttpEntity<>(param, headers),
                PaymentServiceResponse.class
        ).getBody();
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        //todo: order 를 취소하는 로직 (전체 취소)
    }

    @Transactional
    public OrderResponse createOrder(Long memberId, OrderCreateServiceRequest request) {

        Member member = verifedMember(memberId);

        checkEnoughReward(member.getReward(), request.getReward());

        List<Video> videos = videoRepository.findAllById(request.getVideoIds());

        checkValidVideo(videos, request);

        Order order = Order.createOrder(videos, request.getReward());

        orderRepository.save(order);

        return OrderResponse.of(order);
    }

    private void checkValidVideo(List<Video> videos, OrderCreateServiceRequest request) {
        if(videos.size() != request.getVideoIds().size())
            throw new IllegalArgumentException();
    }

    private void checkEnoughReward(int retainReward, Integer requestReward) {
        if(retainReward < requestReward)
            throw new RewardNotEnoughException();
    }

    private Member verifedMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }


}
