package com.server.domain.video.service;

import com.server.domain.category.entity.Category;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.domain.video.service.dto.response.VideoPageResponse;
import com.server.global.testhelper.ServiceTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

class VideoServiceTest extends ServiceTest {

    @Autowired VideoService videoService;

    @TestFactory
    @DisplayName("page, size, sort, category, memberId, subscribe 를 받아서 비디오 리스트를 반환한다.")
    Collection<DynamicTest> getVideos() {
        //given
        Member owner1 = createAndSaveMember();
        Channel channel1 = createAndSaveChannel(owner1);

        Member owner2 = createAndSaveMember();
        Channel channel2 = createAndSaveChannel(owner2);

        Member loginMember = createAndSaveMember(); // 로그인한 회원

        createAndSaveSubscribe(loginMember, channel1); // loginMember 가 owner1 의 channel1 을 구독

        Video video1 = createAndSaveVideo(channel1);
        Video video2 = createAndSaveVideo(channel1);
        Video video3 = createAndSaveVideo(channel1);
        Video video4 = createAndSaveVideo(channel1);
        Video video5 = createAndSaveVideo(channel2);
        Video video6 = createAndSaveVideo(channel2);

        Category category1 = createAndSaveCategory("java");
        Category category2 = createAndSaveCategory("spring");

        createAndSaveVideoCategory(video1, category1); // video1 은 java, spring 카테고리
        createAndSaveVideoCategory(video1, category2);

        createAndSaveVideoCategory(video2, category1); // video2 는 java 카테고리
        createAndSaveVideoCategory(video3, category2); // video3 는 spring 카테고리
        createAndSaveVideoCategory(video4, category1); // video4 는 java 카테고리
        createAndSaveVideoCategory(video5, category2); // video5 는 spring 카테고리
        createAndSaveVideoCategory(video6, category1); // video6 는 java 카테고리

        createAndSaveOrderWithPurchase(loginMember, List.of(video1, video5), 0); // otherMember 가 video1, video5 를 구매

        em.flush();
        em.clear();

        return List.of(
                dynamicTest("조건 없이 video 를 검색한다. 6 ~ 1 순으로 검색되며 5, 1 은 구매했다고 정보가 나오며 4, 3, 2, 1 채널은 구독했다고 나온다.", () -> {
                    //when
                    Page<VideoPageResponse> videos = videoService.getVideos(loginMember.getMemberId(), 0, 10, null, null, false);

                    //then
                    assertThat(videos.getContent()).hasSize(6);
                    assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video6.getVideoId());
                    assertThat(videos.getContent().get(1).getVideoId()).isEqualTo(video5.getVideoId());
                    assertThat(videos.getContent().get(2).getVideoId()).isEqualTo(video4.getVideoId());
                    assertThat(videos.getContent().get(3).getVideoId()).isEqualTo(video3.getVideoId());
                    assertThat(videos.getContent().get(4).getVideoId()).isEqualTo(video2.getVideoId());
                    assertThat(videos.getContent().get(5).getVideoId()).isEqualTo(video1.getVideoId());

                    //구매 여부
                    assertThat(videos.getContent().get(0).getIsPurchased()).isFalse();
                    assertThat(videos.getContent().get(1).getIsPurchased()).isTrue();
                    assertThat(videos.getContent().get(2).getIsPurchased()).isFalse();
                    assertThat(videos.getContent().get(3).getIsPurchased()).isFalse();
                    assertThat(videos.getContent().get(4).getIsPurchased()).isFalse();
                    assertThat(videos.getContent().get(5).getIsPurchased()).isTrue();

                    //비디오가 속한 채널의 구독 여부
                    assertThat(videos.getContent().get(0).getChannel().getIsSubscribed()).isFalse();
                    assertThat(videos.getContent().get(1).getChannel().getIsSubscribed()).isFalse();
                    assertThat(videos.getContent().get(2).getChannel().getIsSubscribed()).isTrue();
                    assertThat(videos.getContent().get(3).getChannel().getIsSubscribed()).isTrue();
                    assertThat(videos.getContent().get(4).getChannel().getIsSubscribed()).isTrue();
                    assertThat(videos.getContent().get(5).getChannel().getIsSubscribed()).isTrue();

                    //카테고리가 잘 있는지
                    assertThat(videos.getContent().get(0).getCategories().get(0).getCategoryName()).isEqualTo(category1.getCategoryName());
                    assertThat(videos.getContent().get(1).getCategories().get(0).getCategoryName()).isEqualTo(category2.getCategoryName());
                    assertThat(videos.getContent().get(2).getCategories().get(0).getCategoryName()).isEqualTo(category1.getCategoryName());
                    assertThat(videos.getContent().get(3).getCategories().get(0).getCategoryName()).isEqualTo(category2.getCategoryName());
                    assertThat(videos.getContent().get(4).getCategories().get(0).getCategoryName()).isEqualTo(category1.getCategoryName());
                    assertThat(videos.getContent().get(5).getCategories()).hasSize(2);
                })
        );
    }
}