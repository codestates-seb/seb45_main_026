package com.server.intergration;

import static com.server.auth.util.AuthConstant.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.data.web.JsonPath;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.server.domain.cart.entity.Cart;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.controller.dto.MemberApiRequest;
import com.server.domain.member.entity.Member;
import com.server.domain.member.service.dto.response.ProfileResponse;
import com.server.domain.member.service.dto.response.RewardsResponse;
import com.server.domain.order.entity.Order;
import com.server.domain.question.entity.Question;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reward.entity.Reward;
import com.server.domain.reward.entity.Rewardable;
import com.server.domain.subscribe.entity.Subscribe;
import com.server.domain.video.entity.Video;
import com.server.domain.watch.entity.Watch;
import com.server.global.reponse.ApiPageResponse;
import com.server.module.s3.service.dto.FileType;
import com.server.module.s3.service.dto.ImageType;

public class MemberIntergrationTest extends IntegrationTest {

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
	@Transactional
	void pre() {
		loginMember = createAndSaveMemberWithEmailPassword(loginMemberEmail, loginMemberPassword);
		loginMemberChannel = createChannel(loginMember);

		for (int i = 0; i < 2; i++) {
			loginMemberVideos.add(createAndSaveFreeVideo(loginMemberChannel));
			loginMemberVideos.add(createAndSavePaidVideo(loginMemberChannel, 10000));
		}

		loginMemberAccessToken = BEARER + createAccessToken(loginMember, 36000);

		otherMember1 = createAndSaveMemberWithEmailPassword(otherMemberEmail1, otherMemberPassword);
		otherMember2 = createAndSaveMemberWithEmailPassword(otherMemberEmail2, otherMemberPassword);
		otherMember3 = createAndSaveMemberWithEmailPassword(otherMemberEmail3, otherMemberPassword);
		otherMember4 = createAndSaveMemberWithEmailPassword(otherMemberEmail4, otherMemberPassword);

		otherMemberChannel1 = createChannel(otherMember1);
		otherMemberChannel2 = createChannel(otherMember2);
		otherMemberChannel3 = createChannel(otherMember3);
		otherMemberChannel4 = createChannel(otherMember4);

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

		for (int i = 0; i < loginMemberReplies.size(); i++) {
			loginMemberRewards.add(createAndSaveReward(loginMember, loginMemberReplies.get(i)));
		}

		flushAll();
	}

	@TestFactory
	@DisplayName("자신의 프로필이미지, 이메일, 닉네임을 조회한다.")
	Collection<DynamicTest> getMember() throws Exception {

		em.flush();
		em.clear();

		ResultActions actions = mockMvc.perform(
			get("/members")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.accept(APPLICATION_JSON)
		);

		actions
			.andDo(print())
			.andExpect(status().isOk());

		ProfileResponse profileResponse = getApiSingleResponseFromResult(actions, ProfileResponse.class).getData();

		return List.of(
			dynamicTest(
				"조회되는 회원의 아이디가 로그인한 회원과 일치하는가",
				() -> {
					assertThat(profileResponse.getMemberId()).isEqualTo(loginMember.getMemberId());
				}
			),
			dynamicTest(
				"조회되는 회원의 이메일이 로그인한 회원과 일치하는가",
				() -> {
					assertThat(profileResponse.getEmail()).isEqualTo(loginMemberEmail);
				}
			),
			dynamicTest(
				"조회되는 회원의 닉네임이 로그인한 회원과 일치하는가",
				() -> {
					assertThat(profileResponse.getNickname()).isEqualTo(loginMember.getNickname());
				}
			),dynamicTest(
				"조회되는 회원의 프로필 이미지가 로그인한 회원과 일치하는가",
				() -> {
					assertThat(profileResponse.getImageUrl()).isEqualTo(getProfileUrl(loginMember));
				}
			),dynamicTest(
				"조회되는 회원의 등급이 로그인한 회원과 일치하는가",
				() -> {
					assertThat(profileResponse.getGrade()).isEqualTo(loginMember.getGrade());
				}
			),dynamicTest(
				"조회되는 회원의 보유한 리워드가 로그인한 회원과 일치하는가",
				() -> {
					assertThat(profileResponse.getReward()).isEqualTo(loginMember.getReward());
				}
			),dynamicTest(
				"조회되는 회원의 가입일이 로그인한 회원과 일치하는가",
				() -> {
					assertThat(profileResponse.getCreatedDate()).isBeforeOrEqualTo(loginMember.getCreatedDate());
				}
			)
		);
	}

	@TestFactory
	@DisplayName("자신의 리워드 목록을 조회한다.")
	Collection<DynamicTest> getRewards() throws Exception {

		ResultActions actions = mockMvc.perform(
			get("/members/rewards")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.param("page","1")
				.param("size","16")
				.accept(APPLICATION_JSON)
		);

		actions
			.andDo(print())
			.andExpect(status().isOk());

		ApiPageResponse<RewardsResponse> rewardsResponse =
			getApiPageResponseFromResult(actions, RewardsResponse.class);

		return List.of(
			dynamicTest(
				"1",
				() -> {

				}
			)
		);
	}

	@TestFactory
	@DisplayName("자신의 구독 목록을 조회한다.")
	Collection<DynamicTest> getSubscribes() throws Exception {

		ResultActions actions = mockMvc.perform(
			get("/members/subscribes")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.param("page","1")
				.param("size","16")
				.accept(APPLICATION_JSON)
		);

		return List.of(
			dynamicTest(
				"1",
				() -> {

				}
			)
		);
	}

