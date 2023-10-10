package com.server.global.testhelper;

import com.server.auth.jwt.service.CustomUserDetails;
import com.server.auth.jwt.service.JwtProvider;
import com.server.domain.account.repository.AccountRepository;
import com.server.domain.announcement.repository.AnnouncementRepository;
import com.server.domain.answer.entity.Answer;
import com.server.domain.answer.repository.AnswerRepository;
import com.server.domain.cart.entity.Cart;
import com.server.domain.cart.repository.CartRepository;
import com.server.domain.category.entity.Category;
import com.server.domain.category.repository.CategoryRepository;
import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.respository.ChannelRepository;
import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.order.entity.Order;
import com.server.domain.order.repository.OrderRepository;
import com.server.domain.question.entity.Question;
import com.server.domain.question.repository.QuestionRepository;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.repository.ReplyRepository;
import com.server.domain.report.repository.ReportRepository;
import com.server.domain.reward.entity.Reward;
import com.server.domain.reward.entity.Rewardable;
import com.server.domain.reward.repository.RewardRepository;
import com.server.domain.subscribe.entity.Subscribe;
import com.server.domain.subscribe.repository.SubscribeRepository;
import com.server.domain.video.entity.Video;
import com.server.domain.video.entity.VideoStatus;
import com.server.domain.video.repository.VideoRepository;
import com.server.domain.videoCategory.entity.VideoCategory;
import com.server.domain.videoCategory.entity.VideoCategoryRepository;
import com.server.domain.watch.repository.WatchRepository;
import com.server.module.email.service.MailService;
import com.server.module.redis.service.RedisService;
import com.server.module.s3.service.AwsService;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class ServiceTest {


    @Autowired protected MemberRepository memberRepository;
    @Autowired protected VideoRepository videoRepository;
    @Autowired protected OrderRepository orderRepository;
    @Autowired protected ChannelRepository channelRepository;
    @Autowired protected QuestionRepository questionRepository;
    @Autowired protected VideoCategoryRepository videoCategoryRepository;
    @Autowired protected CategoryRepository categoryRepository;
    @Autowired protected SubscribeRepository subscribeRepository;
    @Autowired protected AnswerRepository answerRepository;
    @Autowired protected WatchRepository watchRepository;
    @Autowired protected ReplyRepository replyRepository;
    @Autowired protected AnnouncementRepository announcementRepository;
    @Autowired protected CartRepository cartRepository;
    @Autowired protected RewardRepository rewardRepository;
    @Autowired protected ReportRepository reportRepository;
    @Autowired protected AccountRepository accountRepository;
    @Autowired protected EntityManager em;

    @MockBean protected RedisService redisService;
    @MockBean protected RestTemplate restTemplate;
    @MockBean protected MailService mailService;
    @MockBean protected AwsService awsService;
    @MockBean protected DefaultOAuth2UserService defaultOAuth2UserService;
    @Mock protected JwtProvider jwtProvider;
    @Mock protected AuthenticationManager authenticationManager;

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

    protected Member createMemberWithChannel() {
        Member member = Member.builder()
                .email("test@gmail.com")
                .password("1q2w3e4r!")
                .nickname("test")
                .authority(Authority.ROLE_USER)
                .reward(1000)
                .imageFile("imageFile")
                .build();

        memberRepository.save(member);

        createAndSaveChannel(member);

        return member;
    }

    protected Member createAdminWithChannel() {
        Member member = Member.builder()
                .email("test@gmail.com")
                .password("1q2w3e4r!")
                .nickname("test")
                .authority(Authority.ROLE_ADMIN)
                .reward(1000)
                .imageFile("imageFile")
                .build();

        memberRepository.save(member);

        createAndSaveChannel(member);

        return member;
    }

    protected Channel createAndSaveChannel(Member member) {
        Channel channel = Channel.createChannel("channelName");
        channel.setMember(member);
        channelRepository.save(channel);

        return channel;
    }

    protected Channel createAndSaveChannelWithName(Member member, String channelName) {
        Channel channel = Channel.createChannel(channelName);
        channel.setMember(member);
        channelRepository.save(channel);

        return channel;
    }

    protected Channel createAndSaveChannelWithSubscriber(Member member, int subscriber) {
        Channel channel = Channel.builder().channelName("channelName").subscribers(subscriber).build();
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
                .previewFile("previewFile")
                .view(0)
                .star(0.0F)
                .price(1000)
                .videoCategories(new ArrayList<>())
                .videoStatus(VideoStatus.CREATED)
                .channel(channel)
                .questions(new ArrayList<>())
                .build();

        videoRepository.save(video);

        return video;
    }

    protected Video createAndSaveVideo(Channel channel, int price) {
        Video video = Video.builder()
                .videoName("title")
                .description("description")
                .thumbnailFile("thumbnailFile")
                .videoFile("videoFile")
                .view(0)
                .star(0.0F)
                .price(price)
                .videoCategories(new ArrayList<>())
                .videoStatus(VideoStatus.CREATED)
                .channel(channel)
                .questions(new ArrayList<>())
                .build();

        videoRepository.save(video);

        return video;
    }

    protected Video createAndSavePurchasedVideo(Member member) {
        Video video = Video.builder()
                .videoName("title")
                .description("description")
                .thumbnailFile("thumbnailFile")
                .videoFile("videoFile")
                .view(0)
                .star(0.0F)
                .price(1000)
                .videoStatus(VideoStatus.CREATED)
                .channel(member.getChannel())
                .build();

        Order order = Order.createOrder(member, List.of(video), 0);
        order.completeOrder(LocalDateTime.now(), "paymentKey");

        videoRepository.save(video);
        orderRepository.save(order);

        return video;
    }

    protected Question createAndSaveQuestion(Video video) {
        Question question = Question.builder()
                .position(1)
                .content("content")
                .questionAnswer("1")
                .selections(List.of("1", "2", "3", "4", "5"))
                .video(video)
                .build();

        questionRepository.save(question);

        return question;
    }

    protected Order createAndSaveOrder(Member member, List<Video> video, int reward) {
        Order order = Order.createOrder(member, video, reward);

        orderRepository.save(order);

        return order;
    }

    protected Order createAndSaveOrderWithPurchaseComplete(Member member, List<Video> video, int reward) {
        Order order = Order.createOrder(member, video, reward);
        order.completeOrder(LocalDateTime.now(), "paymentKey");
        orderRepository.save(order);

        return order;
    }

    protected void createAndSaveVideoCategory(Video video, Category... categories) {

        for (Category category : categories) {
            VideoCategory videoCategory = VideoCategory.builder()
                    .video(video)
                    .category(category)
                    .build();

            videoCategoryRepository.save(videoCategory);
        }
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
        Reply reply = Reply.builder()
                .content("content")
                .star(0)
                .member(member)
                .video(video)
                .build();

        replyRepository.save(reply);

        return reply;
    }

    protected Reply createAndSaveReply5Star(Member member, Video video) {
        Reply reply = Reply.builder()
            .content("content")
            .star(5)
            .member(member)
            .video(video)
            .build();

        replyRepository.save(reply);

        return reply;
    }

    protected Reward createAndSaveReward(Member member, Rewardable rewardable) {

        Reward reward = Reward.createReward(rewardable.getRewardPoint(), member, rewardable);

        em.persist(reward);

        return reward;
    }

    protected Cart createAndSaveCart(Member member, Video video) {
        Cart cart = Cart.createCart(member, video, video.getPrice());

        return cartRepository.save(cart);
    }

    protected Answer createAndSaveAnswer(Member loginMember, Question question) {

        Answer answer = Answer.createAnswer("1", loginMember, question);

        return answerRepository.save(answer);
    }

    protected UserDetails getUserDetails(Member member) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getAuthority().toString());

        return new CustomUserDetails(
            member.getMemberId(),
            String.valueOf(member.getEmail()),
            member.getPassword(),
            Collections.singleton(grantedAuthority)
        );
    }

    protected UsernamePasswordAuthenticationToken getAuthenticationToken() {
        Member member = createAndSaveMember();

        UserDetails userDetails = getUserDetails(member);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    protected void setAuthentication(Member member) {

        String authority = member.getAuthority().toString();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                member.getMemberId(),
                null,
                List.of(new SimpleGrantedAuthority(authority)));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
