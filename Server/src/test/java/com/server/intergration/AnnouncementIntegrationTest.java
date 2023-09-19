package com.server.intergration;

import com.server.domain.announcement.entity.Announcement;
import com.server.domain.announcement.service.dto.request.AnnouncementUpdateServiceRequest;
import com.server.domain.announcement.service.dto.response.AnnouncementResponse;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import org.junit.jupiter.api.*;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;

import static com.server.auth.util.AuthConstant.AUTHORIZATION;
import static com.server.auth.util.AuthConstant.BEARER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.data.support.PageableExecutionUtils.getPage;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AnnouncementIntegrationTest extends IntegrationTest {

    private boolean isSetting = false;

    // 로그인한 사용자 정보
    Member loginMember;
    Channel loginMemberChannel;
    Announcement loginMemberAnnouncement;
    String loginMemberEmail = "login@email.com";
    String loginMemberPassword = "qwer1234!";
    String loginMemberAccessToken;


    @BeforeEach
    void before() {
        if (isSetting) {
            return;
        }

        loginMember = createAndSaveMemberWithEmailPassword(loginMemberEmail, loginMemberPassword);

        loginMemberChannel = createChannel(loginMember);

        loginMemberAnnouncement = createAndSaveAnnouncement(loginMemberChannel);

        loginMemberAccessToken = BEARER + createAccessToken(loginMember, 360000);


        em.flush();
        em.clear();

        isSetting = true;
    }

    @Test
    @DisplayName("공지사항을 한 건 조회한다.")
    void getAnnouncement() throws Exception {

        Long announcementId = announcementRepository.findById(loginMemberAnnouncement.getAnnouncementId()).get().getAnnouncementId();

        ResultActions actions = mockMvc.perform(
                get("/announcements/{announcement-id}", announcementId)
                        .header(AUTHORIZATION, "")
                        .accept(APPLICATION_JSON)
        );

        actions
                .andDo(print())
                .andExpect(status().isOk());

        AnnouncementResponse response = getApiSingleResponseFromResult(actions, AnnouncementResponse.class).getData();

        assertThat(response.getContent()).isEqualTo("content");
        assertThat(response.getCreatedDate()).isNotNull();
    }

    @TestFactory
    @DisplayName("공지사항을 수정한다")
    Collection<DynamicTest> updateAnnouncement() {

        return Arrays.asList(
                dynamicTest("공지사항이 존재하지 않으면 예외가 발생한다.", () -> {
                    AnnouncementUpdateServiceRequest request = AnnouncementUpdateServiceRequest.builder()
                            .content("content")
                            .build();

                    ResultActions actions = mockMvc.perform(
                            patch("/announcements/{announcement-id}", 1000000L)
                                    .header(AUTHORIZATION, loginMemberAccessToken)
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                                    .accept(APPLICATION_JSON)
                    );

                    actions
                            .andDo(print())
                            .andExpect(status().isNotFound())
                            .andExpect(jsonPath("message").value("존재하지않는 공지사항입니다."));
                }),

                dynamicTest("로그인한 유저가 아니면 예외가 발생한다.", () -> {
                    AnnouncementUpdateServiceRequest request = AnnouncementUpdateServiceRequest.builder()
                            .content("content")
                            .build();

                    Long announcementId = announcementRepository.findById(loginMemberAnnouncement.getAnnouncementId()).get().getAnnouncementId();

                    ResultActions actions = mockMvc.perform(
                            patch("/announcements/{announcement-id}", announcementId)
                                    .header(AUTHORIZATION, "")
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                                    .accept(APPLICATION_JSON)
                    );

                    actions
                            .andDo(print())
                            .andExpect(status().isForbidden())
                            .andExpect(jsonPath("message").value("접근 권한이 없습니다."));
                }),

                dynamicTest("공지사항을 수정한다.", () -> {

                    Long announcementId = announcementRepository.findById(loginMemberAnnouncement.getAnnouncementId()).get().getAnnouncementId();

                    AnnouncementUpdateServiceRequest request = AnnouncementUpdateServiceRequest.builder()
                            .content("new content")
                            .build();

                    ResultActions resultActions = mockMvc.perform(
                            patch("/announcements/{announcement-id}", announcementId)
                                    .header(AUTHORIZATION, loginMemberAccessToken)
                                    .contentType(APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                                    .accept(APPLICATION_JSON)
                    );

                    resultActions
                            .andDo(print())
                            .andExpect(status().isNoContent());

                    assertThat(announcementRepository.findById(announcementId).get().getContent()).isEqualTo("new content");
                })
        );
    }

    @TestFactory
    @DisplayName("공지사항을 삭제한다.")
    Collection<DynamicTest> deleteAnnouncement() {

        return List.of(
                dynamicTest("공지사항이 존재하지 않으면 예외가 발생한다.", () -> {

                    ResultActions actions = mockMvc.perform(
                            delete("/announcements/{announcement-id}", 1000000L)
                                    .header(AUTHORIZATION, loginMemberAccessToken)
                                    .accept(APPLICATION_JSON)
                    );

                    actions
                            .andDo(print())
                            .andExpect(status().isNotFound())
                            .andExpect(jsonPath("message").value("존재하지않는 공지사항입니다."));
                }),

                dynamicTest("로그인한 유저가 아니면 예외가 발생한다.", () -> {

                    Long announcementId = announcementRepository.findById(loginMemberAnnouncement.getAnnouncementId()).get().getAnnouncementId();

                    ResultActions actions = mockMvc.perform(
                            delete("/announcements/{announcement-id}", announcementId)
                                    .header(AUTHORIZATION, "")
                                    .accept(APPLICATION_JSON)
                    );

                    actions
                            .andDo(print())
                            .andExpect(status().isForbidden())
                            .andExpect(jsonPath("message").value("접근 권한이 없습니다."));
                }),

                dynamicTest("공지사항을 삭제한다.", () -> {

                    Long announcementId = announcementRepository.findById(loginMemberAnnouncement.getAnnouncementId()).get().getAnnouncementId();

                        ResultActions actions = mockMvc.perform(
                                delete("/announcements/{announcement-id}", announcementId)
                                        .header(AUTHORIZATION, loginMemberAccessToken)
                                        .accept(APPLICATION_JSON)
                        );

                        actions
                                .andDo(print())
                                .andExpect(status().isNoContent());

                        assertThat(announcementRepository.findById(announcementId)).isEmpty();
                })
        );
    }
}
