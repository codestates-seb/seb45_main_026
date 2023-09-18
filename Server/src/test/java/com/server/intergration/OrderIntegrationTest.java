package com.server.intergration;

import static com.server.auth.util.AuthConstant.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;

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

	Video openFreeVideo;
	Video openPaidVideo;
	Video fullRewardPaidVideo;
	Video closedFreeVideo;
	Video closedPaidVideo;


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

		openFreeVideo = createAndSaveFreeVideo(otherMemberChannel1);
		openPaidVideo = createAndSavePaidVideo(otherMemberChannel1, 20000);
		fullRewardPaidVideo = createAndSavePaidVideo(otherMemberChannel1, 10000);
		closedFreeVideo = createAndSaveFreeVideo(otherMemberChannel1);
		closedPaidVideo = createAndSaveFreeVideo(otherMemberChannel1);

		for (int i = 0; i < 5; i++) {
			createAndSavePaidVideo(otherMemberChannel2, 10000);
			createAndSavePaidVideo(otherMemberChannel3, 10000);
			createAndSaveFreeVideo(otherMemberChannel4);
			createAndSaveFreeVideo(otherMemberChannel5);
		}

	}
}