	@TestFactory
	@DisplayName("자신의 장바구니 목록을 조회한다.")
	Collection<DynamicTest> getCarts() throws Exception {

		ResultActions actions = mockMvc.perform(
			get("/members/carts")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.param("page","1")
				.param("size","20")
				.accept(APPLICATION_JSON)
		);

		return List.of(
			dynamicTest(
				"1",
				() -> {

				}
			)
		);
	}

	@TestFactory
	@DisplayName("자신의 결제 내역을 조회한다.")
	Collection<DynamicTest> getOrders() throws Exception {

		ResultActions actions = mockMvc.perform(
			get("/members/orders")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.param("page", "1")
				.param("size","4")
				.param("month", "1")
				.accept(APPLICATION_JSON)
		);

		return List.of(
			dynamicTest(
				"1",
				() -> {

				}
			)
		);
	}

	@TestFactory
	@DisplayName("자신의 구매한 강의 목록을 조회한다.")
	Collection<DynamicTest> getPlaylists() throws Exception {

		ResultActions actions = mockMvc.perform(
			get("/members/playlists")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.param("page",  "1")
				.param("size", "16")
				.param("sort", "star")
				.accept(APPLICATION_JSON)
		);

		return List.of(
			dynamicTest(
				"1",
				() -> {

				}
			)
		);
	}

	@TestFactory
	@DisplayName("자신의 구매한 강의 목록을 채널별로 그룹화 해서 조회한다.")
	Collection<DynamicTest> getPlaylistChannels() throws Exception {

		ResultActions actions = mockMvc.perform(
			get("/members/playlists/channels")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.param("page","1")
				.param("size","16")
				.accept(APPLICATION_JSON)
		);

		return List.of(
			dynamicTest(
				"1",
				() -> {

				}
			)
		);
	}

	@TestFactory
	@DisplayName("자신의 구매한 강의 목록을 채널별로 상세 조회한다.")
	Collection<DynamicTest> getPlaylistChannelDetails() throws Exception {

		ResultActions actions = mockMvc.perform(
			get("/members/playlists/channels/details")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.param("member-id","1")
				.accept(APPLICATION_JSON)
		);

		return List.of(
			dynamicTest(
				"1",
				() -> {

				}
			)
		);
	}

	@TestFactory
	@DisplayName("자신의 시청 기록을 조회한다.")
	Collection<DynamicTest> getWatchs() throws Exception {

		ResultActions actions = mockMvc.perform(
			get("/members/watchs")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.param("page", "1")
				.param("size","16")
				.param("day", "30")
		);

		return List.of(
			dynamicTest(
				"1",
				() -> {

				}
			)
		);
	}

	@TestFactory
	@DisplayName("자신의 닉네임을 변경한다.")
	Collection<DynamicTest> updateNickname() throws Exception {

		MemberApiRequest.Nickname nickname = new MemberApiRequest.Nickname("testnickname");

		String content = objectMapper.writeValueAsString(nickname);

		ResultActions actions = mockMvc.perform(
			patch("/members")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.contentType(APPLICATION_JSON)
				.content(content)
		);

		return List.of(
			dynamicTest(
				"1",
				() -> {

				}
			)
		);
	}

	@TestFactory
	@DisplayName("프로필 이미지를 변경한다.")
	Collection<DynamicTest> updateImage() throws Exception {

		String imageName = "profile20230907140835";
		MemberApiRequest.Image request = new MemberApiRequest.Image(
			imageName, ImageType.JPG
		);

		String content = objectMapper.writeValueAsString(request);

		ResultActions actions = mockMvc.perform(
			patch("/members/image")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.contentType(APPLICATION_JSON)
				.content(content)
		);

		return List.of(
			dynamicTest(
				"1",
				() -> {

				}
			)
		);
	}

	@TestFactory
	@DisplayName("비밀번호 변경 시 새 비밀번호를 입력하고 비밀번호를 변경한다.")
	Collection<DynamicTest> updatePassword() throws Exception {

		MemberApiRequest.Password request = new MemberApiRequest.Password(
			"abcde12345!", "12345abcde!"
		);

		String content = objectMapper.writeValueAsString(request);

		ResultActions actions = mockMvc.perform(
			patch("/members/password")
				.header(AUTHORIZATION, loginMemberAccessToken)
				.contentType(APPLICATION_JSON)
				.content(content)
		);

		return List.of(
			dynamicTest(
				"기존 비밀번호가 잘못된 경우",
				() -> {

				}
			)
		);
	}

	@TestFactory
	@DisplayName("자신의 프로필 이미지를 삭제한다.")
	Collection<DynamicTest> deleteImage() throws Exception {

		ResultActions actions = mockMvc.perform(
			delete("/members/image")
				.header(AUTHORIZATION, loginMemberAccessToken)
		);

		return List.of(
			dynamicTest(
				"기존 비밀번호가 잘못된 경우",
				() -> {

				}
			)
		);
	}

	@TestFactory
	@DisplayName("회원을 탈퇴시키고 관련된 모든 정보를 삭제 및 갱신한다.")
	Collection<DynamicTest> deleteMember() throws Exception {

		ResultActions actions = mockMvc.perform(
			delete("/members")
				.header(AUTHORIZATION, loginMemberAccessToken)
		);

		return List.of(
			dynamicTest(
				"기존 비밀번호가 잘못된 경우",
				() -> {

				}
			)
		);
	}
}
