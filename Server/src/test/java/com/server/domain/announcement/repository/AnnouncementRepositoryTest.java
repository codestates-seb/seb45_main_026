package com.server.domain.announcement.repository;

import com.server.domain.announcement.entity.Announcement;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.global.testhelper.RepositoryTest;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;


class AnnouncementRepositoryTest extends RepositoryTest {

    @Autowired AnnouncementRepository announcementRepository;

    @Test
    @DisplayName("memberId 를 통해 해당 멤버의 announcement 목록을 페이징해서 최신순으로 찾는다.")
    void findAnnouncementPageByMemberId() {

        //given
        Member member1 = createAndSaveMember();
        Channel channel1 = createAndSaveChannel(member1);
        Member member2 = createAndSaveMember();
        Channel channel2 = createAndSaveChannel(member2);

        setAnnouncements(channel1, channel2, 100);

        PageRequest pageRequest = PageRequest.of(0, 10);

        em.flush();
        em.clear();

        //when
        Page<Announcement> announcements = announcementRepository
                .findAnnouncementPageByMemberId(member1.getMemberId(), pageRequest);

        //then
        assertThat(announcements.getTotalElements()).isEqualTo(50);
        assertThat(announcements.getContent().size()).isEqualTo(10);
        assertThat(announcements.getNumber()).isEqualTo(0);

        //최신순인지 확인
        assertThat(announcements.getContent())
                .isSortedAccordingTo(Comparator.comparing(Announcement::getCreatedDate).reversed());

        //모두 member1 의 announcement 인지 확인
        assertThat(announcements.getContent())
                .allMatch(announcement -> announcement.getChannel().getMember().getMemberId().equals(member1.getMemberId()));
    }

    @Test
    @DisplayName("announcementId 를 통해 announcement 를 조회하면 member, channel 정보가 초기화되어있다.")
    void findAnnouncementWithMember() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Announcement announcement = createAndSaveAnnouncement(channel);

        em.flush();
        em.clear();

        //when
        Announcement findAnnouncement =
                announcementRepository.findAnnouncementWithMember(announcement.getAnnouncementId()).orElseThrow();

        //then
        //초기화 확인
        assertThat(Hibernate.isInitialized(findAnnouncement.getChannel())).isTrue();
        assertThat(Hibernate.isInitialized(findAnnouncement.getChannel().getMember())).isTrue();

        //값 확인
        assertThat(findAnnouncement.getAnnouncementId()).isEqualTo(announcement.getAnnouncementId());
        assertThat(findAnnouncement.getChannel().getChannelId()).isEqualTo(channel.getChannelId());
        assertThat(findAnnouncement.getChannel().getMember().getMemberId()).isEqualTo(member.getMemberId());
    }

    private void setAnnouncements(Channel channel1, Channel channel2, int count) {
        for (int i = 0; i < count; i++) { //member 1 의 announcement 50개, member 2 의 announcement 50개

            Channel channel;

            channel = i % 2 == 0 ? channel1 : channel2;

            createAndSaveAnnouncement(channel);
        }
    }

    private Announcement createAndSaveAnnouncement(Channel channel) {
        Announcement announcement = Announcement.builder()
                .channel(channel)
                .content("content")
                .build();

        em.persist(announcement);

        return announcement;
    }
}