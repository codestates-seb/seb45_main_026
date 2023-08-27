package com.server.domain.video.repository;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.RepositoryTest;
import org.assertj.core.api.Assertions;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class VideoRepositoryTest extends RepositoryTest {

    @Autowired VideoRepository videoRepository;

    @Test
    @DisplayName("video id 리스트를 통해 video 리스트를 조회한다.")
    void findAllByVideoIdIn() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);

        em.flush();
        em.clear();

        List<Long> videoIds = List.of(video1.getVideoId(), video2.getVideoId());

        //when
        List<Video> videos = videoRepository.findAllByVideoIdIn(videoIds);

        //then
        assertThat(videos).hasSize(2)
                .extracting("videoId").containsExactly(video1.getVideoId(), video2.getVideoId());
    }

    @Test
    @DisplayName("videoId 를 통해 member 를 함께 조회한다. channel 과 member 가 초기화된다.")
    void findVideoWithMember() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video = createAndSaveVideo(channel);

        em.flush();
        em.clear();

        //when
        Video findVideo = videoRepository.findVideoWithMember(video.getVideoId()).orElseThrow();

        //then
        assertThat(Hibernate.isInitialized(findVideo.getChannel())).isTrue();
        assertThat(Hibernate.isInitialized(findVideo.getChannel().getMember())).isTrue();

    }
}