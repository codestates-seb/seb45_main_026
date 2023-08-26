package com.server.domain.order.entity;

import com.server.domain.video.entity.Video;
import com.server.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderVideo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderVideoId;

    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    private OrderVideo(Order order, Video video, Integer price) {
        this.order = order;
        this.video = video;
        this.price = price;
    }

    public static OrderVideo createOrderVideo(Order order, Video video, Integer price) {
        return new OrderVideo(order, video, price);
    }

    public void addOrder(Order order) {
        this.order = order;
    }
}
