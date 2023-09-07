package com.server.domain.order.entity;

import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.global.entity.BaseEntity;
import com.server.global.exception.businessexception.orderexception.OrderNotValidException;
import com.server.global.exception.businessexception.orderexception.PriceNotMatchException;
import com.server.global.exception.businessexception.orderexception.RewardNotEnoughException;
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

    private Order(Member member, Integer totalPayAmount, Integer reward) {

        this.member = member;
        this.totalPayAmount = totalPayAmount;
        this.reward = reward;
        this.remainRefundAmount = totalPayAmount;
        this.remainRefundReward = reward;
        this.orderStatus = OrderStatus.ORDERED;
    }

    public static Order createOrder(Member member, List<Video> videos, Integer reward) {

        Integer totalPayAmount = videos.stream().mapToInt(Video::getPrice).sum() - reward;

        member.checkReward(reward);

        Order order = new Order(member, totalPayAmount, reward);

        videos.forEach(video -> {
            OrderVideo orderVideo = OrderVideo.createOrderVideo(order, video, video.getPrice());
            order.addOrderVideo(orderVideo);
        });

        return order;
    }

    public int getPrice(){
        return this.orderVideos.stream().mapToInt(OrderVideo::getPrice).sum() - reward;
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
        if(this.getPrice() != amount){
            throw new PriceNotMatchException();
        }
    }

    public void completeOrder(LocalDateTime completedDate) {
        this.orderStatus = OrderStatus.COMPLETED;
        this.orderVideos.forEach(OrderVideo::complete);
        this.completedDate = completedDate;
    }

    public void cancelOrdered() {
        this.orderStatus = OrderStatus.CANCELED;
    }

    public Refund cancelAllOrder() {
        this.orderStatus = OrderStatus.CANCELED;
        this.orderVideos.forEach(OrderVideo::cancel);

        this.member.addReward(this.remainRefundReward);

        return new Refund(remainRefundAmount, remainRefundReward);
    }

    public Refund cancelVideoOrder(OrderVideo orderVideo) {

        orderVideo.cancel();

        if(this.getOrderVideos().stream().allMatch(ov -> ov.getOrderStatus().equals(OrderStatus.CANCELED))) {
            return cancelAllOrder();
        }

        int refundAmount = calculateRefundAmount(orderVideo);
        int refundReward = calculateRefundReward(orderVideo.getPrice() - refundAmount);

        this.member.addReward(refundReward);

        return new Refund(refundAmount, refundReward);
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

        int totalRefundReward = refundReward;

        if(this.remainRefundReward < refundReward) {
            totalRefundReward = this.remainRefundReward;
            this.remainRefundReward = 0;
            return totalRefundReward;
        }
        this.remainRefundReward -= refundReward;
        return refundReward;
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

    public void setPaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }

    @Getter
    @AllArgsConstructor
    public static class Refund {

        private int refundAmount;
        private int refundReward;
    }

}
