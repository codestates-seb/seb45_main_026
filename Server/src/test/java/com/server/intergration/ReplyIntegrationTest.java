package com.server.intergration;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.reply.dto.ReplyInfo;
import com.server.domain.reply.dto.ReplyUpdateControllerApi;
import com.server.domain.reply.entity.Reply;
import com.server.domain.video.entity.Video;
import com.server.global.reponse.ApiSingleResponse;
import org.junit.jupiter.api.*;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.server.auth.util.AuthConstant.AUTHORIZATION;
import static com.server.auth.util.AuthConstant.BEARER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReplyIntegrationTest extends IntegrationTest {

    private boolean isSetting = false;

    // 로그인한 사용자 정보
    Member loginMember;
    Channel loginMemberChannel;
    List<Video> loginMemberVideos = new ArrayList<>();
    List<Reply> loginMemberReplies = new ArrayList<>();
    String loginMemberEmail = "login@email.com";
    String loginMemberPassword = "qwer1234!";
    String loginMemberAccessToken;

    // 다른 사용자들의 정보
    Member otherMember1;
    Channel otherMemberChannel1;

    List<Video> otherMemberVideos1 = new ArrayList<>();

    String otherMemberEmail1 = "other1@email.com";
    String otherMemberPassword = "other1234!";

    @BeforeEach
    void before() {

        if (isSetting) {
            return;
        }

        loginMember = createAndSaveMemberWithEmailPassword(loginMemberEmail, loginMemberPassword);
        loginMemberChannel = createChannel(loginMember);

        for (int i = 0; i < 2; i++) {
            loginMemberVideos.add(createAndSaveFreeVideo(loginMemberChannel));
            loginMemberVideos.add(createAndSavePaidVideo(loginMemberChannel, 10000));
        }

        loginMemberAccessToken = BEARER + createAccessToken(loginMember, 360000);

        otherMember1 = createAndSaveMemberWithEmailPassword(otherMemberEmail1, otherMemberPassword);

        otherMemberChannel1 = createChannel(otherMember1);

        for (int i = 0; i < 3; i++) {
            otherMemberVideos1.add(createAndSavePaidVideo(otherMemberChannel1, 10000));
        }

        for (int i = 0; i < 2; i++) {
            otherMemberVideos1.add(createAndSaveFreeVideo(otherMemberChannel1));
        }

        for (int i = 0; i < otherMemberVideos1.size(); i++) {
            List<Video> videos = List.of(
                    otherMemberVideos1.get(i)
            );
        }

        for (int i = 0; i < 1; i++) {
            loginMemberReplies.add(createAndSaveReply(loginMember, otherMemberVideos1.get(i)));
        }

        em.flush();
        em.clear();

        isSetting = true;
    }

    @Test
    @DisplayName("댓글 조회을 단건으로 조회한다.")
    void getReplySingle() throws Exception {
        Reply reply = loginMemberReplies.get(0);

        ResultActions actions = mockMvc.perform(
                get("/replies/{reply-id}", reply.getReplyId())
                        .header(AUTHORIZATION, "")
                        .accept(APPLICATION_JSON)
        );

        actions
                .andDo(print())
                .andExpect(status().isOk());

        ApiSingleResponse<ReplyInfo> response = getApiSingleResponseFromResult(actions, ReplyInfo.class);
        ReplyInfo replyInfo = response.getData();

        assertEquals(replyInfo.getReplyId(), reply.getReplyId());
        assertEquals(replyInfo.getContent(), reply.getContent());
        assertEquals(replyInfo.getStar(), reply.getStar());
    }

    @TestFactory
    @DisplayName("댓글을 수정한다.")
    Collection<DynamicTest> updateReply() throws Exception {
        Reply reply = loginMemberReplies.get(0);
        ResultActions actions = mockMvc.perform(
                patch("/replies/{reply-id}", reply.getReplyId())
                        .header(AUTHORIZATION, loginMemberAccessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReplyUpdateControllerApi("new content", 5)))
                        .accept(APPLICATION_JSON)
        );

        return List.of(
                dynamicTest("로그인한 사용자가 댓글을 수정한다.", () -> {
                    for (Reply reply1 : loginMemberReplies) {
                        String content = "new content";
                        int star = 5;

                        actions
                                .andDo(print())
                                .andExpect(status().isNoContent());

                        Reply findReply = replyRepository.findById(reply1.getReplyId()).orElse(null);
                        assertEquals(content, findReply.getContent());
                        assertEquals(star, findReply.getStar());
                    }
                }),

                dynamicTest("비로그인 사용자가 수정을 하려고 하면 예외가 발생한다", () -> {
                    Reply reply2 = loginMemberReplies.get(0);

                    ResultActions anonymousActions = mockMvc.perform(
                            patch("/replies/{reply-id}", reply2.getReplyId())
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(new ReplyUpdateControllerApi("new Content", 5)))
                                    .accept(APPLICATION_JSON)
                    );

                    anonymousActions
                            .andDo(print())
                            .andExpect(status().isForbidden());
                })
        );
    }

    @TestFactory
    @DisplayName("댓글을 삭제한다.")
    Collection<DynamicTest> deleteReply() throws Exception {

        return List.of(
                dynamicTest("로그인한 사용자가 자신의 댓글을 삭제한다.", () -> {
                    Reply reply = loginMemberReplies.get(0);

                    ResultActions actions = mockMvc.perform(
                            delete("/replies/{reply-id}", reply.getReplyId())
                                    .header(AUTHORIZATION, loginMemberAccessToken)
                                    .accept(APPLICATION_JSON)
                    );

                    actions
                            .andDo(print())
                            .andExpect(status().isNoContent());

                    boolean existReply = (replyRepository.findById(reply.getReplyId()).isPresent());
                    assertThat(existReply).isFalse();
                }),

                dynamicTest("비로그인 사용자가 댓글 삭제를 시도하면 예외가 발생한다", () -> {
                    Reply reply = loginMemberReplies.get(0);

                    ResultActions unauthorizedActions = mockMvc.perform(
                            delete("/replies/{reply-id}", reply.getReplyId())
                                    .accept(APPLICATION_JSON)
                    );

                    unauthorizedActions
                            .andDo(print())
                            .andExpect(status().isForbidden());
                })
        );
    }
}