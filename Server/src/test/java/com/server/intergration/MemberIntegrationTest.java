package com.server.intergration;

import static com.server.auth.util.AuthConstant.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.*;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.server.domain.answer.entity.Answer;
import com.server.domain.cart.entity.Cart;
import com.server.domain.category.service.dto.response.CategoryResponse;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.controller.dto.MemberApiRequest;
import com.server.domain.member.entity.Member;
import com.server.domain.member.service.dto.response.CartsResponse;
import com.server.domain.member.service.dto.response.OrdersResponse;
import com.server.domain.member.service.dto.response.PlaylistChannelDetailsResponse;
import com.server.domain.member.service.dto.response.PlaylistChannelResponse;
import com.server.domain.member.service.dto.response.PlaylistsResponse;
import com.server.domain.member.service.dto.response.ProfileResponse;
import com.server.domain.member.service.dto.response.RewardsResponse;
import com.server.domain.member.service.dto.response.SubscribesResponse;
import com.server.domain.member.service.dto.response.WatchsResponse;
import com.server.domain.order.entity.Order;
import com.server.domain.question.entity.Question;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reward.entity.Reward;
import com.server.domain.subscribe.entity.Subscribe;
import com.server.domain.video.entity.Video;
import com.server.domain.watch.entity.Watch;
import com.server.global.exception.businessexception.answerexception.AnswerNotFoundException;
import com.server.global.exception.businessexception.orderexception.OrderNotFoundException;
import com.server.global.exception.businessexception.replyException.ReplyNotFoundException;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.PageInfo;
import com.server.module.s3.service.dto.ImageType;

import javax.persistence.EntityManager;

