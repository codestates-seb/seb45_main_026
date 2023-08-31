package com.server.global.testhelper;

import com.server.domain.announcement.repository.AnnouncementRepository;
import com.server.domain.answer.repository.AnswerRepository;
import com.server.domain.cart.repository.CartRepository;
import com.server.domain.category.entity.Category;
import com.server.domain.category.entity.CategoryRepository;
import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.respository.ChannelRepository;
import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.order.entity.Order;
import com.server.domain.order.repository.OrderRepository;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.repository.ReplyRepository;
import com.server.domain.subscribe.entity.Subscribe;
import com.server.domain.subscribe.repository.SubscribeRepository;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import com.server.domain.videoCategory.entity.VideoCategory;
import com.server.domain.videoCategory.entity.VideoCategoryRepository;
import com.server.domain.watch.repository.WatchRepository;
import com.server.module.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import java.util.ArrayList;
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
    @Autowired protected AnswerRepository answerRepository;
    @Autowired protected WatchRepository watchRepository;
    @Autowired protected ReplyRepository replyRepository;
    @Autowired protected AnnouncementRepository announcementRepository;
    @Autowired protected CartRepository cartRepository;
    @Autowired protected EntityManager em;

    @MockBean protected RedisService redisService;
    @MockBean protected RestTemplate restTemplate;

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
                .videoCategories(new ArrayList<>())
                .channel(channel)
                .build();

        videoRepository.save(video);

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

    protected Reply createAndSaveReply(Member member, Video video) {
        Reply reply = new Reply();
        reply.setMember(member);
        reply.setVideo(video);
        reply.setContent("content");

        replyRepository.save(reply);

        return reply;
    }
}
