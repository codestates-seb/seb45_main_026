package com.server.domain.order.service;

import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderStatus;
import com.server.domain.order.entity.OrderVideo;
import com.server.domain.order.repository.OrderRepository;
import com.server.domain.adjustment.repository.dto.AdjustmentData;
import com.server.domain.order.service.dto.request.OrderCreateServiceRequest;
import com.server.domain.adjustment.service.dto.response.AdjustmentResponse;
import com.server.domain.order.service.dto.response.OrderResponse;
import com.server.domain.order.service.dto.response.PaymentServiceResponse;
import com.server.domain.order.service.dto.response.CancelServiceResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
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

    public OrderService(MemberRepository memberRepository, VideoRepository videoRepository,
                        OrderRepository orderRepository, RewardService rewardService,
                        RestTemplate restTemplate) {
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

        if(order.getTotalPayAmount() == 0) {
            order.completeOrder(LocalDateTime.now(), "freeOrder");
        }

        return OrderResponse.of(orderRepository.save(order));
    }

    public PaymentServiceResponse requestFinalPayment(Long memberId,
                                                      String paymentKey,
                                                      String orderId,
                                                      int amount,
                                                      LocalDateTime orderDate) {

        orderCompleteProcess(memberId, paymentKey, orderId, amount, orderDate);

        ResponseEntity<PaymentServiceResponse> response = restTemplate.postForEntity(
                TOSS_ORIGIN_URL + paymentKey,
                new HttpEntity<>(
                        paymentParams(orderId, amount),
                        paymentRequestHeader()),
                PaymentServiceResponse.class
        );

        if(response.getStatusCode().value() != 200)
            throw new OrderNotValidException();

        return response.getBody();
    }

    public CancelServiceResponse cancelOrder(Long memberId, String orderId) {

        Order order = verifiedOrder(memberId, orderId);

        order.checkAlreadyCanceled();

        int totalRequest = order.getRemainRefundAmount() + order.getRemainRefundReward();

        Order.Refund totalRefund = orderCancelProcess(order);

        return CancelServiceResponse.of(totalRequest, totalRefund);
    }

    public CancelServiceResponse cancelVideo(Long loginMemberId, String orderId, Long videoId) {

        Order order = verifiedOrderWithVideo(loginMemberId, orderId);

        if(!order.isComplete()) throw new OrderNotValidException();

        OrderVideo orderVideo = getOrderVideo(order, videoId);

        orderVideo.checkAlreadyCanceled();

        int totalRequest = orderVideo.getPrice();

        Order.Refund totalRefund = videoCancelProcess(order, orderVideo);

        return CancelServiceResponse.of(totalRequest, totalRefund);
    }

    @Transactional(readOnly = true)
    public Page<AdjustmentResponse> adjustment(Long loginMemberId, int page, int size, Integer month, Integer year, String sort) {

        Pageable pageable = PageRequest.of(page, size);

        Page<AdjustmentData> datas = orderRepository.findByPeriod(loginMemberId, pageable, month, year, sort);

        return datas.map(AdjustmentResponse::of);
    }

    @Transactional(readOnly = true)
    public Integer calculateAmount(Long loginMemberId, Integer month, Integer year) {

        return orderRepository.calculateAmount(loginMemberId, month, year);
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

    private void orderCompleteProcess(Long memberId, String paymentKey, String orderId, int amount, LocalDateTime orderDate) {

        Order order = verifiedOrderWithVideo(memberId, orderId);

        order.checkValidOrder(amount);

        deleteCartFrom(memberId, orderId);

        order.completeOrder(orderDate, paymentKey);

        addReward(order);
    }

    private void addReward(Order order) {

        for (Video video : order.getVideos()) {
            rewardService.createRewardIfNotPresent(video, order.getMember());
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

    private OrderVideo getOrderVideo(Order order, Long videoId) {
        return order.getOrderVideos().stream()
                .filter(ov -> ov.getVideo().getVideoId().equals(videoId))
                .findFirst()
                .orElseThrow(VideoNotFoundException::new);
    }

    private void checkIfWatchAny(Order order) {
        if(!orderRepository.findWatchVideosAfterPurchaseById(order).isEmpty())
            throw new VideoAlreadyWatchedException();
    }

    private void checkIfWatchVideo(OrderVideo orderVideo) {
        if(orderRepository.checkIfWatchAfterPurchase(
                orderVideo.getOrder(),
                orderVideo.getVideo().getVideoId()))
            throw new VideoAlreadyWatchedException();
    }

    private Order.Refund orderCancelProcess(Order order) {

        if(!order.isComplete()) return order.cancelAllOrder();

        checkIfWatchAny(order);

        rewardService.cancelOrderReward(order);

        Order.Refund refund = order.cancelAllOrder();

        orderCancelRequest(order, refund.getRefundAmount());

        return refund;
    }

    private Order.Refund videoCancelProcess(Order order, OrderVideo orderVideo) {

        checkIfWatchVideo(orderVideo);

        rewardService.cancelVideoReward(orderVideo);

        Order.Refund totalRefund = order.cancelVideoOrder(orderVideo);

        orderCancelRequest(order, totalRefund.getRefundAmount());

        return totalRefund;
    }

    private void orderCancelRequest(Order order, Integer cancelPrice) {

        if(cancelPrice == 0) return;

        URI uri = URI.create(TOSS_ORIGIN_URL + order.getPaymentKey() + "/cancel");

        HttpHeaders headers = paymentRequestHeader();

        JSONObject param = new JSONObject();
        param.put("cancelReason", "사용자 취소");
        param.put("cancelAmount", cancelPrice);

        ResponseEntity<String> responseEntity
                = restTemplate.postForEntity(
                uri,
                new HttpEntity<>(param, headers),
                String.class);

        if(responseEntity.getStatusCode().value() != 200)
            throw new CancelFailException();

    }

    private void checkDuplicateOrder(Member member, List<Video> toBuyVideos) {

        List<Long> toBuyVideoIds = toBuyVideos.stream()
                .map(Video::getVideoId)
                .collect(Collectors.toList());

        List<OrderVideo> orderVideos = orderRepository.findOrderedVideosByMemberId(member.getMemberId(), toBuyVideoIds);

        checkAlreadyPurchased(orderVideos);

        checkAlreadyOrderedAndSwitchCancel(orderVideos);
    }

    private void checkAlreadyPurchased(List<OrderVideo> orderVideos) {
        List<String> alreadyPurchasedVideoNames = orderVideos.stream()
                .filter(orderVideo -> orderVideo.getOrderStatus().equals(OrderStatus.COMPLETED))
                .map(orderVideo -> orderVideo.getVideo().getVideoName())
                .collect(Collectors.toList());

        if(!alreadyPurchasedVideoNames.isEmpty())
            throw new OrderExistException(String.join(", ", alreadyPurchasedVideoNames));
    }

    private void checkAlreadyOrderedAndSwitchCancel(List<OrderVideo> orderVideos) {
        orderVideos.forEach(orderVideo -> {
            if(orderVideo.getOrderStatus().equals(OrderStatus.ORDERED))
                orderVideo.getOrder().cancelAllOrder();
        });
    }

    private List<Video> checkValidVideos(OrderCreateServiceRequest request) {

        List<Video> videos = videoRepository.findAllById(request.getVideoIds());

        if(videos.size() != request.getVideoIds().size())
            throw new VideoNotFoundException();

        return videos;
    }

    private void deleteCartFrom(Long memberId, String orderId) {
        orderRepository.deleteCartByMemberAndOrderId(memberId, orderId);
    }

    private Member verifiedMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    private Order verifiedOrder(Long memberId, String orderId) {

        Member member = verifiedMember(memberId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if(!order.getMember().equals(member))
            throw new MemberAccessDeniedException();

        return order;
    }

    private Order verifiedOrderWithVideo(Long memberId, String orderId) {

        return orderRepository.findByIdWithVideos(memberId, orderId)
                .orElseThrow(OrderNotFoundException::new);
    }
}
