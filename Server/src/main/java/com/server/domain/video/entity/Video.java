package com.server.domain.video.entity;

import com.server.domain.entity.BaseEntity;
import com.server.domain.Order.entity.Order;
import com.server.domain.cart.entity.Cart;
import com.server.domain.channel.entity.Channel;
import com.server.domain.like.entity.Like;
import com.server.domain.question.entity.Question;
import com.server.domain.reply.entity.Reply;
import com.server.domain.videoCategory.entity.VideoCategory;
import com.server.domain.watch.entity.Watch;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
public class Video extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long videoId;

    @Column(nullable = false)
    private String videoName;

    @Lob
    @Column
    private String description;

    @Column(nullable = false)
    private String thumbnailFile;

    @Column(nullable = false)
    private String videoFile;

    @Column(nullable = false)
    private int view;

    @Column(nullable = false)
    private int star;

    @Column(nullable = false)
    private int price;

    @OneToMany(mappedBy = "video")
    private List<Like> likes = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "channelId")
    private Channel channel;

    @OneToMany(mappedBy = "video")
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "video")
    private List<Watch> watches = new ArrayList<>();

    @OneToMany(mappedBy = "video")
    private List<Cart> carts = new ArrayList<>();

    @OneToMany(mappedBy = "video")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "video")
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "video")
    private List<VideoCategory> videoCategories = new ArrayList<>();


}
