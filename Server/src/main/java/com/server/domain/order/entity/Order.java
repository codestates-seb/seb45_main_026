package com.server.domain.order.entity;

import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.global.entity.BaseEntity;
import com.server.global.exception.businessexception.orderexception.*;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Order extends BaseEntity {

    @Id
    private String orderId;

    @PrePersist
    public void generateUuid() {
        if (orderId == null) {
            orderId = UUID.randomUUID().toString();
        }
    }

    private String paymentKey;

    private Integer totalPayAmount;

    private Integer remainRefundAmount;

    private Integer reward;

    private Integer remainRefundReward;

    private LocalDateTime completedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderVideo> orderVideos = new ArrayList<>();

    public void addOrderVideo(OrderVideo orderVideo) {
        this.orderVideos.add(orderVideo);
        orderVideo.addOrder(this);
    }

    private Order(Member member, Integer totalPayAmount, Integer reward, List<Video> videos) {

        this.member = member;
        this.totalPayAmount = totalPayAmount;
        this.reward = reward;
        this.remainRefundAmount = 0;
        this.remainRefundReward = 0;
        this.orderStatus = OrderStatus.ORDERED;

        videos.forEach(video -> {
            OrderVideo orderVideo = OrderVideo.createOrderVideo(this, video, video.getPrice());
            this.addOrderVideo(orderVideo);
        });
    }

    public static Order createOrder(Member member, List<Video> videos, Integer reward) {

        int totalPayAmount = videos.stream().mapToInt(Video::getPrice).sum() - reward;

        if(totalPayAmount < 0){
            throw new RewardExceedException();
        }

        member.checkReward(reward);

        return new Order(member, totalPayAmount, reward, videos);
    }

    public List<Video> getVideos(){
        List<Video> videos = new ArrayList<>();
        this.orderVideos.forEach(orderVideo -> videos.add(orderVideo.getVideo()));
        return videos;
    }

    public void checkValidOrder(int amount) {
        checkOrdered();
        checkAmount(amount);
    }

    private void checkOrdered() {
        if(this.orderStatus != OrderStatus.ORDERED){
            throw new OrderNotValidException();
        }
    }

    private void checkAmount(int amount) {
        if(this.getTotalPayAmount() != amount){
            throw new PriceNotMatchException();
        }
    }

    public void completeOrder(LocalDateTime completedDate, String paymentKey) {
        this.paymentKey = paymentKey;
        this.orderStatus = OrderStatus.COMPLETED;
        this.orderVideos.forEach(OrderVideo::complete);
        this.completedDate = completedDate;
        this.remainRefundAmount = totalPayAmount;
        this.remainRefundReward = reward;
        this.member.minusReward(this.reward);
    }

    public Refund cancelAllOrder() {

        this.orderVideos.forEach(OrderVideo::cancel);

        if(isComplete()) {
            this.member.addReward(this.remainRefundReward);
        }

        cancel();

        Refund refund = new Refund(remainRefundAmount, remainRefundReward);

        this.remainRefundAmount = 0;
        this.remainRefundReward = 0;

        return refund;
    }

    public boolean isComplete() {
        return this.orderStatus.equals(OrderStatus.COMPLETED);
    }

    public void checkAlreadyCanceled() {
        if(this.orderStatus.equals(OrderStatus.CANCELED)){
            throw new OrderAlreadyCanceledException();
        }
    }

    private void cancel() {
        this.orderStatus = OrderStatus.CANCELED;
    }

    public Refund cancelVideoOrder(OrderVideo orderVideo) {

        orderVideo.cancel();

        if(allVideoIsCanceled()) {
            return cancelAllOrder();
        }

        int refundAmount = calculateRefundAmount(orderVideo);
        int refundReward = calculateRefundReward(orderVideo.getPrice() - refundAmount);

        this.member.addReward(refundReward);

        return new Refund(refundAmount, refundReward);
    }

    private boolean allVideoIsCanceled() {
        return this.getOrderVideos().stream().allMatch(ov -> ov.getOrderStatus().equals(OrderStatus.CANCELED));
    }

    public void convertAmountToReward(int reward){
        if(this.remainRefundReward + this.remainRefundAmount  < reward) {
            throw new RewardNotEnoughException();
        }
        if(this.remainRefundReward < reward) {
            int refundReward = reward - this.remainRefundReward;
            this.remainRefundReward = 0;
            this.remainRefundAmount -= refundReward;
        }else {
            this.remainRefundReward -= reward;
        }
        this.member.addReward(reward);
    }

    private int calculateRefundAmount(OrderVideo orderVideo) {

        int refundAmount = orderVideo.getPrice();

        if(this.remainRefundAmount < refundAmount) {
            refundAmount = this.remainRefundAmount;
            this.remainRefundAmount = 0;
            return refundAmount;
        }

        this.remainRefundAmount -= refundAmount;
        return refundAmount;
    }

    private int calculateRefundReward(int refundReward) {

        int totalRefundReward;

        if(this.remainRefundReward < refundReward) {
            totalRefundReward = this.remainRefundReward;
            this.remainRefundReward = 0;
            return totalRefundReward;
        }
        this.remainRefundReward -= refundReward;
        return refundReward;
    }
    @Getter
    @AllArgsConstructor
    public static class Refund {


        private int refundAmount;
        private int refundReward;
    }

}
