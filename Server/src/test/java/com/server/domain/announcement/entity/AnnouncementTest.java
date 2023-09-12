package com.server.domain.announcement.entity;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AnnouncementTest {

    @Test
    @DisplayName("채널과 content 로 공지사항을 생성한다.")
    void createAnnouncement() {
        //given
        String content = "공지사항입니다.";
        Channel channel = Channel.builder()
                .channelId(1L)
                .build();

        //when
        Announcement announcement = Announcement.createAnnouncement(channel, content);

        //then
        assertThat(announcement.getContent()).isEqualTo(content);
        assertThat(announcement.getChannel()).isEqualTo(channel);
    }

    @Test
    @DisplayName("content 를 통해 announcement 를 수정한다.")
    void updateContent() {
        //given
        Announcement announcement = Announcement.builder()
                .content("content")
                .build();

        String updateContent = "update content";

        //when
        announcement.updateAnnouncement(updateContent);

        //then
        assertThat(announcement.getContent()).isEqualTo(updateContent);

    }

    @Test
    @DisplayName("content 에 null 이 들어오면 수정하지 않는다.")
    void NotUpdateContentWithNull() {
        //given
        Announcement announcement = Announcement.builder()
                .content("content")
                .build();

        //when
        announcement.updateAnnouncement(null);

        //then
        assertThat(announcement.getContent()).isNotNull();

    }
    
    @Test
    @DisplayName("memberId 의 Id 를 반환한다.")
    void getMemberId() {
        //given
        Member member = createMemberWithChannel();

        Announcement announcement = Announcement.builder()
                .content("content")
                .channel(member.getChannel())
                .build();
    
        //when
        Long findMemberId = announcement.getMemberId();

        //then
        assertThat(findMemberId).isEqualTo(member.getMemberId());
        
    }

    private Member createMemberWithChannel() {
        Member member = Member.builder()
                .memberId(1L)
                .build();

        Channel channel = Channel.builder()
                .channelId(1L)
                .build();

        member.setChannel(channel);

        return member;
    }
}