package com.server.domain.watch.repository;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.domain.watch.entity.Watch;
import com.server.global.testhelper.RepositoryTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class WatchRepositoryTest extends RepositoryTest {

    @Autowired WatchRepository watchRepository;

    @Test
    @DisplayName("member 와 video 로 watch 레코드를 조회한다.")
    void findByMemberAndVideo() {
        // given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video = createAndSaveVideo(channel);

        Member loginMember = createAndSaveMember();

        createAndSaveWatch(loginMember, video);

        em.flush();
        em.clear();

        // when
        Watch findWatch = watchRepository.findByMemberAndVideo(loginMember, video).orElseThrow();

        // then
        assertThat(findWatch.getMember().getMemberId()).isEqualTo(loginMember.getMemberId());
        assertThat(findWatch.getVideo().getVideoId()).isEqualTo(video.getVideoId());
    }

    private void createAndSaveWatch(Member loginMember, Video video) {
        Watch watch = Watch.createWatch(loginMember, video);
        em.persist(watch);
    }
}