package com.server.domain.watch.entity;

import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class WatchTest {

    @Test
    @DisplayName("member 와 video 로 watch 를 생성한다.")
    void createWatch() {
        //given
        Member member = createMember();
        Video video = createVideo();

        //when
        Watch watch = Watch.createWatch(member, video);

        //then
        assertThat(watch.getMember()).isEqualTo(member);
        assertThat(watch.getVideo()).isEqualTo(video);
    }

    @Test
    @DisplayName("watch 의 modifiedDate 를 직접 변경한다.")
    void setLastWatchedTime() {
        //given
        Member member = createMember();
        Video video = createVideo();
        Watch watch = createWatch(member, video);

        LocalDateTime lastWatchedTime = LocalDateTime.now();

        //when
        watch.setLastWatchedTime(lastWatchedTime);

        //then
        assertThat(watch.getModifiedDate()).isEqualTo(lastWatchedTime);
    }

    private Member createMember() {
        return Member.builder()
                .email("test@gmail.com")
                .password("1q2w3e4r!")
                .nickname("test")
                .authority(Authority.ROLE_USER)
                .reward(1000)
                .imageFile("imageFile")
                .build();
    }

    private Video createVideo() {
        return Video.builder()
                .videoName("title")
                .description("description")
                .thumbnailFile("thumbnailFile")
                .videoFile("videoFile")
                .view(0)
                .star(0.0F)
                .price(1000)
                .build();
    }

    private Watch createWatch(Member member, Video video) {
        return Watch.builder()
                .member(member)
                .video(video)
                .build();
    }
}