package com.server.domain.order.entity;

import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.global.entity.BaseEntity;
import com.server.global.exception.businessexception.orderexception.OrderNotValidException;
import com.server.global.exception.businessexception.orderexception.PriceNotMatchException;
import lombok.*;

import javax.persistence.*;
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

    private Integer reward;

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

    private Order(Member member, Integer reward) {

        this.member = member;
        this.reward = reward;
        this.orderStatus = OrderStatus.ORDERED;
    }

    public static Order createOrder(Member member, List<Video> videos, Integer reward) {

        Order order = new Order(member, reward);

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

    public void completeOrder() {
        this.orderStatus = OrderStatus.COMPLETED;
    }

    public void deleteOrder() {
        this.orderStatus = OrderStatus.CANCELED;
    }

    public void setPaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }

}
