package com.server.intergration;

import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.service.dto.ChannelInfo;
import com.server.domain.channel.service.dto.ChannelUpdate;
import com.server.domain.channel.service.dto.response.ChannelVideoResponse;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.global.reponse.ApiPageResponse;
import com.server.module.s3.service.dto.FileType;
import org.junit.jupiter.api.*;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.server.auth.util.AuthConstant.AUTHORIZATION;
import static com.server.auth.util.AuthConstant.BEARER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Channel 통합 테스트")
public class ChannelIntegrationTest extends IntegrationTest {

    private boolean isSetting = false;

    // 로그인한 사용자 정보
    Member loginMember;
    Channel loginMemberChannel;
    List<Video> loginMemberVideos = new ArrayList<>();
    String loginMemberEmail = "login@email.com";
    String loginMemberPassword = "qwer1234!";
    String loginMemberAccessToken;

    // 다른 사용자들의 정보
    Member otherMember;
    Channel otherMemberChannel;
    List<Video> otherMemberVideos = new ArrayList<>();
    String otherMemberEmail = "other1@email.com";
    String otherMemberPassword = "other1234!";


    @BeforeEach
    void before() {
        if (isSetting) {
            return;
        }

        loginMember = createAndSaveMemberWithEmailPassword(loginMemberEmail, loginMemberPassword);

        loginMemberChannel = createChannel(loginMember);

        loginMemberAccessToken = BEARER + createAccessToken(loginMember, 360000);

        otherMember = createAndSaveMemberWithEmailPassword(otherMemberEmail, otherMemberPassword);

        otherMemberChannel = createChannelWithRandomName(otherMember);

        for (int i = 0; i < 3; i++) {
            otherMemberVideos.add(createAndSavePaidVideo(otherMemberChannel, 10000));
        }

        isSetting = true;

        em.flush();
        em.clear();

        isSetting = true;
    }

    @Test
    @DisplayName("로그인한 사용자가 채널에 대한 정보(채널 소개, 프로필 이미지 등)를 조회한다.")
    void getChannel() throws Exception {

        ResultActions actions = mockMvc.perform(
                get("/channels/{member-id}", loginMember.getMemberId())
                        .header(AUTHORIZATION, loginMemberAccessToken)
                        .accept(APPLICATION_JSON)
        );

        actions
                .andDo(print())
                .andExpect(status().isOk());

        ChannelInfo channelInfo = getApiSingleResponseFromResult(actions, ChannelInfo.class).getData();

        assertThat(channelInfo.getChannelName()).isEqualTo(loginMemberChannel.getChannelName());
        assertThat(channelInfo.getDescription()).isEqualTo(loginMemberChannel.getDescription());
        assertThat(channelInfo.getImageUrl()).isEqualTo(awsService.getFileUrl(loginMemberChannel.getMember().getImageFile(), FileType.PROFILE_IMAGE));
        assertThat(channelInfo.getMemberId()).isEqualTo(loginMemberChannel.getMember().getMemberId());
        assertThat(channelInfo.getSubscribers()).isEqualTo(loginMemberChannel.getSubscribers());
        assertThat(channelInfo.getCreatedDate()).isAfter(loginMemberChannel.getCreatedDate().minusNanos(500));
        assertThat(channelInfo.getIsSubscribed()).isFalse();
    }

    @TestFactory
    @DisplayName("채널 정보(채널 이름, 채널 소개) 를 수정한다.")
    Collection<DynamicTest> updateChannelInfo() throws Exception {
        ResultActions actions = mockMvc.perform(
                patch("/channels/{member-id}", loginMember.getMemberId())
                        .header(AUTHORIZATION, loginMemberAccessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ChannelUpdate.builder()
                                .channelName(loginMemberChannel.getChannelName())
                                .description(loginMemberChannel.getDescription())
                                .build()))
                        .accept(APPLICATION_JSON)
        );