@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemberIntegrationTest extends IntegrationTest {

	// 로그인한 사용자 정보
	Member loginMember;
	Channel loginMemberChannel;
	List<Video> loginMemberVideos = new ArrayList<>();
	List<Order> loginMemberOrders = new ArrayList<>();
	List<Cart> loginMemberCarts = new ArrayList<>();
	List<Subscribe> loginMemberSubscribes = new ArrayList<>();
	List<Watch> loginMemberWatches = new ArrayList<>();
	List<Reply> loginMemberReplies = new ArrayList<>();
	List<Reward> loginMemberRewards = new ArrayList<>();
	List<Video> loginMemberPlaylist = new ArrayList<>();
	String loginMemberEmail = "login@email.com";
	String loginMemberPassword = "qwer1234!";
	String loginMemberAccessToken;

	// 다른 사용자들의 정보
	Member otherMember1;
	Member otherMember2;
	Member otherMember3;
	Member otherMember4;

	Channel otherMemberChannel1;
	Channel otherMemberChannel2;
	Channel otherMemberChannel3;
	Channel otherMemberChannel4;

	List<Video> otherMemberVideos1 = new ArrayList<>();
	List<Video> otherMemberVideos2 = new ArrayList<>();
	List<Video> otherMemberVideos3 = new ArrayList<>();
	List<Video> otherMemberVideos4 = new ArrayList<>();

	List<Question> otherMemberQuestions1 = new ArrayList<>();
	List<Question> otherMemberQuestions2 = new ArrayList<>();
	List<Question> otherMemberQuestions3 = new ArrayList<>();
	List<Question> otherMemberQuestions4 = new ArrayList<>();

	String otherMemberEmail1 = "other1@email.com";
	String otherMemberEmail2 = "other2@email.com";
	String otherMemberEmail3 = "other3@email.com";
	String otherMemberEmail4 = "other4@email.com";
	String otherMemberPassword = "other1234!";

	@BeforeAll
	void before() {

		loginMember = createAndSaveMemberWithEmailPassword(loginMemberEmail, loginMemberPassword);
		loginMemberChannel = createChannelWithRandomName(loginMember);

		for (int i = 0; i < 2; i++) {
			loginMemberVideos.add(createAndSaveFreeVideo(loginMemberChannel));
			loginMemberVideos.add(createAndSavePaidVideo(loginMemberChannel, 10000));
		}

		loginMemberAccessToken = BEARER + createAccessToken(loginMember, 360000);

		otherMember1 = createAndSaveMemberWithEmailPassword(otherMemberEmail1, otherMemberPassword);
		otherMember2 = createAndSaveMemberWithEmailPassword(otherMemberEmail2, otherMemberPassword);
		otherMember3 = createAndSaveMemberWithEmailPassword(otherMemberEmail3, otherMemberPassword);
		otherMember4 = createAndSaveMemberWithEmailPassword(otherMemberEmail4, otherMemberPassword);

		otherMemberChannel1 = createChannelWithRandomName(otherMember1);
		otherMemberChannel2 = createChannelWithRandomName(otherMember2);
		otherMemberChannel3 = createChannelWithRandomName(otherMember3);
		otherMemberChannel4 = createChannelWithRandomName(otherMember4);

		for (int i = 0; i < 3; i++) {
			otherMemberVideos1.add(createAndSavePaidVideo(otherMemberChannel1, 10000));
			otherMemberVideos2.add(createAndSavePaidVideo(otherMemberChannel2, 10000));
			otherMemberVideos3.add(createAndSavePaidVideo(otherMemberChannel3, 10000));
			otherMemberVideos4.add(createAndSavePaidVideo(otherMemberChannel4, 10000));
		}

		for (int i = 0; i < 2; i++) {
			otherMemberVideos1.add(createAndSaveFreeVideo(otherMemberChannel1));
			otherMemberVideos2.add(createAndSaveFreeVideo(otherMemberChannel2));
			otherMemberVideos3.add(createAndSaveFreeVideo(otherMemberChannel3));
			otherMemberVideos4.add(createAndSaveFreeVideo(otherMemberChannel4));
		}

		for (int i = 0; i < otherMemberVideos1.size(); i++) {
			List<Video> videos = List.of(
				otherMemberVideos1.get(i),
				otherMemberVideos2.get(i),
				otherMemberVideos3.get(i)
			);

			loginMemberPlaylist.addAll(videos);

			loginMemberOrders.add(
				createAndSaveOrderWithPurchaseComplete(loginMember, videos, 0)
			);
		}

		loginMemberCarts.add(createAndSaveCart(loginMember, otherMemberVideos1.get(0)));
		loginMemberCarts.add(createAndSaveCart(loginMember, otherMemberVideos1.get(1)));
		loginMemberCarts.add(createAndSaveCart(loginMember, otherMemberVideos1.get(2)));

		loginMemberSubscribes.add(createAndSaveSubscribe(loginMember, otherMemberChannel1));
		loginMemberSubscribes.add(createAndSaveSubscribe(loginMember, otherMemberChannel2));

		for (int i = 0; i < 5; i+=2) {
			loginMemberWatches.add(createAndSaveWatch(loginMember, otherMemberVideos1.get(i)));
			loginMemberWatches.add(createAndSaveWatch(loginMember, otherMemberVideos2.get(i)));
			loginMemberWatches.add(createAndSaveWatch(loginMember, otherMemberVideos3.get(i)));
		}

		for (int i = 0; i < 3; i++) {
			loginMemberReplies.add(createAndSaveReply(loginMember, otherMemberVideos1.get(i)));
			loginMemberReplies.add(createAndSaveReply(loginMember, otherMemberVideos2.get(i)));
			loginMemberReplies.add(createAndSaveReply(loginMember, otherMemberVideos3.get(i)));
		}

		for (int i = 0; i < 5; i++) {
			Question question1 = createAndSaveQuestion(otherMemberVideos1.get(i));
			Question question2 = createAndSaveQuestion(otherMemberVideos2.get(i));
			Question question3 = createAndSaveQuestion(otherMemberVideos3.get(i));
			Question question4 = createAndSaveQuestion(otherMemberVideos4.get(i));

			otherMemberQuestions1.add(question1);
			otherMemberQuestions2.add(question2);
			otherMemberQuestions3.add(question3);
			otherMemberQuestions4.add(question4);
		}

		for (int i = 0; i < 5; i++) {
			loginMemberRewards.add(createAndSaveReward(loginMember, otherMemberQuestions1.get(i)));
			loginMemberRewards.add(createAndSaveReward(loginMember, otherMemberQuestions2.get(i)));
			loginMemberRewards.add(createAndSaveReward(loginMember, otherMemberQuestions3.get(i)));
			loginMemberRewards.add(createAndSaveReward(loginMember, otherMemberQuestions4.get(i)));

			loginMemberRewards.add(createAndSaveReward(loginMember, otherMemberVideos1.get(i)));
			loginMemberRewards.add(createAndSaveReward(loginMember, otherMemberVideos2.get(i)));
			loginMemberRewards.add(createAndSaveReward(loginMember, otherMemberVideos3.get(i)));
			loginMemberRewards.add(createAndSaveReward(loginMember, otherMemberVideos4.get(i)));
		}

		for (Reply loginMemberReply : loginMemberReplies) {
			loginMemberRewards.add(createAndSaveReward(loginMember, loginMemberReply));
		}

		memberRepository.saveAll(List.of(
			loginMember,
			otherMember1,
			otherMember2,
			otherMember3,
			otherMember4
		));

		channelRepository.saveAll(List.of(
			otherMemberChannel1,
			otherMemberChannel2,
			otherMemberChannel3,
			otherMemberChannel4
		));
	}

	@Test
	@DisplayName("자신의 프로필이미지, 이메일, 닉네임을 조회한다.")
	void getMember() throws Exception {

		ResultActions actions = mockMvc.perform(
			get("/members")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.accept(APPLICATION_JSON)
		);

		actions
			.andDo(print())
			.andExpect(status().isOk());

		em.flush();
		em.clear();

		ProfileResponse profileResponse = getApiSingleResponseFromResult(actions, ProfileResponse.class).getData();

		Member member = memberRepository.findById(loginMember.getMemberId()).orElseThrow();

		assertThat(profileResponse.getMemberId()).isEqualTo(member.getMemberId());
		assertThat(profileResponse.getEmail()).isEqualTo(member.getEmail());
		assertThat(profileResponse.getNickname()).isEqualTo(member.getNickname());
		assertThat(profileResponse.getImageUrl()).isEqualTo(getProfileUrl(member));
		assertThat(profileResponse.getGrade()).isEqualTo(member.getGrade());
		assertThat(profileResponse.getReward()).isEqualTo(member.getReward());
	}

	@Test
	@DisplayName("자신의 리워드 목록을 조회한다.")
	void getRewards() throws Exception {
		// given
		List<Reward> rewards = memberRepository.findById(loginMember.getMemberId()).orElseThrow().getRewards();

		int totalSize = rewards.size();

		// when
		ResultActions actions = mockMvc.perform(
			get("/members/rewards")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.param("page","1")
				.param("size","16")
				.accept(APPLICATION_JSON)
		);

		em.flush();
		em.clear();

		// then
		actions
			.andDo(print())
			.andExpect(status().isOk());

		ApiPageResponse<RewardsResponse> rewardsResponse =
			getApiPageResponseFromResult(actions, RewardsResponse.class);

		PageInfo pageInfo = rewardsResponse.getPageInfo();
		List<RewardsResponse> responses = rewardsResponse.getData();

		// 비교할 응답값과 예상결과
		RewardsResponse firstContent = responses.get(0);
		Reward firstReward = rewards.get(totalSize - 1);

		assertThat(pageInfo.getTotalPage()).isEqualTo((int) Math.ceil(totalSize / 16.0));
		assertThat(pageInfo.getPage()).isEqualTo(1);
		assertThat(pageInfo.getSize()).isEqualTo(16);
		assertThat(pageInfo.getTotalSize()).isEqualTo(totalSize);

		assertThat(responses).isSortedAccordingTo(Comparator.comparing(RewardsResponse::getCreatedDate).reversed());

		assertThat(firstContent.getRewardId()).isEqualTo(firstReward.getRewardId());
		assertThat(firstContent.getRewardType()).isEqualTo(firstReward.getRewardType());
		assertThat(firstContent.getRewardPoint()).isEqualTo(firstReward.getRewardPoint());
	}

	@Test
	@DisplayName("자신의 구독 목록을 조회한다.")
	void getSubscribes() throws Exception {
		// given
		List<Subscribe> subscribes = memberRepository.findById(loginMember.getMemberId()).orElseThrow().getSubscribes();

		int totalSize = subscribes.size();

		// when
		ResultActions actions = mockMvc.perform(
			get("/members/subscribes")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.param("page","1")
				.param("size","16")
				.accept(APPLICATION_JSON)
		);

		em.flush();
		em.clear();

		// then
		actions
			.andDo(print())
			.andExpect(status().isOk());

		ApiPageResponse<SubscribesResponse> subscribesResponse =
			getApiPageResponseFromResult(actions, SubscribesResponse.class);

		PageInfo pageInfo = subscribesResponse.getPageInfo();
		List<SubscribesResponse> responses = subscribesResponse.getData();

		SubscribesResponse firstContent = responses.get(0);

		assertThat(pageInfo.getTotalPage()).isEqualTo((int) Math.ceil(totalSize / 16.0));
		assertThat(pageInfo.getPage()).isEqualTo(1);
		assertThat(pageInfo.getSize()).isEqualTo(16);
		assertThat(pageInfo.getTotalSize()).isEqualTo(totalSize);

		Channel findChannel = channelRepository.findById(firstContent.getMemberId()).orElseThrow();

		assertThat(firstContent.getMemberId()).isEqualTo(findChannel.getMember().getMemberId());
		assertThat(firstContent.getChannelName()).isEqualTo(findChannel.getChannelName());
		assertThat(firstContent.getImageUrl()).isEqualTo(getProfileUrl(findChannel.getMember()));
		assertThat(firstContent.getSubscribes()).isEqualTo(findChannel.getSubscribers());
	}

	@Test
	@DisplayName("자신의 장바구니 목록을 조회한다.")
	void getCarts() throws Exception {
		// given
		List<Cart> carts = memberRepository.findById(loginMember.getMemberId()).orElseThrow().getCarts();

		int totalSize = carts.size();

		ResultActions actions = mockMvc.perform(
			get("/members/carts")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.param("page","1")
				.param("size","20")
				.accept(APPLICATION_JSON)
		);

		em.flush();
		em.clear();

		actions
			.andDo(print())
			.andExpect(status().isOk());

		ApiPageResponse<CartsResponse> cartsResponse =
			getApiPageResponseFromResult(actions, CartsResponse.class);

		PageInfo pageInfo = cartsResponse.getPageInfo();
		List<CartsResponse> responses = cartsResponse.getData();

		CartsResponse firstContent = responses.get(0);
		Cart firstCart = carts.get(totalSize - 1);

		assertThat(pageInfo.getTotalPage()).isEqualTo((int) Math.ceil(totalSize / 16.0));
		assertThat(pageInfo.getPage()).isEqualTo(1);
		assertThat(pageInfo.getSize()).isEqualTo(20);
		assertThat(pageInfo.getTotalSize()).isEqualTo(totalSize);

		assertThat(firstContent.getVideoId()).isEqualTo(firstCart.getVideo().getVideoId());
		assertThat(firstContent.getVideoName()).isEqualTo(firstCart.getVideo().getVideoName());
		assertThat(firstContent.getThumbnailUrl()).isEqualTo(getThumbnailUrl(firstCart.getVideo()));
		assertThat(firstContent.getViews()).isEqualTo(firstCart.getVideo().getView());
		assertThat(firstContent.getPrice()).isEqualTo(firstCart.getVideo().getPrice());
		assertThat(objectMapper.writeValueAsString(firstContent.getVideoCategories())).isEqualTo(
			objectMapper.writeValueAsString(firstCart.getVideo().getVideoCategories().stream().map(
				videoCategory -> CategoryResponse.of(videoCategory.getCategory())
			).collect(Collectors.toList()))
		);
		assertThat(firstContent.getChannel().getMemberId())
			.isEqualTo(firstCart.getVideo().getChannel().getMember().getMemberId());
		assertThat(firstContent.getChannel().getChannelName())
			.isEqualTo(firstCart.getVideo().getChannel().getChannelName());
		assertThat(firstContent.getChannel().getSubscribes()).isEqualTo(firstCart.getVideo().getChannel().getSubscribers());
		assertThat(firstContent.getChannel().getImageUrl()).isEqualTo(getProfileUrl(firstCart.getVideo().getChannel().getMember()));
	}

	@Test
	@DisplayName("자신의 결제 내역을 조회한다.")
	void getOrders() throws Exception {
		// given
		Member member = memberRepository.findById(loginMember.getMemberId()).orElseThrow();
		List<Order> orders = member.getOrders();

		int totalSize = orders.size();

		// when
		ResultActions actions = mockMvc.perform(
			get("/members/orders")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.param("page", "1")
				.param("size","4")
				.param("month", "1")
				.accept(APPLICATION_JSON)
		);

		em.flush();
		em.clear();

		// then
		actions
			.andDo(print())
			.andExpect(status().isOk());

		ApiPageResponse<OrdersResponse> ordersResponse =
			getApiPageResponseFromResult(actions, OrdersResponse.class);

		PageInfo pageInfo = ordersResponse.getPageInfo();
		List<OrdersResponse> responses = ordersResponse.getData();

		assertThat(pageInfo.getTotalPage()).isEqualTo((int) Math.ceil(totalSize / 4.0));
		assertThat(pageInfo.getPage()).isEqualTo(1);
		assertThat(pageInfo.getSize()).isEqualTo(4);
		assertThat(pageInfo.getTotalSize()).isEqualTo(totalSize);

		OrdersResponse firstContent = responses.get(0);
		Order firstOrder = orderRepository.findById(firstContent.getOrderId()).orElseThrow();

		assertThat(firstContent.getOrderId()).isEqualTo(firstOrder.getOrderId());
		assertThat(firstContent.getAmount())
			.isEqualTo(firstOrder.getTotalPayAmount() - firstOrder.getReward());
		assertThat(firstContent.getOrderCount()).isEqualTo(firstOrder.getOrderVideos().size());
		assertThat(firstContent.getOrderStatus()).isEqualTo(firstOrder.getOrderStatus());
		assertThat(firstContent.getCreatedDate()).isNotNull();
		assertThat(firstContent.getCompletedDate()).isNotNull();
		assertThat(objectMapper.writeValueAsString(firstContent.getOrderVideos()))
			.isEqualTo(
				objectMapper.writeValueAsString(firstOrder.getOrderVideos().stream().map(
					orderVideo -> OrdersResponse.OrderVideo.builder()
						.videoId(orderVideo.getVideo().getVideoId())
						.videoName(orderVideo.getVideo().getVideoName())
						.thumbnailFile(getThumbnailUrl(orderVideo.getVideo()))
						.channelName(orderVideo.getVideo().getChannel().getChannelName())
						.price(orderVideo.getVideo().getPrice())
						.orderStatus(orderVideo.getOrderStatus())
						.build()
				).collect(Collectors.toList()))
			);
	}

	@TestFactory
	@DisplayName("자신의 구매한 강의 목록을 조회한다.")
	Collection<DynamicTest> getPlaylists() throws Exception {

		return List.of(
			dynamicTest(
				"영상 이름순 조회",
				() -> {
					// given
					List<Video> videos = new ArrayList<>();

					memberRepository.findById(loginMember.getMemberId()).orElseThrow().getOrders().forEach(
						order -> order.getOrderVideos().forEach(orderVideo -> videos.add(orderVideo.getVideo()))
					);

					videos.sort(Comparator.comparing(Video::getVideoName));

					int totalSize = videos.size();

					Video firstVideo = videos.get(0);

					Long expectVideoId = firstVideo.getVideoId();
					String expectVideoName = firstVideo.getVideoName();
					Float expectVideoStar = firstVideo.getStar();
					String expectThumbnailUrl = getThumbnailUrl(firstVideo);
					Long expectMemberId = firstVideo.getChannel().getMember().getMemberId();
					String expectChannelName = firstVideo.getChannel().getChannelName();
					String expectImageUrl = getProfileUrl(firstVideo.getChannel().getMember());

					em.flush();
					em.clear();

					// when
					ResultActions actions = mockMvc.perform(
						get("/members/playlists")
							.header(AUTHORIZATION, loginMemberAccessToken)
							.param("page",  "1")
							.param("size", "16")
							.param("sort", "name")
							.accept(APPLICATION_JSON)
					);

					em.flush();
					em.clear();

					// then
					actions
						.andDo(print())
						.andExpect(status().isOk());

					ApiPageResponse<PlaylistsResponse> playlistsResponseApiPageResponse =
						getApiPageResponseFromResult(actions, PlaylistsResponse.class);

					PageInfo pageInfo = playlistsResponseApiPageResponse.getPageInfo();
					List<PlaylistsResponse> responses = playlistsResponseApiPageResponse.getData();

					assertThat(responses).isSortedAccordingTo(Comparator.comparing(PlaylistsResponse::getVideoName));

					assertThat(pageInfo.getTotalPage()).isEqualTo((int) Math.ceil(totalSize / 16.0));
					assertThat(pageInfo.getPage()).isEqualTo(1);
					assertThat(pageInfo.getSize()).isEqualTo(16);
					assertThat(pageInfo.getTotalSize()).isEqualTo(totalSize);

					assertThat(responses.get(0).getVideoId()).isEqualTo(expectVideoId);
					assertThat(responses.get(0).getVideoName()).isEqualTo(expectVideoName);
					assertThat(responses.get(0).getStar()).isEqualTo(expectVideoStar);
					assertThat(responses.get(0).getThumbnailUrl()).isEqualTo(expectThumbnailUrl);
					assertThat(responses.get(0).getChannel().getMemberId()).isEqualTo(expectMemberId);
					assertThat(responses.get(0).getChannel().getChannelName()).isEqualTo(expectChannelName);
					assertThat(responses.get(0).getChannel().getImageUrl()).isEqualTo(expectImageUrl);
				}
			),
			dynamicTest(
				"영상 별점순 조회",
				() -> {
					// given
					List<Video> videos = new ArrayList<>();

					memberRepository.findById(loginMember.getMemberId()).orElseThrow().getOrders().forEach(
						order -> order.getOrderVideos().forEach(orderVideo -> videos.add(orderVideo.getVideo()))
					);

					videos.sort(Comparator.comparing(Video::getStar));

					int totalSize = videos.size();

					Video firstVideo = videos.get(videos.size() - 1);

					Long expectVideoId = firstVideo.getVideoId();
					String expectVideoName = firstVideo.getVideoName();
					Float expectVideoStar = firstVideo.getStar();
					String expectThumbnailUrl = getThumbnailUrl(firstVideo);
					Long expectMemberId = firstVideo.getChannel().getMember().getMemberId();
					String expectChannelName = firstVideo.getChannel().getChannelName();
					String expectImageUrl = getProfileUrl(firstVideo.getChannel().getMember());

					em.flush();
					em.clear();

					// when
					ResultActions actions = mockMvc.perform(
						get("/members/playlists")
							.header(AUTHORIZATION, loginMemberAccessToken)
							.param("page",  "1")
							.param("size", "16")
							.param("sort", "star")
							.accept(APPLICATION_JSON)
					);

					em.flush();
					em.clear();

					// then
					actions
						.andDo(print())
						.andExpect(status().isOk());

					ApiPageResponse<PlaylistsResponse> playlistsResponseApiPageResponse =
						getApiPageResponseFromResult(actions, PlaylistsResponse.class);

					PageInfo pageInfo = playlistsResponseApiPageResponse.getPageInfo();
					List<PlaylistsResponse> responses = playlistsResponseApiPageResponse.getData();

					assertThat(responses).isSortedAccordingTo(Comparator.comparing(PlaylistsResponse::getStar).reversed());

					assertThat(pageInfo.getTotalPage()).isEqualTo((int) Math.ceil(totalSize / 16.0));
					assertThat(pageInfo.getPage()).isEqualTo(1);
					assertThat(pageInfo.getSize()).isEqualTo(16);
					assertThat(pageInfo.getTotalSize()).isEqualTo(totalSize);

					assertThat(responses.get(0).getVideoId()).isEqualTo(expectVideoId);
					assertThat(responses.get(0).getVideoName()).isEqualTo(expectVideoName);
					assertThat(responses.get(0).getStar()).isEqualTo(expectVideoStar);
					assertThat(responses.get(0).getThumbnailUrl()).isEqualTo(expectThumbnailUrl);
					assertThat(responses.get(0).getChannel().getMemberId()).isEqualTo(expectMemberId);
					assertThat(responses.get(0).getChannel().getChannelName()).isEqualTo(expectChannelName);
					assertThat(responses.get(0).getChannel().getImageUrl()).isEqualTo(expectImageUrl);
				}
			),
			dynamicTest(
				"영상 최신순 조회",
				() -> {
					// given
					List<Video> videos = new ArrayList<>();

					memberRepository.findById(loginMember.getMemberId()).orElseThrow().getOrders().forEach(
						order -> order.getOrderVideos().forEach(orderVideo -> videos.add(orderVideo.getVideo()))
					);

					videos.sort(Comparator.comparing(Video::getCreatedDate));

					int totalSize = videos.size();

					Video firstVideo = videos.get(videos.size() - 1);

					Long expectVideoId = firstVideo.getVideoId();
					String expectVideoName = firstVideo.getVideoName();
					Float expectVideoStar = firstVideo.getStar();
					String expectThumbnailUrl = getThumbnailUrl(firstVideo);
					Long expectMemberId = firstVideo.getChannel().getMember().getMemberId();
					String expectChannelName = firstVideo.getChannel().getChannelName();
					String expectImageUrl = getProfileUrl(firstVideo.getChannel().getMember());

					em.flush();
					em.clear();

					// when
					ResultActions actions = mockMvc.perform(
						get("/members/playlists")
							.header(AUTHORIZATION, loginMemberAccessToken)
							.param("page",  "1")
							.param("size", "16")
							.param("sort", "created-date")
							.accept(APPLICATION_JSON)
					);

					em.flush();
					em.clear();

					// then
					actions
						.andDo(print())
						.andExpect(status().isOk());

					ApiPageResponse<PlaylistsResponse> playlistsResponseApiPageResponse =
						getApiPageResponseFromResult(actions, PlaylistsResponse.class);

					PageInfo pageInfo = playlistsResponseApiPageResponse.getPageInfo();
					List<PlaylistsResponse> responses = playlistsResponseApiPageResponse.getData();

					assertThat(responses).isSortedAccordingTo(Comparator.comparing(PlaylistsResponse::getCreatedDate).reversed());

					assertThat(pageInfo.getTotalPage()).isEqualTo((int) Math.ceil(totalSize / 16.0));
					assertThat(pageInfo.getPage()).isEqualTo(1);
					assertThat(pageInfo.getSize()).isEqualTo(16);
					assertThat(pageInfo.getTotalSize()).isEqualTo(totalSize);

					assertThat(responses.get(0).getVideoId()).isEqualTo(expectVideoId);
					assertThat(responses.get(0).getVideoName()).isEqualTo(expectVideoName);
					assertThat(responses.get(0).getStar()).isEqualTo(expectVideoStar);
					assertThat(responses.get(0).getThumbnailUrl()).isEqualTo(expectThumbnailUrl);
					assertThat(responses.get(0).getChannel().getMemberId()).isEqualTo(expectMemberId);
					assertThat(responses.get(0).getChannel().getChannelName()).isEqualTo(expectChannelName);
					assertThat(responses.get(0).getChannel().getImageUrl()).isEqualTo(expectImageUrl);
				}
			)
		);
	}

	@Test
	@DisplayName("자신의 구매한 강의 목록을 채널별로 그룹화 해서 조회한다.")
	void getPlaylistChannels() throws Exception {
		// given
		// 로그인한 회원 조회
		Member member = memberRepository.findById(loginMember.getMemberId()).orElseThrow();

		// 구매한 비디오 목록
		List<Video> videos = new ArrayList<>();

		member.getOrders().forEach(
			order -> order.getOrderVideos().forEach(orderVideo -> videos.add(orderVideo.getVideo()))
		);

		videos.sort(Comparator.comparing(video -> video.getChannel().getChannelName()));

		// 구매한 강의들이 몇개의 채널에 속하는지
		List<String> channelName = videos.stream()
			.map(video -> video.getChannel().getChannelName())
			.distinct()
			.collect(Collectors.toList());

		int totalSize = channelName.size();

		// 채널별 구매한 강의 수
		List<String> channelNames = videos.stream()
			.map(video -> video.getChannel().getChannelName())
			.collect(Collectors.toList());

		Map<String, Long> channelVideoCount = channelNames.stream()
			.collect(Collectors.groupingBy(name -> name, Collectors.counting()));

		boolean isSubscribed = member.getSubscribes().stream()
			.anyMatch(subscribe -> subscribe.getChannel().getChannelId() == videos.get(0).getChannel().getChannelId());

		Video expectVideo = videos.get(0);

		Long expectMemberId = expectVideo.getMemberId();
		String expectChannelName = expectVideo.getChannel().getChannelName();
		String expectImageUrl = getProfileUrl(expectVideo.getChannel().getMember());
		Integer expectSubscribers = expectVideo.getChannel().getSubscribes().size();
		Long expectVideoCount = channelVideoCount.get(channelNames.get(0));
		Boolean expectIsSubscribed = isSubscribed;

		em.flush();
		em.clear();

		// when
		ResultActions actions = mockMvc.perform(
			get("/members/playlists/channels")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.param("page","1")
				.param("size","16")
				.accept(APPLICATION_JSON)
		);

		em.flush();
		em.clear();

		// then
		actions
			.andDo(print())
			.andExpect(status().isOk());

		ApiPageResponse<PlaylistChannelResponse> playlistsChannelResponseApiPageResponse =
			getApiPageResponseFromResult(actions, PlaylistChannelResponse.class);

		PageInfo pageInfo = playlistsChannelResponseApiPageResponse.getPageInfo();
		List<PlaylistChannelResponse> responses = playlistsChannelResponseApiPageResponse.getData();

		assertThat(responses).isSortedAccordingTo(Comparator.comparing(PlaylistChannelResponse::getChannelName));

		assertThat(pageInfo.getTotalPage()).isEqualTo((int) Math.ceil(totalSize / 16.0));
		assertThat(pageInfo.getPage()).isEqualTo(1);
		assertThat(pageInfo.getSize()).isEqualTo(16);
		assertThat(pageInfo.getTotalSize()).isEqualTo(totalSize);

		assertThat(responses.get(0).getMemberId()).isEqualTo(expectMemberId);
		assertThat(responses.get(0).getChannelName()).isEqualTo(expectChannelName);
		assertThat(responses.get(0).getImageUrl()).isEqualTo(expectImageUrl);
		assertThat(responses.get(0).getSubscribers()).isEqualTo(expectSubscribers);
		assertThat(responses.get(0).getVideoCount()).isEqualTo(expectVideoCount);
		assertThat(responses.get(0).getIsSubscribed()).isEqualTo(expectIsSubscribed);
		assertThat(responses.get(0).getList().size()).isEqualTo(0);
	}

	@Test
	@DisplayName("자신의 구매한 강의 목록을 채널별로 상세 조회한다.")
	void getPlaylistChannelDetails() throws Exception {
		// given
		Member member = memberRepository.findById(loginMember.getMemberId()).orElseThrow();

		List<Video> videos = new ArrayList<>();

		member.getOrders().forEach(
			order -> order.getOrderVideos().forEach(orderVideo -> videos.add(orderVideo.getVideo()))
		);

		videos.sort(Comparator.comparing(Video::getVideoName));

		List<Video> expect = videos.stream()
			.filter(video -> {
				Channel channel1 = videos.get(0).getChannel();
				Channel channel2 = video.getChannel();
				return channel1.getChannelName().equals(channel2.getChannelName());
			})
			.sorted(Comparator.comparing(Video::getVideoName))
			.collect(Collectors.toList());

		int totalSize = expect.size();

		Long channelId = videos.get(0).getChannel().getChannelId();

		em.flush();
		em.clear();

		// when
		ResultActions actions = mockMvc.perform(
			get("/members/playlists/channels/details")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.param("page","1")
				.param("size","16")
				.param("member-id",channelId + "")
				.accept(APPLICATION_JSON)
		);

		em.flush();
		em.clear();

		// then
		actions
			.andDo(print())
			.andExpect(status().isOk());

		ApiPageResponse<PlaylistChannelDetailsResponse> playlistsChannelDetailsResponseApiPageResponse =
			getApiPageResponseFromResult(actions, PlaylistChannelDetailsResponse.class);

		PageInfo pageInfo = playlistsChannelDetailsResponseApiPageResponse.getPageInfo();
		List<PlaylistChannelDetailsResponse> responses =
			playlistsChannelDetailsResponseApiPageResponse.getData();

		assertThat(responses)
			.isSortedAccordingTo(Comparator.comparing(PlaylistChannelDetailsResponse::getVideoName));

		assertThat(pageInfo.getTotalPage()).isEqualTo((int) Math.ceil(totalSize / 16.0));
		assertThat(pageInfo.getPage()).isEqualTo(1);
		assertThat(pageInfo.getSize()).isEqualTo(16);
		assertThat(pageInfo.getTotalSize()).isEqualTo(totalSize);

		for (int i = 0; i < 5; i++) {
			assertThat(responses.get(i).getVideoId()).isEqualTo(expect.get(i).getVideoId());
			assertThat(responses.get(i).getVideoName()).isEqualTo(expect.get(i).getVideoName());
			assertThat(responses.get(i).getDescription()).isEqualTo(expect.get(i).getDescription());
			assertThat(responses.get(i).getThumbnailUrl()).isEqualTo(getThumbnailUrl(expect.get(i)));
			assertThat(responses.get(i).getView()).isEqualTo(expect.get(i).getView());
			assertThat(responses.get(i).getStar()).isEqualTo(expect.get(i).getStar());
			assertThat(responses.get(i).getCreatedDate()).isNotNull();
		}
	}

	@Test
	@DisplayName("자신의 시청 기록을 조회한다.")
	void getWatchs() throws Exception {
		// given
		Member member = memberRepository.findById(loginMember.getMemberId()).orElseThrow();

		List<Watch> watches = member.getWatches();

		int totalSize = watches.size();

		Watch firstWatch = watches.get(totalSize - 1);

		Long expectVideoId = firstWatch.getVideo().getVideoId();
		String expectVideoName = firstWatch.getVideo().getVideoName();
		String expectThumbnailUrl = getThumbnailUrl(firstWatch.getVideo());
		Long expectMemberId = firstWatch.getVideo().getChannel().getMember().getMemberId();
		String expectChannelName = firstWatch.getVideo().getChannel().getChannelName();
		String expectImageUrl = getProfileUrl(firstWatch.getVideo().getChannel().getMember());

		em.flush();
		em.clear();

		// when
		ResultActions actions = mockMvc.perform(
			get("/members/watchs")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.param("page", "1")
				.param("size","16")
				.param("day", "30")
		);

		em.flush();
		em.clear();

		// then
		actions
			.andDo(print())
			.andExpect(status().isOk());

		ApiPageResponse<WatchsResponse> watchsResponses =
			getApiPageResponseFromResult(actions, WatchsResponse.class);

		PageInfo pageInfo = watchsResponses.getPageInfo();
		List<WatchsResponse> responses = watchsResponses.getData();

		assertThat(responses).isSortedAccordingTo(Comparator.comparing(WatchsResponse::getModifiedDate).reversed());

		assertThat(pageInfo.getTotalPage()).isEqualTo((int) Math.ceil(totalSize / 16.0));
		assertThat(pageInfo.getPage()).isEqualTo(1);
		assertThat(pageInfo.getSize()).isEqualTo(16);
		assertThat(pageInfo.getTotalSize()).isEqualTo(totalSize);

		WatchsResponse firstContent = responses.get(0);

		assertThat(firstContent.getVideoId()).isEqualTo(expectVideoId);
		assertThat(firstContent.getVideoName()).isEqualTo(expectVideoName);
		assertThat(firstContent.getThumbnailUrl()).isEqualTo(expectThumbnailUrl);
		assertThat(firstContent.getModifiedDate()).isNotNull();
		assertThat(firstContent.getChannel().getMemberId()).isEqualTo(expectMemberId);
		assertThat(firstContent.getChannel().getChannelName()).isEqualTo(expectChannelName);
		assertThat(firstContent.getChannel().getImageUrl()).isEqualTo(expectImageUrl);
	}

	@TestFactory
	@DisplayName("자신의 닉네임을 변경한다.")
	Collection<DynamicTest> updateNickname() throws Exception {

		return List.of(
			dynamicTest(
				"로그인한 회원이 아닌 경우 권한 예외가 발생한다",
				() -> {
					// given
					MemberApiRequest.Nickname nickname = new MemberApiRequest.Nickname("testnickname");

					String content = objectMapper.writeValueAsString(nickname);

					String before = memberRepository.findById(loginMember.getMemberId()).orElseThrow().getNickname();

					// when
					ResultActions actions = mockMvc.perform(
						patch("/members")
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					em.flush();
					em.clear();

					// then
					actions
						.andDo(print())
						.andExpect(status().isForbidden());


					String after = memberRepository.findById(loginMember.getMemberId()).orElseThrow().getNickname();

					assertThat(after).isEqualTo(before);
				}
			),
			dynamicTest(
				"입력값이 없는 경우 변경사항이 없다는 예외를 발생한다",
				() -> {
					// given
					MemberApiRequest.Nickname nickname = new MemberApiRequest.Nickname(null);

					String content = objectMapper.writeValueAsString(nickname);

					String before = memberRepository.findById(loginMember.getMemberId()).orElseThrow().getNickname();

					// when
					ResultActions actions = mockMvc.perform(
						patch("/members")
							.header(AUTHORIZATION, loginMemberAccessToken)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					em.flush();
					em.clear();

					// then
					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("nickname"))
						.andExpect(jsonPath("$.data[0].value").value("null"))
						.andExpect(jsonPath("$.data[0].reason").value("한글/숫자/영어를 선택하여 사용한 1 ~ 20자를 입력하세요."));


					String after = memberRepository.findById(loginMember.getMemberId()).orElseThrow().getNickname();

					assertThat(after).isEqualTo(before);
				}
			),
			dynamicTest(
				"기존 닉네임과 같은 경우 변경사항이 없다는 예외를 발생한다",
				() -> {
					String before = memberRepository.findById(loginMember.getMemberId()).orElseThrow().getNickname();

					MemberApiRequest.Nickname nickname = new MemberApiRequest.Nickname(before);

					String content = objectMapper.writeValueAsString(nickname);

					ResultActions actions = mockMvc.perform(
						patch("/members")
							.header(AUTHORIZATION, loginMemberAccessToken)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest());

					em.flush();
					em.clear();

					String after = memberRepository.findById(loginMember.getMemberId()).orElseThrow().getNickname();

					assertThat(after).isEqualTo(before);
				}
			),
			dynamicTest(
				"닉네임의 길이가 20자를 초과한 경우 예외를 발생한다",
				() -> {
					String before = memberRepository.findById(loginMember.getMemberId()).orElseThrow().getNickname();

					MemberApiRequest.Nickname nickname = new MemberApiRequest.Nickname(before);

					String content = objectMapper.writeValueAsString("aaaaaaaaaaaaaaaaaaaaa");

					ResultActions actions = mockMvc.perform(
						patch("/members")
							.header(AUTHORIZATION, loginMemberAccessToken)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("nickname"))
						.andExpect(jsonPath("$.data[0].value").value("aaaaaaaaaaaaaaaaaaaaa"))
						.andExpect(jsonPath("$.data[0].reason").value("한글/숫자/영어를 선택하여 사용한 1 ~ 20자를 입력하세요."));

					em.flush();
					em.clear();

					String after = memberRepository.findById(loginMember.getMemberId()).orElseThrow().getNickname();

					assertThat(after).isEqualTo(before);
				}
			),
			dynamicTest(
				"닉네임에 한글, 숫자, 영어 이외의 문자가 입력된 경우 예외를 발생한다",
				() -> {
					String before = memberRepository.findById(loginMember.getMemberId()).orElseThrow().getNickname();

					MemberApiRequest.Nickname nickname = new MemberApiRequest.Nickname(before);

					String content = objectMapper.writeValueAsString("$#%$#%#$");

					ResultActions actions = mockMvc.perform(
						patch("/members")
							.header(AUTHORIZATION, loginMemberAccessToken)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.data[0].field").value("nickname"))
						.andExpect(jsonPath("$.data[0].value").value("$#%$#%#$"))
						.andExpect(jsonPath("$.data[0].reason").value("한글/숫자/영어를 선택하여 사용한 1 ~ 20자를 입력하세요."));

					em.flush();
					em.clear();

					String after = memberRepository.findById(loginMember.getMemberId()).orElseThrow().getNickname();

					assertThat(after).isEqualTo(before);
				}
			),
			dynamicTest(
				"닉네임이 변경된다",
				() -> {
					String before = memberRepository.findById(loginMember.getMemberId()).orElseThrow().getNickname();

					MemberApiRequest.Nickname nickname = new MemberApiRequest.Nickname("changeNickname");

					String content = objectMapper.writeValueAsString(nickname);

					ResultActions actions = mockMvc.perform(
						patch("/members")
							.header(AUTHORIZATION, loginMemberAccessToken)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isNoContent());

					em.flush();
					em.clear();

					String after = memberRepository.findById(loginMember.getMemberId()).orElseThrow().getNickname();

					assertThat(after).isNotEqualTo(before);
					assertThat(after).isEqualTo(nickname.getNickname());
				}
			)
		);
	}

	@Test
	@DisplayName("프로필 이미지를 변경한다.")
	void updateImage() throws Exception {
		String change = "newImageName";

		Member member = memberRepository.findById(loginMember.getMemberId()).orElseThrow();

		String before = member.getImageFile();
		String expect = member.getMemberId() + "/profile/" + change;

		MemberApiRequest.Image request = new MemberApiRequest.Image(
			change, ImageType.JPG
		);

		String content = objectMapper.writeValueAsString(request);

		ResultActions actions = mockMvc.perform(
			patch("/members/image")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.contentType(APPLICATION_JSON)
				.content(content)
		);

		actions
			.andDo(print())
			.andExpect(status().isOk());

		String after = memberRepository.findById(loginMember.getMemberId()).orElseThrow().getImageFile();

		assertThat(before).isNotEqualTo(after);
		assertThat(after).isEqualTo(expect);
	}

	@TestFactory
	@DisplayName("비밀번호 변경 테스트")
	Collection<DynamicTest> updatePassword() throws Exception {

		return List.of(
			dynamicTest(
				"권한이 없는 경우",
				() -> {
					String wrongPrevPassword = "wrong" + loginMemberPassword;

					MemberApiRequest.Password request = new MemberApiRequest.Password(
						wrongPrevPassword, "12345abcde!"
					);

					String content = objectMapper.writeValueAsString(request);

					ResultActions actions = mockMvc.perform(
						patch("/members/password")
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isForbidden());
				}
			),
			dynamicTest(
				"기존 비밀번호가 잘못된 경우",
				() -> {
					String wrongPrevPassword = "wrong" + loginMemberPassword;

					MemberApiRequest.Password request = new MemberApiRequest.Password(
						wrongPrevPassword, "12345abcde!"
					);

					String content = objectMapper.writeValueAsString(request);

					ResultActions actions = mockMvc.perform(
						patch("/members/password")
							.header(AUTHORIZATION, loginMemberAccessToken)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isBadRequest());
				}
			),
			dynamicTest(
				"비밀번호가 성공적으로 변경된다",
				() -> {
					String prevPassword = loginMemberPassword;

					MemberApiRequest.Password request = new MemberApiRequest.Password(
						prevPassword, "12345abcde!"
					);

					String before = memberRepository.findById(loginMember.getMemberId()).orElseThrow().getPassword();

					String content = objectMapper.writeValueAsString(request);

					ResultActions actions = mockMvc.perform(
						patch("/members/password")
							.header(AUTHORIZATION, loginMemberAccessToken)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					actions
						.andDo(print())
						.andExpect(status().isNoContent());

					String after = memberRepository.findById(loginMember.getMemberId()).orElseThrow().getPassword();

					assertThat(before).isNotEqualTo(after);
					assertThat(passwordEncoder.matches(request.getNewPassword(), after)).isTrue();
				}
			)
		);
	}

	@Test
	@DisplayName("자신의 프로필 이미지를 삭제한다.")
	void deleteImage() throws Exception {

		String before = memberRepository.findById(loginMember.getMemberId()).orElseThrow().getImageFile();

		ResultActions actions = mockMvc.perform(
			delete("/members/image")
				.header(AUTHORIZATION, loginMemberAccessToken)
		);

		actions
			.andDo(print())
			.andExpect(status().isNoContent());

		String after = memberRepository.findById(loginMember.getMemberId()).orElseThrow().getImageFile();

		assertThat(before).isNotEqualTo(after);
		assertThat(after).isNull();
	}

	@Test
	@DisplayName("회원을 탈퇴시키고 관련된 모든 정보를 삭제 및 갱신한다.")
	void deleteMember() throws Exception {
		// given
		Member member = memberRepository.findById(loginMember.getMemberId()).orElseThrow();

		List<Long> videos = member.getChannel().getVideos().stream().map(Video::getVideoId).collect(Collectors.toList());
		List<Long> watches = member.getWatches().stream().map(Watch::getWatchId).collect(Collectors.toList());
		List<Long> subscribes = member.getSubscribes().stream().map(Subscribe::getSubscribe).collect(Collectors.toList());
		List<String> orders = member.getOrders().stream().map(Order::getOrderId).collect(Collectors.toList());
		List<Long> replies = member.getReplies().stream().map(Reply::getReplyId).collect(Collectors.toList());
		List<Long> answers = member.getAnswers().stream().map(Answer::getAnswerId).collect(Collectors.toList());
		List<Long> carts = member.getCarts().stream().map(Cart::getCartId).collect(Collectors.toList());
		List<Long> rewards = member.getRewards().stream().map(Reward::getRewardId).collect(Collectors.toList());

		List<Long> subscribedChannelId = member.getSubscribes().stream().map(subscribe -> subscribe.getChannel().getChannelId()).collect(Collectors.toList());
		List<Integer> beforeSubscribers = member.getSubscribes().stream().map(subscribe -> subscribe.getChannel().getSubscribers()).collect(Collectors.toList());

		List<Long> replyVideoId = member.getReplies().stream().map(reply -> reply.getVideo().getVideoId()).collect(Collectors.toList());

		member.getReplies().stream().forEach(reply -> reply.getVideo().calculateStar());
		List<Float> beforeStars = member.getReplies().stream().map(reply -> reply.getVideo().getStar()).collect(Collectors.toList());

		em.flush();
		em.clear();

		// when
		ResultActions actions = mockMvc.perform(
			delete("/members")
				.header(AUTHORIZATION, loginMemberAccessToken)
		);

		em.flush();
		em.clear();

		// then
		actions
			.andDo(print())
			.andExpect(status().isNoContent());

		// 비디오의 채널이 null로 변경되었는가
		for (Long id : videos) {
			assertThat(videoRepository.findById(id).orElseThrow().getChannel())
				.isNull();
		}

		// 시청기록이 삭제되었는가
		for (Long id : watches) {
			assertThrows(ChangeSetPersister.NotFoundException.class, () -> watchRepository.findById(id).orElseThrow(
				ChangeSetPersister.NotFoundException::new));
		}

		// 구독 목록이 삭제되었는가
		for (Long id : subscribes) {
			assertThrows(ChangeSetPersister.NotFoundException.class, () -> subscribeRepository.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new));
		}

		// 회원이 구독하던 채널의 구독자 수가 변경되었는가
		for (int i = 0; i < subscribedChannelId.size(); i++) {
			assertThat(channelRepository.findById(subscribedChannelId.get(i)).orElseThrow().getSubscribers())
				.isNotEqualTo(beforeSubscribers.get(i));
		}

		// 주문 기록은 남아있는가
		for (String id : orders) {
			assertDoesNotThrow(() -> orderRepository.findById(id).orElseThrow(OrderNotFoundException::new));
		}

		// 작성한 강의평이 모두 삭제되었는가
		for (Long id : replies) {
			assertThrows(ReplyNotFoundException.class, () -> replyRepository.findById(id).orElseThrow(ReplyNotFoundException::new));
		}

		// 강의평을 작성했던 강의들의 평점이 업데이트 되었는가
		for (int i = 0; i < replyVideoId.size(); i++) {
			assertThat(videoRepository.findById(replyVideoId.get(i)).orElseThrow().getStar())
				.isNotEqualTo(beforeStars.get(i));
		}

		// 문제 풀이 기록이 모두 삭제되었는가
		for (Long id : answers) {
			assertThrows(AnswerNotFoundException.class, () -> answerRepository.findById(id).orElseThrow(AnswerNotFoundException::new));
		}

		// 장바구니가 모두 삭제되었는가
		for (Long id : carts) {
			assertThrows(ChangeSetPersister.NotFoundException.class, () -> cartRepository.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new));
		}

		// 리워드가 모두 삭제되었는가
		for (Long id : rewards) {
			assertThrows(ChangeSetPersister.NotFoundException.class, () -> rewardRepository.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new));
		}
	}
}
