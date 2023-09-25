package com.server.domain.video.entity;

import com.server.domain.cart.entity.Cart;
import com.server.domain.category.entity.Category;
import com.server.domain.channel.entity.Channel;
import com.server.domain.order.entity.OrderVideo;
import com.server.domain.question.entity.Question;
import com.server.domain.reply.entity.Reply;
import com.server.domain.report.entity.Report;
import com.server.domain.report.entity.VideoReport;
import com.server.domain.reward.entity.Rewardable;
import com.server.domain.videoCategory.entity.VideoCategory;
import com.server.domain.watch.entity.Watch;
import com.server.global.entity.BaseEntity;
import com.server.global.exception.businessexception.videoexception.VideoAlreadyCreatedException;
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
public class Video extends BaseEntity implements Rewardable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long videoId;

    @Column(nullable = false)
    private String videoName;

    @Lob
    private String description;

    private String thumbnailFile;

    private String previewFile;

    private String videoFile;

    @Column(nullable = false)
    private int view;

    @Column(nullable = false)
    private Float star;

    @Column(nullable = false)
    private int price;

    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private VideoStatus videoStatus = VideoStatus.UPLOADING;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
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

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<VideoReport> videoReports = new ArrayList<>();


    public static Video createVideo(Channel channel, String videoName) {

        return Video.builder()
                .channel(channel)
                .videoName(videoName)
                .price(0)
                .description("uploading")
                .videoStatus(VideoStatus.UPLOADING)
                .view(0)
                .star(0f)
                .videoCategories(new ArrayList<>())
                .build();
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

    public void updateVideo(String description) {
        this.description = description == null ? this.description : description;
    }

    public void additionalCreateProcess(Integer price, String description, List<Category> categories, boolean hasPreview) {

        checkIsUploading();

        String filePath = getMemberId() + "/videos/" + this.videoId + "/" + this.videoName;


        this.price = price;
        this.description = description;
        this.videoStatus = VideoStatus.CREATED;
        this.thumbnailFile = filePath;
        this.videoFile = filePath;


        this.videoCategories.clear();
        for (Category category : categories) {
            VideoCategory videoCategory = VideoCategory.createVideoCategory(this, category);
            this.addVideoCategory(videoCategory);
        }

        if(price == 0) {
            this.getChannel().getMember().addReward(100);
            this.getChannel().getMember().addGradePoint(100);
        }

        if(hasPreview) {
            this.previewFile = getMemberId() + "/previews/" + this.videoId + "/" + this.videoName;
        }
    }

    public int getRewardPoint(){
        return (int) (price * 0.01);
    }

    public boolean isClosed() {
        return this.videoStatus == VideoStatus.CLOSED;
    }

    public boolean isAdminClosed() {
        return this.videoStatus == VideoStatus.ADMIN_CLOSED;
    }

    public void close() {
        this.videoStatus = VideoStatus.CLOSED;
    }

    public void adminClose() {
        this.videoStatus = VideoStatus.ADMIN_CLOSED;
    }

    public void open() {
        this.videoStatus = VideoStatus.CREATED;
    }

    public boolean isOwnedBy(Long memberId) {
        return this.channel.getMember().getMemberId().equals(memberId);
    }

    private void checkIsUploading() {
        if(this.videoStatus != VideoStatus.UPLOADING) {
            throw new VideoAlreadyCreatedException();
        }
    }

    public Long getMemberId() {
        return this.channel.getMember().getMemberId();
    }
}
