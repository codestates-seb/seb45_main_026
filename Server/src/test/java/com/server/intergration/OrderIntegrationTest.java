package com.server.intergration;

import static com.server.auth.util.AuthConstant.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.springframework.test.web.servlet.ResultActions;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.order.controller.dto.request.OrderCreateApiRequest;
import com.server.domain.order.entity.Order;
import com.server.domain.order.service.dto.response.OrderResponse;
import com.server.domain.video.entity.Video;
import com.server.global.exception.businessexception.orderexception.OrderNotFoundException;
import com.server.global.reponse.ApiSingleResponse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderIntegrationTest extends IntegrationTest {

	private final String BASE_URL = "/orders";

	Member loginMember;
	Channel loginMemberChannel;
	String loginMemberEmail = "login@email.com";
	String loginMemberPassword = "qwer1234!";
	String loginMemberAccessToken;
	private final int LOGIN_MEMBER_REWARD = 10000;

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

	String otherMemberEmail1 = "other1@email.com";
	String otherMemberEmail2 = "other2@email.com";
	String otherMemberEmail3 = "other3@email.com";
	String otherMemberEmail4 = "other4@email.com";
	String otherMemberEmail5 = "other5@email.com";
	String otherMemberPassword = "other1234!";

	String otherMemberToken5;

	Video openFreeVideo;
	Video openPaidVideo;
	Video fullRewardPaidVideo;
	Video closedFreeVideo;
	Video closedPaidVideo;
	Video orderCompletedVideo;


	@BeforeAll
	void init() {
		loginMember = createAndSaveMemberWithEmailPasswordReward(
			loginMemberEmail, loginMemberPassword, LOGIN_MEMBER_REWARD
		);
		loginMemberChannel = createChannelWithRandomName(loginMember);
		loginMemberAccessToken = BEARER + createAccessToken(loginMember, 123456789);

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

		otherMemberToken5 = BEARER + createAccessToken(otherMember5, 123456789);

		openFreeVideo = createAndSaveFreeVideo(otherMemberChannel1);
		openPaidVideo = createAndSavePaidVideo(otherMemberChannel1, 20000);
		fullRewardPaidVideo = createAndSavePaidVideo(otherMemberChannel1, 10000);
		closedFreeVideo = createAndSaveFreeVideo(otherMemberChannel1);
		closedPaidVideo = createAndSaveFreeVideo(otherMemberChannel1);

		orderCompletedVideo = createAndSavePaidVideo(otherMemberChannel1, 30000);
		createAndSaveOrderWithPurchaseComplete(otherMember5, List.of(orderCompletedVideo), 0);
		createAndSaveOrder(loginMember, List.of(openPaidVideo), 0);

		for (int i = 0; i < 5; i++) {
			createAndSavePaidVideo(otherMemberChannel2, 10000);
			createAndSavePaidVideo(otherMemberChannel3, 10000);
			createAndSaveFreeVideo(otherMemberChannel4);
			createAndSaveFreeVideo(otherMemberChannel5);
		}

		memberRepository.saveAll(List.of(
			loginMember,
			otherMember1,
			otherMember2,
			otherMember3,
			otherMember4,
			otherMember5
		));

		channelRepository.saveAll(List.of(
			loginMemberChannel,
			otherMemberChannel1,
			otherMemberChannel2,
			otherMemberChannel3,
			otherMemberChannel4,
			otherMemberChannel5
		));

	}

	@TestFactory
	@DisplayName("주문 생성 API")
	Collection<DynamicTest> createOrder() {

		return List.of(
			dynamicTest(
				"주문 생성 성공",
				() -> {
					//given
					Member otherMember = memberRepository.findById(otherMember2.getMemberId()).orElseThrow();

					List<Long> videoIds = otherMember.getChannel().getVideos().stream()
						.filter(video -> video.getPrice() > 0)
						.map(Video::getVideoId)
						.collect(Collectors.toList());

					OrderCreateApiRequest request = OrderCreateApiRequest.builder()
						.reward(1000)
						.videoIds(videoIds)
						.build();

					//when
					ResultActions actions = mockMvc.perform(
						post(BASE_URL)
						.header(AUTHORIZATION, loginMemberAccessToken)
						.contentType(APPLICATION_JSON)
						.accept(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
					);

					//then
					actions
						.andDo(print())
						.andExpect(status().isOk());

					ApiSingleResponse<OrderResponse> apiSingleResponse =
						getApiSingleResponseFromResult(actions, OrderResponse.class);

					OrderResponse response = apiSingleResponse.getData();

					assertDoesNotThrow(
						() -> orderRepository.findById(response.getOrderId())
							.orElseThrow(OrderNotFoundException::new)
					);
					assertThat(response.getTotalAmount()).isEqualTo(49000);
				}
			)
		);
	}

	@TestFactory
	@DisplayName("주문 성공 API")
	Collection<DynamicTest> success() {

		return List.of(
			dynamicTest(
				"존재하지 않거나 만료된 paymentKey인 경우",
				() -> {
					//given
					Order order = memberRepository.findById(loginMember.getMemberId()).orElseThrow()
						.getOrders().get(0);

					String orderId = order.getOrderId();
					String paymentKey = "paymentKey";
					Integer amount = 20000;

					//when
					ResultActions actions = mockMvc.perform(
						get(BASE_URL + "/success")
							.header(AUTHORIZATION, loginMemberAccessToken)
							.accept(APPLICATION_JSON)
							.param("order-id", orderId)
							.param("payment-key", paymentKey)
							.param("amount", String.valueOf(amount))
					);

					//then
					actions
						.andDo(print())
						.andExpect(status().isInternalServerError());
				}
			),
			dynamicTest(
				"존재하지 않는 주문인 경우",
				() -> {
					//given
					String wrongOrderId = "abcdefg-hijklmn-opqrstu-vwxyz-1234567890";
					String paymentKey = "paymentKey";
					Integer amount = 20000;

					//when
					ResultActions actions = mockMvc.perform(
						get(BASE_URL + "/success")
							.header(AUTHORIZATION, loginMemberAccessToken)
							.accept(APPLICATION_JSON)
							.param("order-id", wrongOrderId)
							.param("payment-key", paymentKey)
							.param("amount", String.valueOf(amount))
					);

					//then
					actions
						.andDo(print())
						.andExpect(status().isNotFound());
				}
			)
		);
	}

	// @TestFactory
	// @DisplayName("주문 취소 API")
	// Collection<DynamicTest> cancelOrder() {
	//
	// 	return List.of(
	// 		dynamicTest(
	// 			"주문 취소 성공",
	// 			() -> {
	// 				//given
	// 				Order order = memberRepository.findById(otherMember5.getMemberId()).orElseThrow()
	// 					.getOrders().get(0);
	//
	// 				String orderId = order.getOrderId();
	// 				int totalRequest =
	// 					order.getRemainRefundAmount() + order.getRemainRefundReward();
	//
	// 				when(mockOrderService.orderCancelRequest(any(Order.class), anyInt()))
	//
	// 				//when
	// 				ResultActions actions = mockMvc.perform(
	// 					delete(BASE_URL + "/{order-id}", orderId)
	// 						.header(AUTHORIZATION, otherMemberToken5)
	// 				);
	// 				//then
	// 				actions
	// 					.andDo(print())
	// 					.andExpect(status().isOk());
	//
	// 				ApiSingleResponse<VideoCancelApiResponse> apiSingleResponse =
	// 					getApiSingleResponseFromResult(actions, VideoCancelApiResponse.class);
	//
	// 				VideoCancelApiResponse response = apiSingleResponse.getData();
	//
	// 				assertThat(response.getTotalRequest()).isEqualTo(totalRequest);
	// 				assertThat(response.getUsedReward()).isEqualTo(0);
	// 				assertThat(response.getTotalCancelAmount()).isEqualTo(totalRequest);
	// 				assertThat(response.getTotalCancelReward()).isEqualTo(0);
	// 			}
	// 		)
	// 	);
	// }
}
