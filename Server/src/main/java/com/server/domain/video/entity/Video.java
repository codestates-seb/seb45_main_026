package com.server.domain.video.entity;

import com.server.domain.category.entity.Category;
import com.server.domain.cart.entity.Cart;
import com.server.domain.channel.entity.Channel;
import com.server.domain.order.entity.OrderVideo;
import com.server.domain.question.entity.Question;
import com.server.domain.reply.entity.Reply;
import com.server.domain.watch.entity.Watch;
import com.server.global.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Builder.Default
    @OneToMany(mappedBy = "video")
    private List<Reply> replies = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "video")
    private List<Watch> watches = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "video")
    private List<Cart> carts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "video")
    private List<Question> questions = new ArrayList<>();

    @Builder.Default
    @ManyToMany(mappedBy = "videoCategories")
    private List<Category> videoCategories = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "video")
    private List<OrderVideo> orderVideos = new ArrayList<>();



}
