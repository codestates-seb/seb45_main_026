package com.server.intergration;

import java.util.ArrayList;
import java.util.Collections;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.auth.jwt.service.CustomUserDetails;
import com.server.auth.jwt.service.JwtProvider;
import com.server.domain.announcement.repository.AnnouncementRepository;
import com.server.domain.answer.repository.AnswerRepository;
import com.server.domain.cart.repository.CartRepository;
import com.server.domain.category.repository.CategoryRepository;
import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.respository.ChannelRepository;
import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.order.repository.OrderRepository;
import com.server.domain.question.repository.QuestionRepository;
import com.server.domain.reply.repository.ReplyRepository;
import com.server.domain.reward.repository.RewardRepository;
import com.server.domain.subscribe.repository.SubscribeRepository;
import com.server.domain.video.entity.Video;
import com.server.domain.video.entity.VideoStatus;
import com.server.domain.video.repository.VideoRepository;
import com.server.domain.videoCategory.entity.VideoCategoryRepository;
import com.server.domain.watch.repository.WatchRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
	@Autowired protected EntityManager entityManager;

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
			.nickname("test")
			.password(passwordEncoder.encode(password))
			.authority(Authority.ROLE_USER)
			.build();

		return memberRepository.save(member);
	}

	protected Member createMemberWithEmail(String email) {
		Member member = Member.builder()
			.email(email)
			.nickname("test")
			.password(passwordEncoder.encode("qwer1234!"))
			.authority(Authority.ROLE_USER)
			.build();

		return memberRepository.save(member);
	}

	protected Member createAndSaveMemberWithPassword(String password) {
		Member member = Member.builder()
			.email("test@email.com")
			.nickname("test")
			.password(passwordEncoder.encode(password))
			.authority(Authority.ROLE_USER)
			.build();

		return memberRepository.save(member);
	}

	protected Member createAndSaveMemberWithReward(int reward) {
		Member member = Member.builder()
			.email("test@email.com")
			.password("qwer1234!")
			.nickname("test")
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
			.nickname("test")
			.password(passwordEncoder.encode("qwer1234!"))
			.authority(Authority.ROLE_USER)
			.build();

		return memberRepository.save(member);
	}

	protected Channel createChannel(Member member) {
		Channel channel = Channel.builder()
			.member(member)
			.channelName("channelName")
			.build();

		return channelRepository.save(channel);
	}

	protected Video createAndSavePaidVideo(Channel channel, int price) {
		Video video = Video.builder()
			.videoName("videoName")
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
			.videoName("videoName")
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
}
