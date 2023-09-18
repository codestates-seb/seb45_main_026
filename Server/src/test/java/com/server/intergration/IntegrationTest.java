package com.server.intergration;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.auth.jwt.service.CustomUserDetails;
import com.server.auth.jwt.service.JwtProvider;
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
import com.server.domain.order.service.OrderService;
import com.server.domain.question.entity.Question;
import com.server.domain.question.repository.QuestionRepository;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.repository.ReplyRepository;
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
import com.server.domain.watch.entity.Watch;
import com.server.domain.watch.repository.WatchRepository;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import com.server.module.redis.service.RedisService;
import com.server.module.s3.service.AwsService;
import com.server.module.s3.service.dto.FileType;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class IntegrationTest {
	// 레포지토리
	@Autowired protected AnnouncementRepository announcementRepository;
	@Autowired protected AnswerRepository answerRepository;
	@Autowired protected CartRepository cartRepository;
	@Autowired protected CategoryRepository categoryRepository;
	@Autowired protected ChannelRepository channelRepository;
	@Autowired protected MemberRepository memberRepository;
	@Autowired protected OrderRepository orderRepository;
	@Autowired protected QuestionRepository questionRepository;
	@Autowired protected ReplyRepository replyRepository;
	@Autowired protected RewardRepository rewardRepository;
	@Autowired protected SubscribeRepository subscribeRepository;
	@Autowired protected VideoRepository videoRepository;
	@Autowired protected VideoCategoryRepository videoCategoryRepository;
	@Autowired protected WatchRepository watchRepository;

	// 인증
	@Autowired protected PasswordEncoder passwordEncoder;
	@Autowired protected JwtProvider jwtProvider;

	// 테스트 유틸 클래스
	@Autowired protected MockMvc mockMvc;
	@Autowired protected ObjectMapper objectMapper;
	@Autowired protected RestTemplate restTemplate;
	@Autowired protected EntityManager em;

	// AWS
	@Autowired protected AwsService awsService;

	// Mock
	@MockBean protected AwsService mockAwsService;

	// 이메일 및 레디스
	@Autowired protected RedisService redisService;
	@Autowired protected StringRedisTemplate stringRedisTemplate;

	protected void flushAll() {
		memberRepository.flush();
		channelRepository.flush();
		announcementRepository.flush();
		subscribeRepository.flush();
		categoryRepository.flush();
		videoCategoryRepository.flush();
		videoRepository.flush();
		questionRepository.flush();
		answerRepository.flush();
		cartRepository.flush();
		orderRepository.flush();
		replyRepository.flush();
		watchRepository.flush();
		rewardRepository.flush();
		memberRepository.flush();
	}

	protected void deleteAll() {
		rewardRepository.deleteAll();
		watchRepository.deleteAll();
		replyRepository.deleteAll();
		orderRepository.deleteAll();
		cartRepository.deleteAll();
		answerRepository.deleteAll();
		questionRepository.deleteAll();
		categoryRepository.deleteAll();
		videoCategoryRepository.deleteAll();
		videoRepository.deleteAll();
		subscribeRepository.deleteAll();
		announcementRepository.deleteAll();
		channelRepository.deleteAll();
		memberRepository.deleteAll();
		em.flush();
		em.clear();
	}

	protected String createAccessToken(Member member, long accessTokenExpireTime) {
		UserDetails userDetails = createUserDetails(member);

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
			userDetails, null, userDetails.getAuthorities());

		return jwtProvider.createAccessToken(authenticationToken, accessTokenExpireTime);
	}

	protected String createRefreshToken(Member member, long refreshTokenExpireTime) {
		UserDetails userDetails = createUserDetails(member);

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
			userDetails, null, userDetails.getAuthorities());

		return jwtProvider.createRefreshToken(authenticationToken, refreshTokenExpireTime);
	}

	protected UsernamePasswordAuthenticationToken createAuthenticationToken() {
		Member member = createAndSaveMember();

		UserDetails userDetails = createUserDetails(member);

		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	protected UserDetails createUserDetails(Member member) {
		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getAuthority().toString());

		return new CustomUserDetails(
			member.getMemberId(),
			String.valueOf(member.getEmail()),
			member.getPassword(),
			Collections.singleton(grantedAuthority)
		);
	}

	protected Member createAndSaveMemberWithEmailPassword(String email, String password){

		Member member = Member.builder()
			.email(email)
			.nickname(generateRandomString())
			.password(passwordEncoder.encode(password))
			.imageFile("imageFile")
			.authority(Authority.ROLE_USER)
			.build();

		return memberRepository.save(member);
	}

	protected Member createAndSaveMemberWithEmailPasswordReward(String email, String password, int reward){

		Member member = Member.builder()
			.email(email)
			.nickname(generateRandomString())
			.password(passwordEncoder.encode(password))
			.imageFile("imageFile")
			.reward(reward)
			.authority(Authority.ROLE_USER)
			.build();

		return memberRepository.save(member);
	}

	protected Member createMemberWithEmail(String email) {
		Member member = Member.builder()
			.email(email)
			.nickname(generateRandomString())
			.password(passwordEncoder.encode("qwer1234!"))
			.authority(Authority.ROLE_USER)
			.build();

		return memberRepository.save(member);
	}

	protected Member createAndSaveMemberWithPassword(String password) {
		Member member = Member.builder()
			.email("test@email.com")
			.nickname(generateRandomString())
			.password(passwordEncoder.encode(password))
			.authority(Authority.ROLE_USER)
			.build();

		return memberRepository.save(member);
	}

	protected Member createAndSaveMemberWithReward(int reward) {
		Member member = Member.builder()
			.email("test@email.com")
			.password("qwer1234!")
			.nickname(generateRandomString())
			.authority(Authority.ROLE_USER)
			.reward(reward)
			.imageFile("imageFile")
			.build();

		memberRepository.save(member);

		return member;
	}

	protected Member createAndSaveMember() {
		Member member = Member.builder()
			.email("test@email.com")
			.nickname(generateRandomString())
			.password(passwordEncoder.encode("qwer1234!"))
			.authority(Authority.ROLE_USER)
			.build();

		return memberRepository.save(member);
	}

	protected Channel createChannel(Member member) {
		Channel channel = Channel.builder()
			.member(member)
			.channelName("channelName")
			.description("channelDescription")
			.build();

		return channelRepository.save(channel);
	}

	protected Channel createChannelWithRandomName(Member member) {
		Channel channel = Channel.builder()
			.member(member)
			.channelName(generateRandomString())
			.build();

		return channelRepository.save(channel);
	}

	protected Video createAndSavePaidVideo(Channel channel, int price) {
		Video video = Video.builder()
			.videoName(generateRandomString())
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

		return videoRepository.save(video);
	}

	protected Video createAndSaveFreeVideo(Channel channel) {
		Video video = Video.builder()
			.videoName(generateRandomString())
			.description("description")
			.thumbnailFile("thumbnailFile")
			.videoFile("videoFile")
			.view(0)
			.star(0.0F)
			.price(0)
			.videoCategories(new ArrayList<>())
			.videoStatus(VideoStatus.CREATED)
			.channel(channel)
			.questions(new ArrayList<>())
			.build();

		return videoRepository.save(video);
	}

	protected Video createAndSaveClosedVideo(Channel channel) {
		Video video = Video.builder()
			.videoName(generateRandomString())
			.description("description")
			.thumbnailFile("thumbnailFile")
			.videoFile("videoFile")
			.view(0)
			.star(0.0F)
			.price(5000)
			.videoCategories(new ArrayList<>())
			.videoStatus(VideoStatus.CLOSED)
			.channel(channel)
			.questions(new ArrayList<>())
			.build();

		return videoRepository.save(video);
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

	protected Subscribe createAndSaveSubscribe(Member member, Channel channel) {
		Subscribe subscribe = Subscribe.builder()
			.member(member)
			.channel(channel)
			.build();

		channel.addSubscriber();

		subscribeRepository.save(subscribe);

		return subscribe;
	}

	protected Reply createAndSaveReply(Member member, Video video) {
		Reply reply = Reply.builder()
			.content("content")
			.star(generateRandomStarInteger())
			.member(member)
			.video(video)
			.build();

		replyRepository.save(reply);

		return reply;
	}

	protected Reward createAndSaveReward(Member member, Rewardable rewardable) {

		Reward reward = Reward.createReward(rewardable.getRewardPoint(), member, rewardable);

		rewardRepository.save(reward);

		return reward;
	}

	protected Cart createAndSaveCart(Member member, Video video) {
		Cart cart = Cart.createCart(member, video, video.getPrice());

		return cartRepository.save(cart);
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

	protected Answer createAndSaveAnswer(Member loginMember, Question question) {

		Answer answer = Answer.createAnswer("1", loginMember, question);

		return answerRepository.save(answer);
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

	protected Watch createAndSaveWatch(Member member, Video video) {
		Watch watch = Watch.builder()
			.member(member)
			.video(video)
			.build();

		return watchRepository.save(watch);
	}

	protected String getProfileUrl(Member member) {
		return awsService.getFileUrl(
			member.getImageFile(),
			FileType.PROFILE_IMAGE
		);
	}

	protected String getThumbnailUrl(Video video) {
		return awsService.getFileUrl(
			video.getThumbnailFile(),
			FileType.THUMBNAIL
		);
	}

	protected String getVideoUrl(Video video) {
		return awsService.getFileUrl(
			video.getVideoFile(),
			FileType.VIDEO
		);
	}

	protected <T> ApiSingleResponse<T> getApiSingleResponseFromResult(ResultActions actions, Class<T> clazz) throws
		UnsupportedEncodingException,
		JsonProcessingException {
		String contentAsString = actions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

		JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ApiSingleResponse.class, clazz);

		return objectMapper.readValue(contentAsString, javaType);
	}

	protected <T> ApiSingleResponse<List<T>> getApiSingleListResponseFromResult(ResultActions actions, Class<T> clazz) throws UnsupportedEncodingException {
		String jsonData = actions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

		try {
			JavaType responseType = objectMapper.getTypeFactory().constructParametricType(ApiSingleResponse.class,
				objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));

			return objectMapper.readValue(jsonData, responseType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected <T> ApiPageResponse<T> getApiPageResponseFromResult(ResultActions actions, Class<T> clazz) throws UnsupportedEncodingException, JsonProcessingException {
		String contentAsString = actions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

		JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ApiPageResponse.class, clazz);

		return objectMapper.readValue(contentAsString, javaType);
	}

	private String generateRandomString() {
		String characters = "abcdefghijklmnopqrstuvwxyz123456789";
		StringBuilder randomString = new StringBuilder(10);
		Random random = new SecureRandom();

		for (int i = 0; i < 10; i++) {
			int randomIndex = random.nextInt(characters.length());
			char randomChar = characters.charAt(randomIndex);
			randomString.append(randomChar);
		}

		return randomString.toString();
	}

	private Float generateRandomStarFloat() {
		return Math.round((0.0f + (10.0f - 0.0f) * new Random().nextFloat()) * 10.0f) / 10.0f;
	}

	private Integer generateRandomStarInteger() {
		return new Random().nextInt(10) + 1;
	}
}
