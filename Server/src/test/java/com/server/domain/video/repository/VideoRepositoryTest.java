package com.server.domain.video.repository;

import com.server.domain.category.entity.Category;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.RepositoryTest;
import org.assertj.core.api.Assertions;
import org.hibernate.Hibernate;
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
import static org.junit.jupiter.api.DynamicTest.*;

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

    @TestFactory
    @DisplayName("page, sort, category, subscribe(구독 여부) 로 video 를 조회한다.")
    Collection<DynamicTest> findByCategory() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Member otherMember = createAndSaveMember();
        Channel otherChannel = createAndSaveChannel(otherMember);

        createAndSaveSubscribe(otherMember, channel); // otherMember 가 member 의 channel 을 구독

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel, 1); // 조회수 1
        Video video3 = createAndSaveVideo(channel, 5.0F); // 별점 5
        Video video4 = createAndSaveVideo(channel);
        Video video5 = createAndSaveVideo(otherChannel);
        Video video6 = createAndSaveVideo(otherChannel);

        Category category1 = createAndSaveCategory("java");
        Category category2 = createAndSaveCategory("spring");

        createAndSaveVideoCategory(video1, category1); // video1 은 java 카테고리
        createAndSaveVideoCategory(video1, category2); // video1 은 spring 카테고리

        createAndSaveVideoCategory(video2, category1); // video2 는 java 카테고리
        createAndSaveVideoCategory(video3, category2); // video3 는 spring 카테고리
        createAndSaveVideoCategory(video4, category1); // video4 는 java 카테고리
        createAndSaveVideoCategory(video5, category2); // video5 는 spring 카테고리
        createAndSaveVideoCategory(video6, category1); // video6 는 java 카테고리


        PageRequest pageRequest = PageRequest.of(0, 10);

        em.flush();
        em.clear();

        return List.of(
            dynamicTest("category java 로 조회하면 video 6, 4, 2, 1 이 조회된다.", ()-> {
                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging("java", pageRequest, null, otherMember.getMemberId(), false);

                //then
                assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video6.getVideoId());
                assertThat(videos.getContent().get(1).getVideoId()).isEqualTo(video4.getVideoId());
                assertThat(videos.getContent().get(2).getVideoId()).isEqualTo(video2.getVideoId());
                assertThat(videos.getContent().get(3).getVideoId()).isEqualTo(video1.getVideoId());
                assertThat(videos.getContent().get(0).getVideoCategories().get(0).getCategory().getCategoryName()).isEqualTo("java");
                assertThat(videos.getContent().get(1).getVideoCategories().get(0).getCategory().getCategoryName()).isEqualTo("java");
                assertThat(videos.getContent().get(2).getVideoCategories().get(0).getCategory().getCategoryName()).isEqualTo("java");
                assertThat(videos.getContent().get(3).getVideoCategories()).hasSize(2);

            }),
            dynamicTest("category spring 로 조회하면 video 5, 3, 1 이 조회된다.", ()-> {
                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging("spring", pageRequest, null, otherMember.getMemberId(), false);

                //then
                assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video5.getVideoId());
                assertThat(videos.getContent().get(1).getVideoId()).isEqualTo(video3.getVideoId());
                assertThat(videos.getContent().get(2).getVideoId()).isEqualTo(video1.getVideoId());
                assertThat(videos.getContent().get(0).getVideoCategories().get(0).getCategory().getCategoryName()).isEqualTo("spring");
                assertThat(videos.getContent().get(1).getVideoCategories().get(0).getCategory().getCategoryName()).isEqualTo("spring");
                assertThat(videos.getContent().get(2).getVideoCategories()).hasSize(2);

            }),
            dynamicTest("별점 순으로 조회하면 video3 이 먼저 조회된 후 나머지는 최신순으로 조회된다.", ()-> {
                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging(null, pageRequest, "star", otherMember.getMemberId(), false);

                //then
                assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video3.getVideoId());
                assertThat(videos.getContent().get(1).getVideoId()).isEqualTo(video6.getVideoId());
                assertThat(videos.getContent().get(2).getVideoId()).isEqualTo(video5.getVideoId());
                assertThat(videos.getContent().get(3).getVideoId()).isEqualTo(video4.getVideoId());
                assertThat(videos.getContent().get(4).getVideoId()).isEqualTo(video2.getVideoId());
                assertThat(videos.getContent().get(5).getVideoId()).isEqualTo(video1.getVideoId());
            }),
            dynamicTest("조회 순으로 조회하면 video2 가 먼저 조회된 후 나머지는 최신순으로 조회된다.", ()-> {
                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging(null, pageRequest, "view", otherMember.getMemberId(), false);

                //then
                assertThat(videos.getContent()).hasSize(6);
                assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video2.getVideoId());
                assertThat(videos.getContent().get(1).getVideoId()).isEqualTo(video6.getVideoId());
                assertThat(videos.getContent().get(2).getVideoId()).isEqualTo(video5.getVideoId());
                assertThat(videos.getContent().get(3).getVideoId()).isEqualTo(video4.getVideoId());
                assertThat(videos.getContent().get(4).getVideoId()).isEqualTo(video3.getVideoId());
                assertThat(videos.getContent().get(5).getVideoId()).isEqualTo(video1.getVideoId());
            }),
            dynamicTest("최신순으로 조회하면 video6 이 먼저 조회된다.", ()-> {
                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging(null, pageRequest, null, otherMember.getMemberId(), false);

                //then
                assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video6.getVideoId());
                assertThat(videos.getContent().get(1).getVideoId()).isEqualTo(video5.getVideoId());
                assertThat(videos.getContent().get(2).getVideoId()).isEqualTo(video4.getVideoId());
                assertThat(videos.getContent().get(3).getVideoId()).isEqualTo(video3.getVideoId());
                assertThat(videos.getContent().get(4).getVideoId()).isEqualTo(video2.getVideoId());
                assertThat(videos.getContent().get(5).getVideoId()).isEqualTo(video1.getVideoId());
            }),
            dynamicTest("별점 순, spring 으로 조회하면 video3, video5, video1 순서로 조회된다.", ()-> {
                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging("spring", pageRequest, "star", otherMember.getMemberId(), false);

                //then
                assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video3.getVideoId());
                assertThat(videos.getContent().get(1).getVideoId()).isEqualTo(video5.getVideoId());
                assertThat(videos.getContent().get(2).getVideoId()).isEqualTo(video1.getVideoId());

                assertThat(videos.getContent().get(0).getVideoCategories().get(0).getCategory().getCategoryName()).isEqualTo("spring");
                assertThat(videos.getContent().get(1).getVideoCategories().get(0).getCategory().getCategoryName()).isEqualTo("spring");
                assertThat(videos.getContent().get(2).getVideoCategories()).hasSize(2);
            }),
            dynamicTest("조회 순, java 로 조회하면 video2, video4, video1 순서로 조회된다.", ()-> {
                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging("java", pageRequest, "view", otherMember.getMemberId(), false);

                //then
                assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video2.getVideoId());
                assertThat(videos.getContent().get(1).getVideoId()).isEqualTo(video6.getVideoId());
                assertThat(videos.getContent().get(2).getVideoId()).isEqualTo(video4.getVideoId());
                assertThat(videos.getContent().get(3).getVideoId()).isEqualTo(video1.getVideoId());

                assertThat(videos.getContent().get(0).getVideoCategories().get(0).getCategory().getCategoryName()).isEqualTo("java");
                assertThat(videos.getContent().get(1).getVideoCategories().get(0).getCategory().getCategoryName()).isEqualTo("java");
                assertThat(videos.getContent().get(2).getVideoCategories().get(0).getCategory().getCategoryName()).isEqualTo("java");
                assertThat(videos.getContent().get(3).getVideoCategories()).hasSize(2);
            }),
            dynamicTest("othermember 의 구독 목록만 조회하면 video 4, 3, 2, 1 이 조회된다.", ()-> {
                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging(null, pageRequest, null, otherMember.getMemberId(), true);

                //then
                assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video4.getVideoId());
                assertThat(videos.getContent().get(1).getVideoId()).isEqualTo(video3.getVideoId());
                assertThat(videos.getContent().get(2).getVideoId()).isEqualTo(video2.getVideoId());
                assertThat(videos.getContent().get(3).getVideoId()).isEqualTo(video1.getVideoId());
            }),
            dynamicTest("othermember 의 구독 목록 중 spring 카테고리로 조회하면 video 3, 1 이 조회된다.", ()-> {
                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging("spring", pageRequest, null, otherMember.getMemberId(), true);

                //then
                assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video3.getVideoId());
                assertThat(videos.getContent().get(1).getVideoId()).isEqualTo(video1.getVideoId());
            }),
            dynamicTest("othermember 의 구독 목록 중 java 카테고리로 조회하면 video 4, 2, 1 이 조회된다.", ()-> {
                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging("java", pageRequest, null, otherMember.getMemberId(), true);

                //then
                assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video4.getVideoId());
                assertThat(videos.getContent().get(1).getVideoId()).isEqualTo(video2.getVideoId());
                assertThat(videos.getContent().get(2).getVideoId()).isEqualTo(video1.getVideoId());
            }),
            dynamicTest("othermember 의 구독 목록 중 java 카테고리로 조회순으로 조회하면 video 2, 4, 1 이 조회된다.", ()-> {
                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging("java", pageRequest, "view", otherMember.getMemberId(), true);

                //then
                assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video2.getVideoId());
                assertThat(videos.getContent().get(1).getVideoId()).isEqualTo(video4.getVideoId());
                assertThat(videos.getContent().get(2).getVideoId()).isEqualTo(video1.getVideoId());
            })
        );
    }
}