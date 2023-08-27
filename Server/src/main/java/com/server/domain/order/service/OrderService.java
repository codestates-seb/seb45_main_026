package com.server.domain.order.service;

import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.member.repository.dto.MemberVideoData;
import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderStatus;
import com.server.domain.order.repository.OrderRepository;
import com.server.domain.order.service.dto.request.OrderCreateServiceRequest;
import com.server.domain.order.service.dto.response.OrderResponse;
import com.server.domain.order.service.dto.response.PaymentServiceResponse;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.orderexception.OrderExistException;
import com.server.global.exception.businessexception.orderexception.OrderNotFoundException;
import com.server.global.exception.businessexception.orderexception.OrderNotValidException;
import com.server.global.exception.businessexception.orderexception.RewardNotEnoughException;
import com.server.global.exception.businessexception.videoexception.VideoNotFoundException;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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

    @Transactional
    public PaymentServiceResponse requestFinalPayment(Long memberId, String paymentKey, String orderId, int amount) {

        Member member = verifedMember(memberId);

        Order order = verifedOrder(member, orderId);

        order.checkValidOrder(amount);

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

        order.completeOrder();

        member.minusReward(amount);

        ResponseEntity<PaymentServiceResponse> response = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey,
                new HttpEntity<>(param, headers),
                PaymentServiceResponse.class
        );

        if(response.getStatusCode().value() != 200)
            throw new OrderNotValidException();

        order.setPaymentKey(paymentKey);

        return response.getBody();
    }

    @Transactional
    public void deleteOrder(Long memberId, String orderId) {

        Member member = verifedMember(memberId);
        Order order = verifedOrder(member, orderId);
        
        if(order.getOrderStatus().equals(OrderStatus.COMPLETED))
            member.addReward(order.getReward());

        order.deleteOrder();
    }

    @Transactional
    public OrderResponse createOrder(Long memberId, OrderCreateServiceRequest request) {

        Member member = verifedMember(memberId);

        checkEnoughReward(member.getReward(), request.getReward());

        List<Video> videos = checkValidVideos(request);

        checkDuplicateOrder(member, videos);

        Order order = Order.createOrder(member, videos, request.getReward());

        orderRepository.save(order);

        return OrderResponse.of(order);
    }

    private void checkDuplicateOrder(Member member, List<Video> toBuyVideos) {
        List<MemberVideoData> purchasedVideos = memberRepository.getMemberPurchaseVideo(member.getMemberId());

        for(MemberVideoData video : purchasedVideos){
            toBuyVideos.forEach(toBuyVideo -> {
                if (video.getVideoId().equals(toBuyVideo.getVideoId()) && !video.getOrderStatus().equals(OrderStatus.CANCELED))
                    throw new OrderExistException();
            });
        }


    }

    private List<Video> checkValidVideos(OrderCreateServiceRequest request) {

        List<Video> videos = videoRepository.findAllById(request.getVideoIds());

        if(videos.size() != request.getVideoIds().size())
            throw new VideoNotFoundException();

        return videos;
    }

    private void checkEnoughReward(int retainReward, Integer requestReward) {
        if(retainReward < requestReward)
            throw new RewardNotEnoughException();
    }

    private Member verifedMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    private Order verifedOrder(Member member, String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if(!order.getMember().equals(member))
            throw new OrderNotValidException();

        return order;
    }

}
