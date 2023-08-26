package com.server.domain.order.entity;

import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long orderId;

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

    private Order(Integer reward) {
        this.reward = reward;
        this.orderStatus = OrderStatus.ORDERED;
    }

    public static Order createOrder(List<Video> videos, Integer reward) {

        Order order = new Order(reward);

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
}
