package com.server.intergration;

import static com.server.auth.util.AuthConstant.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.server.domain.cart.entity.Cart;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.question.entity.Question;
import com.server.domain.question.service.dto.response.QuestionResponse;
import com.server.domain.reply.dto.CreateReply;
import com.server.domain.reply.dto.ReplyInfo;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reward.entity.Reward;
import com.server.domain.subscribe.entity.Subscribe;
import com.server.domain.video.controller.dto.request.AnswersCreateApiRequest;
import com.server.domain.video.controller.dto.request.QuestionCreateApiRequest;
import com.server.domain.video.controller.dto.request.VideoCartDeleteApiRequest;
import com.server.domain.video.controller.dto.request.VideoCreateApiRequest;
import com.server.domain.video.controller.dto.request.VideoCreateUrlApiRequest;
import com.server.domain.video.controller.dto.request.VideoUpdateApiRequest;
import com.server.domain.video.entity.Video;
import com.server.domain.video.entity.VideoStatus;
import com.server.domain.video.service.dto.response.VideoDetailResponse;
import com.server.domain.video.service.dto.response.VideoPageResponse;
import com.server.domain.watch.entity.Watch;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import com.server.global.reponse.PageInfo;
import com.server.module.s3.service.dto.FileType;
import com.server.module.s3.service.dto.ImageType;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VideoIntegrationTest extends IntegrationTest {

	private final String BASE_URL = "/videos";

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
	Member otherMember5;

	Channel otherMemberChannel1;
	Channel otherMemberChannel2;
	Channel otherMemberChannel3;
	Channel otherMemberChannel4;
	Channel otherMemberChannel5;

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
	String otherMemberEmail5 = "other5@email.com";
	String otherMemberPassword = "other1234!";

	Video otherMemberClosedVideo;
	Video loginMemberClosedVideo;
	Video isNotPurchasedVideo;
	Video isNotPurchasedVideo2;
	Video isPurchasedVideo;
	Video isNotSubscribedVideo1;
	Video isNotSubscribedVideo2;
	Video replyTestVideo;

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
		otherMember5 = createAndSaveMemberWithEmailPassword(otherMemberEmail5, otherMemberPassword);

		otherMemberChannel1 = createChannelWithRandomName(otherMember1);
		otherMemberChannel2 = createChannelWithRandomName(otherMember2);
		otherMemberChannel3 = createChannelWithRandomName(otherMember3);
		otherMemberChannel4 = createChannelWithRandomName(otherMember4);
		otherMemberChannel5 = createChannelWithRandomName(otherMember5);

		replyTestVideo = createAndSaveFreeVideo(otherMemberChannel5);

		createAndSaveOrderWithPurchaseComplete(otherMember1, List.of(replyTestVideo), 0);
		createAndSaveOrderWithPurchaseComplete(otherMember2, List.of(replyTestVideo), 0);
		createAndSaveOrderWithPurchaseComplete(otherMember3, List.of(replyTestVideo), 0);
		createAndSaveOrderWithPurchaseComplete(otherMember4, List.of(replyTestVideo), 0);

		createAndSaveReply(otherMember1, replyTestVideo);
		createAndSaveReply(otherMember2, replyTestVideo);
		createAndSaveReply(otherMember3, replyTestVideo);
		createAndSaveReply(otherMember4, replyTestVideo);

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

		otherMemberClosedVideo = createAndSaveClosedVideo(otherMemberChannel1);
		loginMemberClosedVideo = createAndSaveClosedVideo(loginMemberChannel);
		isNotPurchasedVideo = createAndSavePaidVideo(otherMemberChannel1, 10000);
		isNotPurchasedVideo2 = createAndSavePaidVideo(otherMemberChannel1, 10000);
		isPurchasedVideo = createAndSavePurchasedVideo(loginMember);
		isNotSubscribedVideo1 = createAndSavePaidVideo(otherMemberChannel2, 20000);
		isNotSubscribedVideo2 = createAndSavePaidVideo(otherMemberChannel2, 20000);

		createAndSaveCart(loginMember, isNotPurchasedVideo2);

		createAndSaveCategory("Java");

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

	@TestFactory
	@DisplayName("비디오의 전체 문제 목록 조회 API")
	Collection<DynamicTest> getQuestions() throws Exception {

		return List.of(
			dynamicTest(
				"비디오를 구매한 경우",
				() -> {
					// given
					Member member = memberRepository.findById(loginMember.getMemberId()).orElseThrow();

					List<Video> videos = new ArrayList<>();

					member.getOrders().forEach(
						order -> order.getOrderVideos().forEach(orderVideo -> videos.add(orderVideo.getVideo()))
					);

					Long videoId = videos.get(0).getVideoId();

					Question question = videos.get(0).getQuestions().get(0);

					Long questionId = question.getQuestionId();
					int position = question.getPosition();
					String content = question.getContent();
					String questionAnswer = question.getQuestionAnswer();
					String description = question.getDescription();
					String selection = question.getSelections().get(0);

					// when
					ResultActions actions = mockMvc.perform(
						get(BASE_URL + "/{video-id}/questions", videoId)
							.accept(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
					);

					// then
					actions
						.andDo(print())
						.andExpect(status().isOk());

					ApiSingleResponse<List<QuestionResponse>> apiSingleResponse =
						getApiSingleListResponseFromResult(actions, QuestionResponse.class);

					List<QuestionResponse> questionResponses = apiSingleResponse.getData();
					QuestionResponse questionResponse = questionResponses.get(0);

					assertThat(questionResponse.getQuestionId()).isEqualTo(questionId);
					assertThat(questionResponse.getQuestionAnswer()).isEqualTo(questionAnswer);
					assertThat(questionResponse.getPosition()).isEqualTo(position);
					assertThat(questionResponse.getContent()).isEqualTo(content);
					assertThat(questionResponse.getDescription()).isEqualTo(description);
					assertThat(questionResponse.getSelections().get(0)).isEqualTo(selection);
				}
			),
			dynamicTest(
				"비디오를 구매하지 않은 경우",
				() -> {
					// given
					Video video = createAndSavePaidVideo(otherMemberChannel1, 50000);
					em.flush();
					em.clear();

					Long videoId = video.getVideoId();

					// when
					ResultActions actions = mockMvc.perform(
						get(BASE_URL + "/{video-id}/questions", videoId)
							.accept(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
					);

					// then
					actions
						.andDo(print())
						.andExpect(status().isForbidden());
				}
			),
			dynamicTest(
				"비디오가 존재하지 않는 경우",
				() -> {
					// given
					Long videoId = 999999L;

					// when
					ResultActions actions = mockMvc.perform(
						get(BASE_URL + "/{video-id}/questions", videoId)
							.accept(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
					);

					// then
					actions
						.andDo(print())
						.andExpect(status().isNotFound());
				}
			)
		);
	}

	@TestFactory
	@DisplayName("문제 풀이 API")
	Collection<DynamicTest> solveQuestions() {

		return List.of(
			dynamicTest(
				"비디오를 구매하지 않은 경우",
				() -> {
					// given
					Video video = createAndSavePaidVideo(otherMemberChannel1, 50000);
					em.flush();
					em.clear();

					Long videoId = video.getVideoId();

					AnswersCreateApiRequest request = new AnswersCreateApiRequest(List.of("1"));
					String content = objectMapper.writeValueAsString(request);

					// when
					ResultActions actions = mockMvc.perform(
						post(BASE_URL + "/{video-id}/answers", videoId)
							.accept(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					// then
					actions
						.andDo(print())
						.andExpect(status().isForbidden());
				}
			),
			dynamicTest(
				"비디오가 존재하지 않는 경우",
				() -> {
					// given
					Long videoId = 999999L;

					AnswersCreateApiRequest request = new AnswersCreateApiRequest(List.of("1"));
					String content = objectMapper.writeValueAsString(request);

					// when
					ResultActions actions = mockMvc.perform(
						post(BASE_URL + "/{video-id}/answers", videoId)
							.accept(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					// then
					actions
						.andDo(print())
						.andExpect(status().isNotFound());
				}
			),
			dynamicTest(
				"구매한 비디오의 문제를 맞춘 경우",
				() -> {
					// given
					Member member = memberRepository.findById(loginMember.getMemberId()).orElseThrow();

					List<Video> videos = new ArrayList<>();

					member.getOrders().forEach(
						order -> order.getOrderVideos().forEach(orderVideo -> videos.add(orderVideo.getVideo()))
					);

					Long videoId = videos.get(0).getVideoId();

					AnswersCreateApiRequest request = new AnswersCreateApiRequest(List.of("1"));
					String content = objectMapper.writeValueAsString(request);

					// when
					ResultActions actions = mockMvc.perform(
						post(BASE_URL + "/{video-id}/answers", videoId)
							.accept(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					// then
					actions
						.andDo(print())
						.andExpect(status().isOk());

					ApiSingleResponse<List<Boolean>> apiSingleResponse =
						getApiSingleListResponseFromResult(actions, Boolean.class);

					List<Boolean> answers = apiSingleResponse.getData();
					Boolean firstAnswers = answers.get(0);

					assertThat(firstAnswers).isTrue();
				}
			),
			dynamicTest(
				"구매한 비디오의 문제를 틀린 경우",
				() -> {
					// given
					Member member = memberRepository.findById(loginMember.getMemberId()).orElseThrow();

					List<Video> videos = new ArrayList<>();

					member.getOrders().forEach(
						order -> order.getOrderVideos().forEach(orderVideo -> videos.add(orderVideo.getVideo()))
					);

					Long videoId = videos.get(0).getVideoId();

					AnswersCreateApiRequest request = new AnswersCreateApiRequest(List.of("2"));
					String content = objectMapper.writeValueAsString(request);

					// when
					ResultActions actions = mockMvc.perform(
						post(BASE_URL + "/{video-id}/answers", videoId)
							.accept(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					// then
					actions
						.andDo(print())
						.andExpect(status().isOk());

					ApiSingleResponse<List<Boolean>> apiSingleResponse =
						getApiSingleListResponseFromResult(actions, Boolean.class);

					List<Boolean> answers = apiSingleResponse.getData();
					Boolean firstAnswers = answers.get(0);

					assertThat(firstAnswers).isFalse();
				}
			)
		);
	}

	@TestFactory
	@DisplayName("문제 생성 API")
	Collection<DynamicTest> createQuestions() {

		return List.of(
			dynamicTest(
				"비디오가 존재하지 않는 경우",
				() -> {
					// given
					Long videoId = 999999L;

					List<QuestionCreateApiRequest> requests = createQuestionCreateApiRequests(1);

					String content = objectMapper.writeValueAsString(requests);

					// when
					ResultActions actions = mockMvc.perform(
						post(BASE_URL + "/{video-id}/questions", videoId)
							.accept(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					// then
					actions
						.andDo(print())
						.andExpect(status().isNotFound());
				}
			),
			dynamicTest(
				"자신의 비디오가 아닌 경우",
				() -> {
					// given
					Long videoId = memberRepository.findById(otherMember1.getMemberId()).orElseThrow()
						.getChannel().getVideos().get(0).getVideoId();

					List<QuestionCreateApiRequest> requests = createQuestionCreateApiRequests(1);

					String content = objectMapper.writeValueAsString(requests);

					// when
					ResultActions actions = mockMvc.perform(
						post(BASE_URL + "/{video-id}/questions", videoId)
							.accept(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					// then
					actions
						.andDo(print())
						.andExpect(status().isForbidden());
				}
			),
			dynamicTest(
				"자신이 업로드 한 강의의 문제를 추가",
				() -> {
					// given
					Long videoId = memberRepository.findById(loginMember.getMemberId()).orElseThrow()
						.getChannel().getVideos().get(0).getVideoId();

					List<QuestionCreateApiRequest> requests = createQuestionCreateApiRequests(1);

					String content = objectMapper.writeValueAsString(requests);

					// when
					ResultActions actions = mockMvc.perform(
						post(BASE_URL + "/{video-id}/questions", videoId)
							.accept(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
							.contentType(APPLICATION_JSON)
							.content(content)
					);

					// then
					actions
						.andDo(print())
						.andExpect(status().isCreated())
						.andExpect(header().string("Location", "/videos/" + videoId + "/questions"));

					em.flush();
					em.clear();

					ApiSingleResponse<List<Long>> apiSingleResponse =
						getApiSingleListResponseFromResult(actions, Long.class);

					List<Long> questionIds = apiSingleResponse.getData();
					Long questionId = questionIds.get(0);

					Question createQuestion = questionRepository.findById(questionId).orElseThrow();
					Question expectQuestion = videoRepository.findById(videoId).orElseThrow().getQuestions().get(0);

					assertThat(createQuestion.getQuestionAnswer()).isEqualTo(requests.get(0).getQuestionAnswer());
					assertThat(createQuestion.getContent()).isEqualTo(requests.get(0).getContent());
					assertThat(createQuestion.getDescription()).isEqualTo(requests.get(0).getDescription());
					assertThat(createQuestion.getSelections().get(0)).isEqualTo(requests.get(0).getSelections().get(0));

					assertThat(createQuestion.getQuestionId()).isEqualTo(expectQuestion.getQuestionId());
					assertThat(createQuestion.getContent()).isEqualTo(expectQuestion.getContent());
					assertThat(createQuestion.getQuestionAnswer()).isEqualTo(expectQuestion.getQuestionAnswer());
					assertThat(createQuestion.getDescription()).isEqualTo(expectQuestion.getDescription());
					assertThat(createQuestion.getSelections().get(0)).isEqualTo(expectQuestion.getSelections().get(0));
				}
			)
		);
	}

	@TestFactory
	@DisplayName("비디오 목록 조회 API")
	Collection<DynamicTest> getVideos() {
		// 정렬 기준 : sort = created-date, view, star
		// 카테고리 : category = Java, JS, React 등
		// 구독 여부 : subscribe = false, true
		// 무료 여부 : free = false, true
		// 구매한 비디오도 표시하는지 여부 : is-purchased = true, false

		return List.of(
			dynamicTest(
				"최신순 + 무료",
				() -> {
					// given

					// when
					ResultActions actions = mockMvc.perform(
						get(BASE_URL)
							.accept(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
							.param("free", "true")
							.contentType(APPLICATION_JSON)
					);

					// then
					actions
						.andDo(print())
						.andExpect(status().isOk());

					ApiPageResponse<VideoPageResponse> apiPageResponse =
						getApiPageResponseFromResult(actions, VideoPageResponse.class);

					List<VideoPageResponse> videoPageResponses = apiPageResponse.getData();
					PageInfo pageInfo = apiPageResponse.getPageInfo();

					assertThat(videoPageResponses).isSortedAccordingTo(Comparator.comparing(VideoPageResponse::getCreatedDate).reversed());
					assertThat(pageInfo.getPage()).isEqualTo(1);
					assertThat(pageInfo.getSize()).isEqualTo(16);

					for (VideoPageResponse videoPageResponse : videoPageResponses) {
						assertThat(videoPageResponse.getPrice()).isEqualTo(0);
					}
				}
			)
		);
	}

	@TestFactory
	@DisplayName("비디오 단건 상세 조회 API")
	Collection<DynamicTest> getVideo() {

		return List.of(
			dynamicTest(
				"존재하지 않는 비디오인 경우",
				() -> {
					// given
					Long wrongVideoId = 99999L;

					// when
					ResultActions actions = mockMvc.perform(
						get(BASE_URL + "/{video-id}", wrongVideoId)
							.contentType(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
					);

					// then
					actions
						.andDo(print())
						.andExpect(status().isNotFound());
				}
			),
			dynamicTest(
				"닫힌 비디오인 경우",
				() -> {
					// given
					Long videoId = otherMemberClosedVideo.getVideoId();

					// when
					ResultActions actions = mockMvc.perform(
						get(BASE_URL + "/{video-id}", videoId)
							.contentType(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
					);

					// then
					actions
						.andDo(print())
						.andExpect(status().isNotFound());
				}
			),
			dynamicTest(
				"구매 하지 않은 비디오인 경우",
				() -> {
					// given
					Long videoId = isNotPurchasedVideo.getVideoId();

					int beforeWatches = memberRepository.findById(loginMember.getMemberId()).orElseThrow()
						.getWatches().size();

					int beforeView = videoRepository.findById(isNotPurchasedVideo.getVideoId())
						.orElseThrow().getView();

					// when
					ResultActions actions = mockMvc.perform(
						get(BASE_URL + "/{video-id}", videoId)
							.contentType(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
					);

					em.flush();
					em.clear();

					// then
					actions
						.andDo(print())
						.andExpect(status().isOk());

					ApiSingleResponse<VideoDetailResponse> apiPageResponse =
						getApiSingleResponseFromResult(actions, VideoDetailResponse.class);

					VideoDetailResponse videoDetailResponse = apiPageResponse.getData();

					int afterWatches = memberRepository.findById(loginMember.getMemberId()).orElseThrow()
						.getWatches().size();

					int afterView = videoRepository.findById(videoDetailResponse.getVideoId())
						.orElseThrow().getView();

					assertThat(videoDetailResponse.getIsPurchased()).isFalse();
					assertThat(afterWatches).isEqualTo(beforeWatches + 1);
					assertThat(afterView).isEqualTo(beforeView + 1);
				}
			),
			dynamicTest(
				"구매한 비디오인 경우",
				() -> {
					// given
					Long videoId = isPurchasedVideo.getVideoId();

					int beforeWatches = memberRepository.findById(loginMember.getMemberId()).orElseThrow()
						.getWatches().size();

					int beforeView = videoRepository.findById(isPurchasedVideo.getVideoId())
						.orElseThrow().getView();

					// when
					ResultActions actions = mockMvc.perform(
						get(BASE_URL + "/{video-id}", videoId)
							.contentType(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
					);

					em.flush();
					em.clear();

					// then
					actions
						.andDo(print())
						.andExpect(status().isOk());

					ApiSingleResponse<VideoDetailResponse> apiPageResponse =
						getApiSingleResponseFromResult(actions, VideoDetailResponse.class);

					VideoDetailResponse videoDetailResponse = apiPageResponse.getData();

					int afterWatches = memberRepository.findById(loginMember.getMemberId()).orElseThrow()
						.getWatches().size();

					int afterView = videoRepository.findById(videoDetailResponse.getVideoId())
						.orElseThrow().getView();

					assertThat(videoDetailResponse.getIsPurchased()).isTrue();
					assertThat(afterWatches).isEqualTo(beforeWatches + 1);
					assertThat(afterView).isEqualTo(beforeView + 1);
				}
			)
		);
	}

	@Test
	@DisplayName("비디오 호버링용 url 조회 API")
	void getVideoUrl() throws Exception {
		// given
		Video video = memberRepository.findById(loginMember.getMemberId()).orElseThrow()
			.getOrders().get(0).getOrderVideos().get(0).getVideo();

		Long videoId = video.getVideoId();

		// when
		ResultActions actions = mockMvc.perform(
			get(BASE_URL + "/{video-id}/url", videoId)

				.contentType(APPLICATION_JSON)
		);

		// then
		actions.andDo(print())
			.andExpect(status().isOk());

		String actualVideoUrlResponse =
			actions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

		assertThat(
			actualVideoUrlResponse.contains(
				"\"code\":200,\"status\":\"OK\",\"message\":\"비디오 url 조회 성공\""
			)
		).isTrue();
	}

	@TestFactory
	@DisplayName("비디오 생성 URL 조회 API")
	Collection<DynamicTest> getVideoCreateUrl() {

		return List.of(
			dynamicTest(
				"비디오명에 /가 들어간 경우",
				() -> {
					// given
					VideoCreateUrlApiRequest request = VideoCreateUrlApiRequest.builder()
						.imageType(ImageType.JPG)
						.fileName("wrong/VideoName")
						.build();

					String apiResponse = objectMapper.writeValueAsString(request);

					// when
					ResultActions actions = mockMvc.perform(
						post(BASE_URL + "/presigned-url")
							.contentType(APPLICATION_JSON)
							.accept(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
							.content(apiResponse)
					);

					// then
					actions
						.andDo(print())
						.andExpect(status().isConflict());
				}
			),
			dynamicTest(
				"한 채널에 같은 이름의 비디오가 존재하는 경우",
				() -> {
					// given
					String sameVideoName = memberRepository.findById(loginMember.getMemberId())
						.orElseThrow().getChannel().getVideos().get(0).getVideoName();

					VideoCreateUrlApiRequest request = VideoCreateUrlApiRequest.builder()
						.imageType(ImageType.JPG)
						.fileName(sameVideoName)
						.build();

					String apiResponse = objectMapper.writeValueAsString(request);

					// when
					ResultActions actions = mockMvc.perform(
						post(BASE_URL + "/presigned-url")
							.contentType(APPLICATION_JSON)
							.accept(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
							.content(apiResponse)
					);

					// then
					actions
						.andDo(print())
						.andExpect(status().isConflict());
				}
			),
			dynamicTest(
				"put url 생성 성공",
				() -> {
					// given
					VideoCreateUrlApiRequest request = VideoCreateUrlApiRequest.builder()
						.imageType(ImageType.JPG)
						.fileName("createUrlTest")
						.build();

					String apiResponse = objectMapper.writeValueAsString(request);

					// when
					ResultActions actions = mockMvc.perform(
						post(BASE_URL + "/presigned-url")
							.contentType(APPLICATION_JSON)
							.accept(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
							.content(apiResponse)
					);

					// then
					actions
						.andDo(print())
						.andExpect(status().isOk());

					String response =
						actions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

					assertThat(response.contains("\"thumbnailUrl\"")).isTrue();
					assertThat(response.contains("\"videoUrl\"")).isTrue();
					assertThat(response.contains("\"code\":200,\"status\":\"OK\",\"message\":\"put url 생성 성공\""))
						.isTrue();
				}
			)
		);
	}

	@TestFactory
	@DisplayName("비디오 생성 API")
	Collection<DynamicTest> createVideo() {

		return List.of(
			dynamicTest(
				"비디오 생성 성공",
				() -> {
					// given
					String videoName = "createVideoTest";

					VideoCreateUrlApiRequest createUrlRequest = VideoCreateUrlApiRequest.builder()
						.imageType(ImageType.JPG)
						.fileName(videoName)
						.build();

					String createUrlApiResponse =
						objectMapper.writeValueAsString(createUrlRequest);

					ResultActions createUrlActions = mockMvc.perform(
						post(BASE_URL + "/presigned-url")
							.contentType(APPLICATION_JSON)
							.accept(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
							.content(createUrlApiResponse)
					);

					Video beforeVideo = videoRepository.findVideoByNameWithMember(
						loginMember.getMemberId(), videoName
					).orElseThrow();

					Long videoId = beforeVideo.getVideoId();

					em.flush();
					em.clear();

					given(mockAwsService.isExistFile(Mockito.anyString(), Mockito.any(FileType.class)))
						.willReturn(true);

					VideoCreateApiRequest request = VideoCreateApiRequest.builder()
						.videoName(videoName)
						.price(10000)
						.description("description")
						.categories(List.of("Java"))
						.build();

					String apiResponse =
						objectMapper.writeValueAsString(request);

					// when
					ResultActions actions = mockMvc.perform(
						post(BASE_URL)
							.contentType(APPLICATION_JSON)
							.accept(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
							.content(apiResponse)
					);

					em.flush();
					em.clear();

					// then
					actions.andDo(print())
						.andExpect(status().isCreated())
						.andExpect(header().string("Location", "/videos/" + videoId));

					Video video = videoRepository.findVideoByNameWithMember(
						loginMember.getMemberId(), videoName
					).orElseThrow();

					assertThat(video.getVideoCategories().get(0).getCategory().getCategoryName())
						.isEqualTo(request.getCategories().get(0));
					assertThat(video.getPrice()).isEqualTo(request.getPrice());
					assertThat(video.getDescription()).isEqualTo(request.getDescription());
				}
			)
		);
	}

	@TestFactory
	@DisplayName("비디오 수정 API")
	Collection<DynamicTest> updateVideo() {

		return List.of(
			dynamicTest(
				"비디오 수정 성공",
				() -> {
					// given
					Video video = memberRepository.findById(loginMember.getMemberId())
						.orElseThrow().getChannel().getVideos().get(0);

					Long videoId = video.getVideoId();

					String before = video.getDescription();

					VideoUpdateApiRequest request = VideoUpdateApiRequest.builder()
						.description("change")
						.build();

					// when
					ResultActions actions = mockMvc.perform(
						patch(BASE_URL + "/{videoId}", videoId)
							.contentType(APPLICATION_JSON)
							.accept(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
							.content(objectMapper.writeValueAsString(request))
					);

					// then
					actions.andDo(print())
						.andExpect(status().isNoContent());

					String after = videoRepository.findById(videoId).orElseThrow().getDescription();

					assertThat(after).isNotEqualTo(before);
					assertThat(after).isEqualTo(request.getDescription());
				}
			)
		);
	}

	@TestFactory
	@DisplayName("장바구니 담기 API")
	Collection<DynamicTest> changeCart() {

		return List.of(
			dynamicTest(
				"장바구니 담기",
				() -> {
					// given
					Long videoId = isNotPurchasedVideo.getVideoId();

					int beforeCartSize = memberRepository.findById(loginMember.getMemberId())
						.orElseThrow().getCarts().size();

					String expect =
						objectMapper.writeValueAsString(
							ApiSingleResponse.ok(true, "장바구니 담기 성공")
						);

					// when
					ResultActions actions = mockMvc.perform(
						patch(BASE_URL + "/{videoId}/carts", videoId)
							.contentType(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
					);

					// then
					String actual =
						actions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

					actions.andDo(print())
						.andExpect(status().isOk());

					assertThat(actual).isEqualTo(expect);
				}
			),
			dynamicTest(
				"장바구니 취소",
				() -> {
					// given
					Long videoId = isNotPurchasedVideo2.getVideoId();

					String expect =
						objectMapper.writeValueAsString(
							ApiSingleResponse.ok(false, "장바구니 취소 성공")
						);

					// when
					ResultActions actions = mockMvc.perform(
						patch(BASE_URL + "/{videoId}/carts", videoId)
							.contentType(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
					);

					// then
					String actual =
						actions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

					actions.andDo(print())
						.andExpect(status().isOk());

					assertThat(actual).isEqualTo(expect);
				}
			)
		);
	}

	@Test
	@DisplayName("videoId 를 통한 장바구니 전체 취소 API")
	void deleteCarts() throws Exception {
		// given
		Member member = memberRepository.findById(loginMember.getMemberId()).orElseThrow();
		Video video1 = videoRepository.findById(isNotSubscribedVideo1.getVideoId()).orElseThrow();
		Video video2 = videoRepository.findById(isNotSubscribedVideo2.getVideoId()).orElseThrow();

		createAndSaveCart(member, video1);
		createAndSaveCart(member, video2);

		List<Long> videoIds = List.of(video1.getVideoId(), video2.getVideoId());

		em.flush();
		em.clear();

		int beforeCartSize = memberRepository.findById(loginMember.getMemberId())
			.orElseThrow().getCarts().size();

		VideoCartDeleteApiRequest request = VideoCartDeleteApiRequest.builder()
			.videoIds(videoIds)
			.build();


		// when
		ResultActions actions = mockMvc.perform(
			delete(BASE_URL + "/carts")
				.contentType(APPLICATION_JSON)
				.header(AUTHORIZATION, loginMemberAccessToken)
				.content(objectMapper.writeValueAsString(request))
		);

		em.flush();
		em.clear();

		// then
		actions.andDo(print())
			.andExpect(status().isNoContent());

		int afterCartSize = memberRepository.findById(loginMember.getMemberId())
			.orElseThrow().getCarts().size();

		assertThat(afterCartSize).isEqualTo(beforeCartSize - videoIds.size());
	}

	@TestFactory
	@DisplayName("비디오 열기/폐쇄 API")
	Collection<DynamicTest> changeVideoStatus() {

		return List.of(
			dynamicTest(
				"비디오 열기",
				() -> {
					// given
					Long videoId = loginMemberClosedVideo.getVideoId();

					String expect = objectMapper.writeValueAsString(
						ApiSingleResponse.ok(true, "비디오 열기")
					);

					// when
					ResultActions actions = mockMvc.perform(
						patch(BASE_URL + "/{videoId}/status", videoId)
							.header(AUTHORIZATION, loginMemberAccessToken)
					);

					em.flush();
					em.clear();

					// then
					actions.andDo(print())
						.andExpect(status().isOk());

					String actual = actions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
					VideoStatus videoStatus = videoRepository.findById(videoId).get().getVideoStatus();


					assertThat(actual).isEqualTo(expect);
					assertThat(videoStatus).isEqualTo(VideoStatus.CREATED);
				}
			),
			dynamicTest(
				"비디오 폐쇄",
				() -> {
					// given
					Long videoId = memberRepository.findById(loginMember.getMemberId())
						.orElseThrow().getChannel().getVideos().get(0).getVideoId();

					String expect = objectMapper.writeValueAsString(
						ApiSingleResponse.ok(false, "비디오 폐쇄")
					);

					// when
					ResultActions actions = mockMvc.perform(
						patch(BASE_URL + "/{videoId}/status", videoId)
							.header(AUTHORIZATION, loginMemberAccessToken)
					);

					em.flush();
					em.clear();

					// then
					actions.andDo(print())
						.andExpect(status().isOk());

					String actual = actions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
					VideoStatus videoStatus = videoRepository.findById(videoId).get().getVideoStatus();

					assertThat(actual).isEqualTo(expect);
					assertThat(videoStatus).isEqualTo(VideoStatus.CLOSED);
				}
			)
		);
	}

	@TestFactory
	@DisplayName("비디오의 댓글 조회 API")
	Collection<DynamicTest> getReplies() {

		return List.of(
			dynamicTest(
				"최신순",
				() -> {
					// given
					Long videoId = replyTestVideo.getVideoId();

					// when
					ResultActions actions = mockMvc.perform(
						get(BASE_URL + "/{videoId}/replies", videoId)
							.param("sort", "created-date")
							.header(AUTHORIZATION, loginMemberAccessToken)
					);

					// then
					actions.andDo(print())
						.andExpect(status().isOk());

					ApiPageResponse<ReplyInfo> pages =
						getApiPageResponseFromResult(actions, ReplyInfo.class);

					List<ReplyInfo> replyInfos = pages.getData();
					PageInfo pageInfo = pages.getPageInfo();

					assertThat(replyInfos).isSortedAccordingTo(Comparator.comparing(ReplyInfo::getCreatedDate).reversed());
					assertThat(pageInfo.getTotalSize()).isEqualTo(4);
					assertThat(pageInfo.getTotalPage()).isEqualTo(1);
				}
			),
			dynamicTest(
				"별점순",
				() -> {
					// given
					Long videoId = replyTestVideo.getVideoId();

					// when
					ResultActions actions = mockMvc.perform(
						get(BASE_URL + "/{videoId}/replies", videoId)
							.param("sort", "star")
							.header(AUTHORIZATION, loginMemberAccessToken)
					);

					// then
					actions.andDo(print())
						.andExpect(status().isOk());

					ApiPageResponse<ReplyInfo> pages =
						getApiPageResponseFromResult(actions, ReplyInfo.class);

					List<ReplyInfo> replyInfos = pages.getData();
					PageInfo pageInfo = pages.getPageInfo();

					assertThat(replyInfos).isSortedAccordingTo(Comparator.comparing(ReplyInfo::getStar).reversed());
					assertThat(pageInfo.getTotalSize()).isEqualTo(4);
					assertThat(pageInfo.getTotalPage()).isEqualTo(1);
				}
			),
			dynamicTest(
				"지정한 별점 이상만 높은순으로 표시",
				() -> {
					// given
					Long videoId = replyTestVideo.getVideoId();

					// when
					ResultActions actions = mockMvc.perform(
						get(BASE_URL + "/{videoId}/replies", videoId)
							.param("sort", "star")
							.param("star", "5")
							.header(AUTHORIZATION, loginMemberAccessToken)
					);

					// then
					actions.andDo(print())
						.andExpect(status().isOk());

					ApiPageResponse<ReplyInfo> pages =
						getApiPageResponseFromResult(actions, ReplyInfo.class);

					List<ReplyInfo> replyInfos = pages.getData();

					assertThat(replyInfos).isSortedAccordingTo(Comparator.comparing(ReplyInfo::getStar).reversed());

					for (ReplyInfo replyInfo : replyInfos) {
						assertThat(replyInfo.getStar()).isGreaterThanOrEqualTo(5);
					}
				}
			)
		);
	}

	@TestFactory
	@DisplayName("비디오 댓글 생성 API")
	Collection<DynamicTest> createReply() {

		return List.of(
			dynamicTest(
				"구매하지 않은 비디오인 경우",
				() -> {
					// given
					Long videoId = replyTestVideo.getVideoId();

					CreateReply request = CreateReply.builder()
						.content("댓글 내용")
						.star(5)
						.build();

					// when
					ResultActions actions = mockMvc.perform(
						post(BASE_URL + "/{videoId}/replies", videoId)
							.contentType(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
							.content(objectMapper.writeValueAsString(request))
					);

					actions
						.andDo(print())
						.andExpect(status().isForbidden());
				}
			),
			dynamicTest(
				"구매한 비디오인 경우",
				() -> {
					// given
					Long videoId = isPurchasedVideo.getVideoId();

					CreateReply request = CreateReply.builder()
						.content("댓글 내용")
						.star(5)
						.build();

					// when
					ResultActions actions = mockMvc.perform(
						post(BASE_URL + "/{videoId}/replies", videoId)
							.contentType(APPLICATION_JSON)
							.header(AUTHORIZATION, loginMemberAccessToken)
							.content(objectMapper.writeValueAsString(request))
					);

					em.flush();
					em.clear();

					actions
						.andDo(print())
						.andExpect(status().isCreated());

					String location = actions.andReturn().getResponse().getHeader("Location");
					Long replyId = Long.valueOf(location.replace("/replies/", ""));

					Reply reply = replyRepository.findById(replyId).orElseThrow();

					assertThat(reply.getContent()).isEqualTo(request.getContent());
					assertThat(reply.getStar()).isEqualTo(request.getStar());
				}
			)
		);
	}

	private List<QuestionCreateApiRequest> createQuestionCreateApiRequests(int count) {

		List<QuestionCreateApiRequest> requests = new ArrayList<>();

		for(int i = 1; i <= count; i++) {
			QuestionCreateApiRequest request = QuestionCreateApiRequest.builder()
				.content("문제 내용" + i)
				.questionAnswer("정답" + i)
				.description("해설" + i)
				.selections(List.of("선택1", "선택2", "선택3"))
				.build();

			requests.add(request);
		}

		return requests;
	}
}
