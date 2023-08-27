package com.server.domain.video.repository;

import com.server.domain.video.entity.Video;
import com.server.global.testhelper.RepositoryTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

class VideoRepositoryTest extends RepositoryTest {

    @Autowired VideoRepository videoRepository;

    @Test
    @DisplayName("video id 리스트를 통해 video 리스트를 조회한다.")
    void findAllByVideoIdIn() {
        //given
        Video video1 = createAndSaveVideo();
        Video video2 = createAndSaveVideo();

        em.flush();
        em.clear();

        List<Long> videoIds = List.of(video1.getVideoId(), video2.getVideoId());

        //when
        List<Video> videos = videoRepository.findAllByVideoIdIn(videoIds);

        //then
        Assertions.assertThat(videos).hasSize(2)
                .extracting("videoId").containsExactly(video1.getVideoId(), video2.getVideoId());

    }
}