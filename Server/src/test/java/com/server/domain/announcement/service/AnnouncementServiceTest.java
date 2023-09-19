package com.server.domain.announcement.service;

import com.server.domain.announcement.entity.Announcement;
import com.server.domain.announcement.service.dto.request.AnnouncementCreateServiceRequest;
import com.server.domain.announcement.service.dto.request.AnnouncementUpdateServiceRequest;
import com.server.domain.announcement.service.dto.response.AnnouncementResponse;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.global.exception.businessexception.announcementexception.AnnouncementNotFoundException;
import com.server.global.exception.businessexception.channelException.ChannelNotFoundException;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.testhelper.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class AnnouncementServiceTest extends ServiceTest {

    @Autowired AnnouncementService announcementService;

    @Test
    @DisplayName("memberId 를 통해 해당 멤버 채널의 announcement 목록을 페이징해서 최신순으로 찾는다.")
    void getAnnouncements() {
        //given
        Member member1 = createAndSaveMember();
        Channel channel1 = createAndSaveChannel(member1);
        List<Announcement> announcements1 = new ArrayList<>();

        Member member2 = createAndSaveMember();
        Channel channel2 = createAndSaveChannel(member2);
        List<Announcement> announcements2 = new ArrayList<>();

        setAnnouncements(channel1, announcements1, channel2, announcements2, 100);

        //when
        Page<AnnouncementResponse> announcements =
                announcementService.getAnnouncements(member1.getMemberId(), 0, 10);

        //then
        assertThat(announcements.getTotalElements()).isEqualTo(50);
        assertThat(announcements.getContent().size()).isEqualTo(10);
        assertThat(announcements.getNumber()).isEqualTo(0);

        //최신순인지 확인
        assertThat(announcements.getContent())
                .isSortedAccordingTo(Comparator.comparing(AnnouncementResponse::getCreatedDate).reversed());

        //모두 member1 의 announcement 인지 확인
        assertThat(announcements.getContent())
                .allMatch(announcementResponse -> announcements1.stream()
                        .anyMatch(announcement -> announcementResponse.getAnnouncementId().equals(announcement.getAnnouncementId())));
    }

    @Test
    @DisplayName("memberId 가 존재하지 않으면 ChannelNotFoundException 예외가 발생한다.")
    void getAnnouncementsChannelNotFoundException() {
        //given
        Member member1 = createAndSaveMember();
        Channel channel1 = createAndSaveChannel(member1);
        createAndSaveAnnouncement(channel1);

        Long wrongMemberId = member1.getMemberId() + 999L;

        //when & then
        assertThatThrownBy(() -> announcementService.getAnnouncements(wrongMemberId, 0, 10))
                .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("announcementId 를 통해 announcement 를 찾는다.")
    void getAnnouncement() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Announcement announcement = createAndSaveAnnouncement(channel);

        //when
        AnnouncementResponse announcementResponse = announcementService.getAnnouncement(announcement.getAnnouncementId());

        //then
        assertThat(announcementResponse.getAnnouncementId()).isEqualTo(announcement.getAnnouncementId());
        assertThat(announcementResponse.getContent()).isEqualTo(announcement.getContent());
    }

    @Test
    @DisplayName("announcement 를 찾을 때 해당 announcementId 가 없으면 AnnouncementNotFoundException 이 발생한다.")
    void getAnnouncementAnnouncementNotFoundException() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Announcement announcement = createAndSaveAnnouncement(channel);

        Long findAnnouncementId = announcement.getAnnouncementId() + 999L; // 존재하지 않는 announcementId

        //when & then
        assertThatThrownBy(() -> announcementService.getAnnouncement(findAnnouncementId))
                .isInstanceOf(AnnouncementNotFoundException.class);
    }

    @Test
    @DisplayName("content 를 받아서 해당 member 의 채널에 announcement 를 생성한다.")
    void createAnnouncement() {
        //given
        Member member = createMemberWithChannel();

        String content = "content";

        AnnouncementCreateServiceRequest request = AnnouncementCreateServiceRequest.builder()
                .memberId(member.getMemberId())
                .content(content)
                .build();

        //when
        Long announcementId = announcementService.createAnnouncement(member.getMemberId(), request);

        //then
        Announcement announcement = announcementRepository.findById(announcementId).orElseThrow();
        assertThat(announcement.getContent()).isEqualTo(content);
        assertThat(announcement.getChannel().getChannelId()).isEqualTo(member.getChannel().getChannelId());
    }

    @Test
    @DisplayName("Announcement 생성 시 loginMember 와 요청한 채널의 멤버의 id 가 다르면 MemberAccessDeniedException 이 발생한다.")
    void createAnnouncementMemberAccessDeniedException() {
        //given
        Member owner = createMemberWithChannel();

        Member loginMember = createMemberWithChannel();

        String content = "content";

        AnnouncementCreateServiceRequest request = AnnouncementCreateServiceRequest.builder()
                .memberId(owner.getMemberId())
                .content(content)
                .build();

        //when (loginMember 가 요청한 채널의 멤버가 아닌 경우)
        assertThatThrownBy(() -> announcementService.createAnnouncement(loginMember.getMemberId(), request))
                .isInstanceOf(MemberAccessDeniedException.class);
    }

    @Test
    @DisplayName("Announcement 생성 시 생성 요청한 채널의 멤버의 id 가 없으면 ChannelNotFoundException 이 발생한다.")
    void createAnnouncementMemberNotFoundException() {
        //given
        Member owner = createMemberWithChannel();

        String content = "content";

        Long notExistMemberId = owner.getMemberId() + 999L;

        AnnouncementCreateServiceRequest request = AnnouncementCreateServiceRequest.builder()
                .memberId(notExistMemberId)
                .content(content)
                .build();

        //when
        assertThatThrownBy(() -> announcementService.createAnnouncement(notExistMemberId, request))
                .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("content 를 받아서 해당 announcement 를 수정한다.")
    void updateAnnouncement() {
        //given
        Member member = createMemberWithChannel();
        Announcement announcement = createAndSaveAnnouncement(member.getChannel());

        String updateContent = "updateContent";

        AnnouncementUpdateServiceRequest request = AnnouncementUpdateServiceRequest.builder()
                .announcementId(announcement.getAnnouncementId())
                .content(updateContent)
                .build();

        //when
        announcementService.updateAnnouncement(member.getMemberId(), request);

        //then
        Announcement findAnnouncement = announcementRepository.findById(announcement.getAnnouncementId()).orElseThrow();
        assertThat(findAnnouncement.getContent()).isEqualTo(updateContent);
    }

    @Test
    @DisplayName("Announcement 수정 시 loginMember 와 요청한 채널의 멤버의 id 가 다르면 MemberAccessDeniedException 이 발생한다.")
    void updateAnnouncementMemberAccessDeniedException() {
        //given
        Member owner = createMemberWithChannel();
        Announcement announcement = createAndSaveAnnouncement(owner.getChannel());

        String updateContent = "updateContent";

        Member loginMember = createMemberWithChannel();

        AnnouncementUpdateServiceRequest request = AnnouncementUpdateServiceRequest.builder()
                .announcementId(announcement.getAnnouncementId())
                .content(updateContent)
                .build();

        //when (loginMember 가 요청한 채널의 멤버가 아닌 경우)
        assertThatThrownBy(() -> announcementService.updateAnnouncement(loginMember.getMemberId(), request))
                .isInstanceOf(MemberAccessDeniedException.class);

        //then (announcement 가 수정되지 않는다.)
        Announcement findAnnouncement = announcementRepository.findById(announcement.getAnnouncementId()).orElseThrow();
        assertThat(findAnnouncement.getContent()).isNotEqualTo(updateContent);
    }

    @Test
    @DisplayName("Announcement 수정 시 수정 요청한 announcement 가 없으면 AnnouncementNotFoundException 이 발생한다.")
    void updateAnnouncementAnnouncementNotFoundException() {
        //given
        Member owner = createMemberWithChannel();

        Announcement announcement = createAndSaveAnnouncement(owner.getChannel());

        String updateContent = "updateContent";

        Long notExistAnnouncementId = announcement.getAnnouncementId() + 999L; // 존재하지 않는 announcementId

        AnnouncementUpdateServiceRequest request = AnnouncementUpdateServiceRequest.builder()
                .announcementId(notExistAnnouncementId)
                .content(updateContent)
                .build();

        //when
        assertThatThrownBy(() -> announcementService.updateAnnouncement(owner.getMemberId(), request))
                .isInstanceOf(AnnouncementNotFoundException.class);

        //then (announcement 가 수정되지 않는다.)
        Announcement findAnnouncement = announcementRepository.findById(announcement.getAnnouncementId()).orElseThrow();
        assertThat(findAnnouncement.getContent()).isNotEqualTo(updateContent);

    }

    @Test
    @DisplayName("announcement 를 삭제한다.")
    void deleteAnnouncement() {
        //given
        Member owner = createMemberWithChannel();
        Announcement announcement = createAndSaveAnnouncement(owner.getChannel());

        //when
        announcementService.deleteAnnouncement(owner.getMemberId(), announcement.getAnnouncementId());

        //then
        assertThat(announcementRepository.findById(announcement.getAnnouncementId()).isEmpty())
                .isTrue();
    }

    @Test
    @DisplayName("Announcement 삭제 시 loginMember 와 announcement 의 채널 멤버가 다르면 MemberAccessDeniedException 이 발생한다.")
    void deleteAnnouncementMemberAccessDeniedException() {
        //given
        Member owner = createMemberWithChannel();
        Announcement announcement = createAndSaveAnnouncement(owner.getChannel());

        Long notExistMemberId = owner.getMemberId() + 999L; // 다른 멤버의 id

        //when & then
        assertThatThrownBy(() -> announcementService.deleteAnnouncement(notExistMemberId, announcement.getAnnouncementId()))
                .isInstanceOf(MemberAccessDeniedException.class);

        //then (announcement 가 삭제되지 않는다.)
        assertThat(announcementRepository.findById(announcement.getAnnouncementId()).isEmpty()).isFalse();
    }

    @Test
    @DisplayName("Announcement 삭제 시 announcement 가 존재하지 않으면 AnnouncementNotFoundException 이 발생한다.")
    void deleteAnnouncementAnnouncementNotFoundException() {
        //given
        Member owner = createMemberWithChannel();
        Announcement announcement = createAndSaveAnnouncement(owner.getChannel());

        Long notExistAnnouncementId = announcement.getAnnouncementId() + 999L; // 존재하지 않는 announcementId

        //when & then
        assertThatThrownBy(() -> announcementService.deleteAnnouncement(owner.getMemberId(), notExistAnnouncementId))
                .isInstanceOf(AnnouncementNotFoundException.class);
    }

    private void setAnnouncements(Channel channel1, List<Announcement> announcements1, Channel channel2, List<Announcement> announcements2, int count) {
        for (int i = 0; i < count; i++) { //member 1 의 announcement 50개, member 2 의 announcement 50개

            Channel channel;

            channel = i % 2 == 0 ? channel1 : channel2;

            Announcement announcement = createAndSaveAnnouncement(channel);

            if(i % 2 == 0) announcements1.add(announcement);
            else announcements2.add(announcement);
        }
    }

    private Announcement createAndSaveAnnouncement(Channel channel) {
        Announcement announcement = Announcement.builder()
                .content("content")
                .channel(channel)
                .build();

        return announcementRepository.save(announcement);
    }
}