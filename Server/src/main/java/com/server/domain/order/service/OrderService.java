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
import com.server.domain.reward.service.RewardService;
import com.server.domain.video.entity.Video;
import com.server.domain.video.entity.VideoStatus;
import com.server.domain.video.repository.VideoRepository;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.orderexception.*;
import com.server.global.exception.businessexception.videoexception.VideoClosedException;
import com.server.global.exception.businessexception.videoexception.VideoNotFoundException;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Transactional
public class OrderService {

    public static final String TOSS_ORIGIN_URL = "https://api.tosspayments.com/v1/payments/";
    private final MemberRepository memberRepository;
    private final VideoRepository videoRepository;
    private final OrderRepository orderRepository;
    private final RewardService rewardService;
    private final RestTemplate restTemplate;

    @Value("${order.payment-secret-key}")
    private String paymentSecretKey;

    public OrderService(MemberRepository memberRepository, VideoRepository videoRepository, OrderRepository orderRepository, RewardService rewardService, RestTemplate restTemplate) {
        this.memberRepository = memberRepository;
        this.videoRepository = videoRepository;
        this.orderRepository = orderRepository;
        this.rewardService = rewardService;
        this.restTemplate = restTemplate;
    }

    public OrderResponse createOrder(Long memberId, OrderCreateServiceRequest request) {

        Member member = verifiedMember(memberId);

        List<Video> videos = checkValidVideos(request);

        checkDuplicateOrder(member, videos);

        checkIfVideoClosed(videos);

        Order order = Order.createOrder(member, videos, request.getReward());

        return OrderResponse.of(orderRepository.save(order));
    }

    private void checkIfVideoClosed(List<Video> videos) {

        List<String> closedVideoNames = videos.stream()
                .filter(video -> video.getVideoStatus().equals(VideoStatus.CLOSED))
                .map(Video::getVideoName)
                .collect(Collectors.toList());

        if(!closedVideoNames.isEmpty()) {
            throw new VideoClosedException(String.join(", ", closedVideoNames));
        }
    }

    public PaymentServiceResponse requestFinalPayment(Long memberId, String paymentKey, String orderId, int amount) {

        orderCompleteProcess(memberId, paymentKey, orderId, amount);

        HttpHeaders headers = paymentRequestHeader();

        JSONObject param = paymentParams(orderId, amount);

        ResponseEntity<PaymentServiceResponse> response = restTemplate.postForEntity(
                TOSS_ORIGIN_URL + paymentKey,
                new HttpEntity<>(param, headers),
                PaymentServiceResponse.class
        );

        if(response.getStatusCode().value() != 200)
            throw new OrderNotValidException();

        return response.getBody();
    }

    private void orderCompleteProcess(Long memberId, String paymentKey, String orderId, int amount) {

        Member member = verifiedMember(memberId);

        Order order = verifiedOrderWithVideo(member, orderId);

        deleteCartFrom(memberId, orderId);

        order.checkValidOrder(amount);

        order.completeOrder();

        order.setPaymentKey(paymentKey);

        member.minusReward(order.getReward());

        addReward(member, order);
    }

    private void addReward(Member member, Order order) {

        for (Video video : order.getVideos()) {
            rewardService.createRewardIfNotPresent(video, member);
        }
    }

    private HttpHeaders paymentRequestHeader() {
        String encodedAuth = new String(Base64.getEncoder().encode((paymentSecretKey + ":").getBytes(UTF_8)));

        HttpHeaders headers = new HttpHeaders();

        headers.setBasicAuth(encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private JSONObject paymentParams(String orderId, int amount) {
        JSONObject param = new JSONObject();
        param.put("orderId", orderId);
        param.put("amount", amount);
        return param;
    }

    public void deleteOrder(Long memberId, String orderId) {

        Member member = verifiedMember(memberId);

        Order order = verifiedOrder(member, orderId);

        checkIfWatch(order);

        if(isAlreadyCanceled(order)) throw new OrderAlreadyCanceledException();

        if(isCompleted(order)) orderCancelProcess(order);

        order.deleteOrder();
    }

    private void checkIfWatch(Order order) {
        if(!orderRepository.findWatchVideosById(order.getOrderId()).isEmpty())
            throw new VideoAlreadyWatchedException();
    }

    private boolean isCompleted(Order order) {
        return order.getOrderStatus().equals(OrderStatus.COMPLETED);
    }

    private boolean isAlreadyCanceled(Order order) {
        return order.getOrderStatus().equals(OrderStatus.CANCELED);
    }

    private void orderCancelProcess(Order order) {

        URI uri = URI.create(TOSS_ORIGIN_URL + order.getPaymentKey() + "/cancel");

        rewardService.cancelReward(order);

        HttpHeaders headers = paymentRequestHeader();

        JSONObject param = new JSONObject();
        param.put("cancelReason", "사용자 취소");

        ResponseEntity<String> responseEntity
                = restTemplate.postForEntity(
                        uri,
                new HttpEntity<>(param, headers),
                String.class);

        if(responseEntity.getStatusCode().value() != 200)
            throw new CancelFailException();
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

    private void deleteCartFrom(Long memberId, String orderId) {
        orderRepository.deleteCartByMemberAndOrderId1(memberId, orderId);
    }

    private Member verifiedMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    private Order verifiedOrder(Member member, String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if(!order.getMember().equals(member))
            throw new MemberAccessDeniedException();

        return order;
    }

    private Order verifiedOrderWithVideo(Member member, String orderId) {
        Order order = orderRepository.findByIdWithVideos(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if(!order.getMember().equals(member))
            throw new MemberAccessDeniedException();

        return order;
    }
}
