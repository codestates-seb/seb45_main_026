package com.server.domain.member.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.server.domain.channel.entity.Channel;
import com.server.domain.order.entity.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@NoArgsConstructor
@Getter
public class MemberVideoResponse {

    private Long videoId;

    private String orderId;

    private String videoName;

    private String description;

    private String thumbnailFile;

    private String videoFile;

    private int view;

    private int star;

    private int price;

    private OrderStatus orderStatus;

    private LocalDateTime purchasedDate;

    private Long channelId;

    private String channelName;

    @QueryProjection
    public MemberVideoResponse(Long videoId, String orderId, String videoName, String description, String thumbnailFile, String videoFile, int view, int star, int price, OrderStatus orderStatus, LocalDateTime purchasedDate, Long channelId, String channelName) {
        this.videoId = videoId;
        this.orderId = orderId;
        this.videoName = videoName;
        this.description = description;
        this.thumbnailFile = thumbnailFile;
        this.videoFile = videoFile;
        this.view = view;
        this.star = star;
        this.price = price;
        this.orderStatus = orderStatus;
        this.purchasedDate = purchasedDate;
        this.channelId = channelId;
        this.channelName = channelName;
    }
}
