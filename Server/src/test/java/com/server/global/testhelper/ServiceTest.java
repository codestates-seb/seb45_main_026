package com.server.global.testhelper;

import com.server.domain.category.entity.Category;
import com.server.domain.category.entity.CategoryRepository;
import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.respository.ChannelRepository;
import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.order.entity.Order;
import com.server.domain.order.repository.OrderRepository;
import com.server.domain.subscribe.entity.Subscribe;
import com.server.domain.subscribe.repository.SubscribeRepository;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import com.server.domain.videoCategory.entity.VideoCategory;
import com.server.domain.videoCategory.entity.VideoCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class ServiceTest {


    @Autowired protected MemberRepository memberRepository;
    @Autowired protected VideoRepository videoRepository;
    @Autowired protected OrderRepository orderRepository;
    @Autowired protected ChannelRepository channelRepository;
    @Autowired protected VideoCategoryRepository videoCategoryRepository;
    @Autowired protected CategoryRepository categoryRepository;
    @Autowired protected SubscribeRepository subscribeRepository;
    @Autowired protected EntityManager em;

    protected void flush(){
        memberRepository.flush();
        videoRepository.flush();
        orderRepository.flush();
        channelRepository.flush();
        videoCategoryRepository.flush();
        categoryRepository.flush();
        subscribeRepository.flush();
    }

    protected Member createAndSaveMember() {
        Member member = Member.builder()
                .email("test@gmail.com")
                .password("1q2w3e4r!")
                .nickname("test")
                .authority(Authority.ROLE_USER)
                .reward(1000)
                .imageFile("imageFile")
                .build();

        memberRepository.save(member);

        return member;
    }

    protected Channel createAndSaveChannel(Member member) {
        Channel channel = Channel.createChannel("channelName");
        channel.setMember(member);
        channelRepository.save(channel);

        return channel;
    }

    protected Video createAndSaveVideo(Channel channel) {
        Video video = Video.builder()
                .videoName("title")
                .description("description")
                .thumbnailFile("thumbnailFile")
                .videoFile("videoFile")
                .view(0)
                .star(0.0F)
                .price(1000)
                .channel(channel)
                .build();

        videoRepository.save(video);
        videoRepository.flush();

        return video;
    }

    protected Video createAndSavePurchasedVideo(Member member, Channel channel) {
        Video video = Video.builder()
                .videoName("title")
                .description("description")
                .thumbnailFile("thumbnailFile")
                .videoFile("videoFile")
                .view(0)
                .star(0.0F)
                .price(1000)
                .channel(channel)
                .build();

        Order order = Order.createOrder(member, List.of(video), 0);
        order.completeOrder();

        videoRepository.save(video);
        orderRepository.save(order);

        return video;
    }

    protected Order createAndSaveOrder(Member member, List<Video> video, int reward) {
        Order order = Order.createOrder(member, video, reward);

        orderRepository.save(order);

        return order;
    }

    protected Order createAndSaveOrderWithPurchase(Member member, List<Video> video, int reward) {
        Order order = Order.createOrder(member, video, reward);
        order.completeOrder();
        orderRepository.save(order);

        return order;
    }

    protected void createAndSaveVideoCategory(Video video, Category category) {

        VideoCategory videoCategory = VideoCategory.builder()
                .video(video)
                .category(category)
                .build();

        videoCategoryRepository.save(videoCategory);
    }

    protected Category createAndSaveCategory(String categoryName) {
        Category category = Category.builder()
                .categoryName(categoryName)
                .build();

        categoryRepository.save(category);

        return category;
    }

    protected Subscribe createAndSaveSubscribe(Member member, Channel channel) {
        Subscribe subscribe = Subscribe.builder()
                .member(member)
                .channel(channel)
                .build();

        subscribeRepository.save(subscribe);

        return subscribe;
    }
}
