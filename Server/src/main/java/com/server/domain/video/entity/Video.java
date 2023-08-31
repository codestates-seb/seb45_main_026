package com.server.domain.video.entity;

import com.server.domain.category.entity.Category;
import com.server.domain.cart.entity.Cart;
import com.server.domain.channel.entity.Channel;
import com.server.domain.order.entity.OrderVideo;
import com.server.domain.question.entity.Question;
import com.server.domain.reply.entity.Reply;
import com.server.domain.videoCategory.entity.VideoCategory;
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
    private Float star;

    @Column(nullable = false)
    private int price;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @OneToMany(mappedBy = "video")
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "video")
    private List<Watch> watches = new ArrayList<>();

    @OneToMany(mappedBy = "video")
    private List<Cart> carts = new ArrayList<>();

    @OneToMany(mappedBy = "video")
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<VideoCategory> videoCategories = new ArrayList<>();

    @OneToMany(mappedBy = "video")
    private List<OrderVideo> orderVideos = new ArrayList<>();

    public static Video createVideo(Channel channel, String videoName, Integer price, String description, List<Category> categories) {

        Video video = Video.builder()
                .channel(channel)
                .videoName(videoName)
                .price(price)
                .description(description)
                .videoFile(videoName)
                .thumbnailFile(videoName)
                .view(0)
                .star(0f)
                .videoCategories(new ArrayList<>())
                .build();

        for (Category category : categories) {
            VideoCategory videoCategory = VideoCategory.createVideoCategory(video, category);
            video.addVideoCategory(videoCategory);
        }

        return video;
    }

    private void addVideoCategory(VideoCategory videoCategory) {
        this.videoCategories.add(videoCategory);
    }

    public void addView(){
        this.view++;
    }

    public void calculateStar(){
        double average = this.replies.stream().mapToDouble(Reply::getStar).average().orElse(0);

        this.star = (float) (Math.round(average * 10.0) / 10.0);
    }

    public void updateCategory(List<Category> categories) {

        if(categories == null) {
            return;
        }

        this.videoCategories.clear();
        for (Category category : categories) {
            VideoCategory videoCategory = VideoCategory.createVideoCategory(this, category);
            this.addVideoCategory(videoCategory);
        }
    }

    public void updateVideo(String videoName, Integer price, String description) {
        this.videoName = videoName == null ? this.videoName : videoName;
        this.price = price == null ? this.price : price;
        this.description = description == null ? this.description : description;
    }
}