        return List.of(
                dynamicTest("채널 정보를 수정한다.", () -> {

                    actions
                            .andDo(print())
                            .andExpect(status().isNoContent());

                    assertThat(loginMemberChannel.getChannelName()).isEqualTo("channelName");
                    assertThat(loginMemberChannel.getDescription()).isEqualTo("channelDescription");
                }),

                dynamicTest("비로그인 사용자가 수정을 하려고 하면 예외가 발생한다", () -> {
                    ResultActions anonymousActions = mockMvc.perform(
                            patch("/channels/{member-id}", loginMember.getMemberId())
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(ChannelUpdate.builder()
                                            .channelName("channelName")
                                            .description("channelDescription")
                                            .build()))
                                    .accept(APPLICATION_JSON)
                    );

                    anonymousActions
                            .andDo(print())
                            .andExpect(status().isForbidden());
                })
        );
    }

    @TestFactory
    @DisplayName("채널을 구독한다")
    Collection<DynamicTest> subscribeChannel() throws Exception {
        ResultActions actions = mockMvc.perform(
                patch("/channels/{member-id}/subscribe", otherMemberChannel.getChannelId())
                        .header(AUTHORIZATION, loginMemberAccessToken)
                        .accept(APPLICATION_JSON)
        );

        return List.of(
                dynamicTest("채널을 구독한다.", () -> {
                    actions
                            .andDo(print())
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.data").value(true));
                }),

                dynamicTest("이미 구독한 채널을 다시 구독하면 구독이 취소된다.", () -> {
                    ResultActions resultActions = mockMvc.perform(
                            patch("/channels/{member-id}/subscribe", otherMemberChannel.getChannelId())
                                    .header(AUTHORIZATION, loginMemberAccessToken)
                                    .accept(APPLICATION_JSON)
                    );

                    resultActions
                            .andDo(print())
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.data").value(false));
                }),

                dynamicTest("비로그인 사용자가 채널을 구독하면 예외가 발생한다.", () -> {
                    ResultActions anonymousActions = mockMvc.perform(
                            post("/channels/{member-id}/subscribe", otherMember.getMemberId())
                                    .accept(APPLICATION_JSON)
                                    .header(AUTHORIZATION, ""));

                    anonymousActions
                            .andDo(print())
                            .andExpect(status().isForbidden());
                }),

                dynamicTest("타유저의 채널을 구독하면 타유저의 구독자 수가 1 증가한다", () -> {
                    ResultActions resultActions = mockMvc.perform(
                            patch("/channels/{member-id}/subscribe", otherMemberChannel.getChannelId())
                                    .header(AUTHORIZATION, loginMemberAccessToken)
                                    .accept(APPLICATION_JSON)
                    );

                    resultActions
                            .andDo(print())
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.data").value(true));

                    em.flush();
                    em.clear();

                    Channel channel = channelRepository.findById(otherMemberChannel.getMember().getMemberId()).get();
                    assertThat(channel.getSubscribers()).isEqualTo(1);
                }),

                dynamicTest("타유저의 채널을 구독취소하면 타유저의 구독자 수가 1 감소한다", () -> {
                    ResultActions resultActions = mockMvc.perform(
                            patch("/channels/{member-id}/subscribe", otherMemberChannel.getChannelId())
                                    .header(AUTHORIZATION, loginMemberAccessToken)
                                    .accept(APPLICATION_JSON)
                    );
                    resultActions
                            .andDo(print())
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.data").value(false));

                    em.flush();
                    em.clear();

                    Channel channel = channelRepository.findById(otherMemberChannel.getMember().getMemberId()).get();
                    assertThat(channel.getSubscribers()).isEqualTo(0);
                }),


                dynamicTest("존재하지 않는 채널이면 예외가 발생한다.", () -> {
                    ResultActions resultActions = mockMvc.perform(
                            patch("/channels/{member-id}/subscribe", 999999999)
                                    .header(AUTHORIZATION, loginMemberAccessToken)
                                    .accept(APPLICATION_JSON)
                    );
                    resultActions
                            .andDo(print())
                            .andExpect(status().isNotFound());
                })
        );
    }


    @TestFactory
    @DisplayName("채널의 영상을 페이징 및 정렬된 목록으로 조회한다")
    Collection<DynamicTest> getChannels() throws Exception {
        return Arrays.asList(
                dynamicTest(
                        "영상 최신순 조회",
                        () -> {
                            ResultActions actions = mockMvc.perform(
                                    get("/channels/{member-id}/videos", loginMember.getMemberId())
                                            .header(AUTHORIZATION, loginMemberAccessToken)
                                            .param("page", ("1"))
                                            .param("size", ("16"))
                                            .param("sort", "created-date")
                                            .param("category", "ALL")
                                            .param("free", "false")
                                            .param("is-purchased", "true")

                                            .accept(APPLICATION_JSON)
                            );

                            actions
                                    .andDo(print())
                                    .andExpect(status().isOk());

                            ApiPageResponse<ChannelVideoResponse> response = getApiPageResponseFromResult(actions, ChannelVideoResponse.class);

                            List<ChannelVideoResponse> responses = response.getData();

                            List<Video> expectedVideos = loginMemberVideos;
                            assertThat(responses).isEqualTo(expectedVideos);
                        }
                ),

                dynamicTest(
                        "최신순으로 정렬한다.",
                        () -> {
                            ResultActions actions = mockMvc.perform(
                                    get("/channels/{member-id}/videos", loginMember.getMemberId())
                                            .header(AUTHORIZATION, loginMemberAccessToken)
                                            .param("page", ("1"))
                                            .param("size", ("16"))
                                            .param("sort", "created-date")
                                            .param("category", "ALL")
                                            .param("free", "false")
                                            .param("is-purchased", "true")
                                            .accept(APPLICATION_JSON)
                            );

                            actions
                                    .andDo(print())
                                    .andExpect(status().isOk());

                            ApiPageResponse<ChannelVideoResponse> response = getApiPageResponseFromResult(actions, ChannelVideoResponse.class);

                            List<ChannelVideoResponse> responses = response.getData();

                            List<Video> expectedVideos = loginMemberVideos;
                            assertThat(responses).isEqualTo(expectedVideos);
                        }
                ),

                dynamicTest(
                        "별점순으로 정렬한다.",
                        () -> {
                            ResultActions actions = mockMvc.perform(
                                    get("/channels/{member-id}/videos", loginMember.getMemberId())
                                            .header(AUTHORIZATION, loginMemberAccessToken)
                                            .param("page", ("1"))
                                            .param("size", ("16"))
                                            .param("sort", "star")
                                            .param("category", "ALL")
                                            .param("free", "false")
                                            .param("is-purchased", "true")
                                            .accept(APPLICATION_JSON)
                            );

                            actions
                                    .andDo(print())
                                    .andExpect(status().isOk());

                            ApiPageResponse<ChannelVideoResponse> response = getApiPageResponseFromResult(actions, ChannelVideoResponse.class);

                            List<ChannelVideoResponse> responses = response.getData();

                            List<Video> expectedVideos = loginMemberVideos;
                            assertThat(responses).isEqualTo(expectedVideos);
                        }
                )
        );
    }
}