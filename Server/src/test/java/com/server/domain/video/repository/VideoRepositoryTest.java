package com.server.domain.video.repository;

import com.server.domain.category.entity.Category;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.reply.entity.Reply;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.dto.ChannelVideoGetDataRequest;
import com.server.domain.video.repository.dto.VideoGetDataRequest;
import com.server.domain.video.service.dto.response.VideoPageResponse;
import com.server.global.testhelper.RepositoryTest;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        Video video4 = createAndSaveFreeVideo(channel); // 무료 비디오
        Video video5 = createAndSaveVideo(otherChannel);
        Video video6 = createAndSaveVideo(otherChannel);
        Video video7 = createAndSaveVideo(otherChannel);
        video7.close(); //조회되지 않는 비디오

        createAndSaveOrderComplete(member, List.of(video4)); // member 가 video4 를 구매

        Category category1 = createAndSaveCategory("java");
        Category category2 = createAndSaveCategory("spring");

        createAndSaveVideoCategory(video1, category1, category2); // video1 은 java, spring 카테고리
        createAndSaveVideoCategory(video2, category1); // video2 는 java 카테고리
        createAndSaveVideoCategory(video3, category2); // video3 는 spring 카테고리
        createAndSaveVideoCategory(video4, category1); // video4 는 java 카테고리
        createAndSaveVideoCategory(video5, category2); // video5 는 spring 카테고리
        createAndSaveVideoCategory(video6, category1); // video6 는 java 카테고리


        PageRequest pageRequest = PageRequest.of(0, 10);

        em.flush();
        em.clear();

        return List.of(
            dynamicTest("category java 로 조회하면 java 카테고리를 가진 video 가 최신순으로 조회된다.", ()-> {
                //given
                VideoGetDataRequest request = new VideoGetDataRequest(
                        otherMember.getMemberId(),
                        pageRequest,
                        "java",
                        null,
                        false,
                        null,
                        true);

                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging(request);

                //then
                assertThat(videos.getContent())
                        .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());
                assertHasCategoryName(assertThat(videos.getContent()), "java");
            }),
            dynamicTest("category spring 로 조회하면 spring 카테고리를 가진 video 가 최신순으로 조회된다.", ()-> {
                //given
                VideoGetDataRequest request = new VideoGetDataRequest(
                        otherMember.getMemberId(),
                        pageRequest,
                        "spring",
                        null,
                        false,
                        null,
                        true);

                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging(request);

                //then
                assertThat(videos.getContent())
                        .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());
                assertHasCategoryName(assertThat(videos.getContent()), "spring");
            }),
            dynamicTest("별점 순으로 조회하면 video3 이 먼저 조회된 후 나머지는 최신순으로 조회된다.", ()-> {
                //given
                VideoGetDataRequest request = new VideoGetDataRequest(
                        otherMember.getMemberId(),
                        pageRequest,
                        null,
                        "star",
                        false,
                        null,
                        true);

                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging(request);

                //then
                assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video3.getVideoId());

                List<Video> sortedVideos = videos.getContent().subList(1, videos.getContent().size());
                assertThat(sortedVideos)
                        .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());
            }),
            dynamicTest("조회 순으로 조회하면 video2 가 먼저 조회된 후 나머지는 최신순으로 조회된다.", ()-> {
                //given
                VideoGetDataRequest request = new VideoGetDataRequest(
                        otherMember.getMemberId(),
                        pageRequest,
                        null,
                        "view",
                        false,
                        null,
                        true);
                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging(request);

                //then
                assertThat(videos.getContent()).hasSize(6);
                assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video2.getVideoId());

                List<Video> sortedVideos = videos.getContent().subList(1, videos.getContent().size());
                assertThat(sortedVideos)
                        .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());
            }),
            dynamicTest("최신순으로 조회하면 생성 최신순으로 조회된다.", ()-> {
                //given
                VideoGetDataRequest request = new VideoGetDataRequest(
                        otherMember.getMemberId(),
                        pageRequest,
                        null,
                        null,
                        false,
                        null,
                        true);

                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging(request);

                //then
                assertThat(videos.getContent())
                        .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());
            }),
            dynamicTest("별점 순, spring 으로 조회하면 video3 가 먼저 조회되고, video5, video1 순서로 조회된다.", ()-> {
                //given
                VideoGetDataRequest request = new VideoGetDataRequest(
                        otherMember.getMemberId(),
                        pageRequest,
                        "spring",
                        "star",
                        false,
                        null,
                        true);

                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging(request);

                //then
                assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video3.getVideoId());

                List<Video> sortedVideos = videos.getContent().subList(1, videos.getContent().size());
                assertThat(sortedVideos)
                        .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());

                assertHasCategoryName(assertThat(videos.getContent()), "spring");

            }),
            dynamicTest("조회 순, java 로 조회하면 video2 가 먼저 조회되고, video4, video1 순서로 조회된다.", ()-> {
                //given
                VideoGetDataRequest request = new VideoGetDataRequest(
                        otherMember.getMemberId(),
                        pageRequest,
                        "java",
                        "view",
                        false,
                        null,
                        true);
                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging(request);

                //then
                assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video2.getVideoId());

                List<Video> sortedVideos = videos.getContent().subList(1, videos.getContent().size());
                assertThat(sortedVideos)
                        .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());

                assertHasCategoryName(assertThat(videos.getContent()), "java");
            }),
            dynamicTest("otherMember 의 구독 목록만 조회하면 video 4, 3, 2, 1 이 조회된다.", ()-> {
                //given
                VideoGetDataRequest request = new VideoGetDataRequest(
                        otherMember.getMemberId(),
                        pageRequest,
                        null,
                        null,
                        true,
                        null,
                        true);

                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging(request);

                //then
                assertThat(videos.getContent())
                        .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());

                videos.getContent().forEach(video ->
                        assertThat(List.of(video1.getVideoId(), video2.getVideoId(), video3.getVideoId(), video4.getVideoId()))
                                .contains(video.getVideoId()));
            }),
            dynamicTest("otherMember 의 구독 목록 중 spring 카테고리로 조회하면 video 3, 1 이 조회된다.", ()-> {
                //given
                VideoGetDataRequest request = new VideoGetDataRequest(
                        otherMember.getMemberId(),
                        pageRequest,
                        "spring",
                        null,
                        true,
                        null,
                        true);

                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging(request);

                //then
                assertThat(videos.getContent())
                        .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());

                videos.getContent().forEach(video ->
                        assertThat(List.of(video1.getVideoId(), video3.getVideoId()))
                                .contains(video.getVideoId()));
            }),
            dynamicTest("otherMember 의 구독 목록 중 java 카테고리로 조회하면 video 4, 2, 1 이 조회된다.", ()-> {
                //given
                VideoGetDataRequest request = new VideoGetDataRequest(
                        otherMember.getMemberId(),
                        pageRequest,
                        "java",
                        null,
                        true,
                        null,
                        true);

                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging(request);

                //then
                assertThat(videos.getContent())
                        .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());

                videos.getContent().forEach(video ->
                        assertThat(List.of(video1.getVideoId(), video2.getVideoId(), video4.getVideoId()))
                                .contains(video.getVideoId()));
            }),
            dynamicTest("othermember 의 구독 목록 중 java 카테고리로 조회순으로 조회하면 video 2, 4, 1 이 조회된다.", ()-> {
                //given
                VideoGetDataRequest request = new VideoGetDataRequest(
                        otherMember.getMemberId(),
                        pageRequest,
                        "java",
                        "view",
                        true,
                        null,
                        true);

                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging(request);

                //then
                assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video2.getVideoId());

                List<Video> sortedVideos = videos.getContent().subList(1, videos.getContent().size());
                assertThat(sortedVideos)
                        .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());

                videos.getContent().forEach(video ->
                        assertThat(List.of(video1.getVideoId(), video2.getVideoId(), video4.getVideoId())).contains(video.getVideoId()));
            }),
            dynamicTest("무료인 video 만 조회하면 video 4 만 조회된다.", ()-> {
                //given
                VideoGetDataRequest request = new VideoGetDataRequest(
                        otherMember.getMemberId(),
                        pageRequest,
                        null,
                        null,
                        false,
                        true,
                        true);

                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging(request);

                //then
                videos.getContent().forEach(video ->
                        assertThat(List.of(video4.getVideoId())).contains(video.getVideoId()));
            }),
            dynamicTest("유료인 video 만 조회하면 video 4 를 제외하고 최신순으로 조회된다.", ()-> {
                //given
                VideoGetDataRequest request = new VideoGetDataRequest(
                        otherMember.getMemberId(),
                        pageRequest,
                        null,
                        null,
                        false,
                        false,
                        true);

                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging(request);

                //then
                assertThat(videos.getContent())
                        .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());
                videos.getContent().forEach(video ->
                        assertThat(List.of(video4.getVideoId())).doesNotContain(video.getVideoId()));
            }),
            dynamicTest("구매한 강의 여부를 false 로 주면 video 7 은 검색되지 않는다.", ()-> {
                //given
                VideoGetDataRequest request = new VideoGetDataRequest(
                        otherMember.getMemberId(),
                        pageRequest,
                        null,
                        null,
                        false,
                        false,
                        false);

                //when
                Page<Video> videos = videoRepository.findAllByCategoryPaging(request);

                //then
                assertThat(videos.getContent())
                        .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());
                videos.getContent().forEach(video ->
                        assertThat(List.of(video7.getVideoId())).doesNotContain(video.getVideoId()));
            })


        );
    }

    @TestFactory
    @DisplayName("video 전체를 찾을 때 page, size 가 주어지면 page 에 해당하는 video 목록을 size 만큼 조회한다.")
    Collection<DynamicTest> findAllByCategoryPaging() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Member member = createAndSaveMember();

        Category category1 = createAndSaveCategory("java");
        Category category2 = createAndSaveCategory("spring");

        for(int i = 1; i <= 100; i++) {
            Video video = createAndSaveVideo(channel);
            createAndSaveVideoCategory(video, category1, category2);
        }

        Video closedVideo = createAndSaveVideo(channel);
        closedVideo.close(); // 조회되지 않는 비디오

        em.flush();
        em.clear();

        //when
        return List.of(
                dynamicTest("page 0, size 10 으로 조회한다.", () -> {
                    //given
                    PageRequest pageRequest = PageRequest.of(0, 10);

                    VideoGetDataRequest request = new VideoGetDataRequest(
                            member.getMemberId(),
                            pageRequest,
                            null,
                            null,
                            false,
                            null,
                            true);

                    //when
                    Page<Video> videos = videoRepository.findAllByCategoryPaging(request);

                    //then
                    assertThat(videos.getContent().size()).isEqualTo(10);
                    assertThat(videos.getTotalElements()).isEqualTo(100);
                    assertThat(videos.getTotalPages()).isEqualTo(10);
                    assertThat(videos.getNumber()).isEqualTo(0);
                    assertThat(videos.getSize()).isEqualTo(10);
                    assertThat(videos.hasNext()).isTrue();
                    assertThat(videos.hasPrevious()).isFalse();
                }),
                dynamicTest("page 1, size 12 으로 조회한다.", () -> {
                    //given
                    PageRequest pageRequest = PageRequest.of(1, 12);

                    VideoGetDataRequest request = new VideoGetDataRequest(
                            member.getMemberId(),
                            pageRequest,
                            null,
                            null,
                            false,
                            null,
                            true);

                    //when
                    Page<Video> videos = videoRepository.findAllByCategoryPaging(request);

                    //then
                    assertThat(videos.getContent().size()).isEqualTo(12);
                    assertThat(videos.getTotalElements()).isEqualTo(100);
                    assertThat(videos.getTotalPages()).isEqualTo(9);
                    assertThat(videos.getNumber()).isEqualTo(1);
                    assertThat(videos.getSize()).isEqualTo(12);
                    assertThat(videos.hasNext()).isTrue();
                    assertThat(videos.hasPrevious()).isTrue();
                })
        );
    }

    @Test
    @DisplayName("videoId 로 video 정보를 조회한다.")
    void findVideoDetail() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Member loginMember = createAndSaveMember();

        createAndSaveSubscribe(loginMember, channel); // loginMember 가 owner 의 channel 을 구독

        Video video = createAndSaveVideo(channel);

        Category category1 = createAndSaveCategory("java");
        Category category2 = createAndSaveCategory("spring");

        createAndSaveVideoCategory(video, category1); // video1 은 java 카테고리
        createAndSaveVideoCategory(video, category2); // video1 은 spring 카테고리

        em.flush();
        em.clear();

        //when
        Video findVideo = videoRepository.findVideoDetail(video.getVideoId()).orElseThrow();

        //then
        //category, channel, owner 초기화 확인
        assertThat(Hibernate.isInitialized(findVideo.getVideoCategories())).isTrue();
        assertThat(Hibernate.isInitialized(findVideo.getVideoCategories().get(0))).isTrue();
        assertThat(Hibernate.isInitialized(findVideo.getVideoCategories().get(1))).isTrue();
        assertThat(Hibernate.isInitialized(findVideo.getChannel())).isTrue();
        assertThat(Hibernate.isInitialized(findVideo.getChannel().getMember())).isTrue();

        assertThat(findVideo.getVideoId()).isEqualTo(video.getVideoId());
        assertThat(findVideo.getVideoCategories()).hasSize(2);
        assertThat(findVideo.getVideoCategories().get(0).getCategory().getCategoryName()).isEqualTo("java");
        assertThat(findVideo.getVideoCategories().get(1).getCategory().getCategoryName()).isEqualTo("spring");
        assertThat(findVideo.getChannel().getChannelName()).isEqualTo(channel.getChannelName());
        assertThat(findVideo.getChannel().getMember().getMemberId()).isEqualTo(owner.getMemberId());
    }

    @TestFactory
    @DisplayName("memberId 와 videoId 로 해당 멤버가 video 를 구매했는지, 댓글을 달았는지 조회한다.")
    Collection<DynamicTest> isPurchasedAndIsReplied() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video = createAndSaveVideo(channel);

        Member loginMember = createAndSaveMember();

        em.flush();
        em.clear();

        return List.of(
                dynamicTest("구매하지 않았으면 모두 false 가 반환된다.", ()-> {
                    //when
                    List<Boolean> purchasedAndIsReplied = videoRepository.isPurchasedAndIsReplied(loginMember.getMemberId(), video.getVideoId());

                    //then
                    assertThat(purchasedAndIsReplied.get(0)).isFalse();
                    assertThat(purchasedAndIsReplied.get(1)).isFalse();
                }),
                dynamicTest("구매만 하고 댓글을 달지 않으면 첫번째 값은 true, 두번째 값은 false 가 된다.", ()-> {
                    //given
                    createAndSaveOrderComplete(loginMember, List.of(video));

                    em.flush();
                    em.clear();

                    //when
                    List<Boolean> purchasedAndIsReplied = videoRepository.isPurchasedAndIsReplied(loginMember.getMemberId(), video.getVideoId());

                    //then
                    assertThat(purchasedAndIsReplied.get(0)).isTrue();
                    assertThat(purchasedAndIsReplied.get(1)).isFalse();
                }),
                dynamicTest("댓글을 달면 두 값 모두 true 가 된다.", ()-> {
                    //given
                    createAndSaveReply(loginMember, video);

                    em.flush();
                    em.clear();

                    //when
                    List<Boolean> purchasedAndIsReplied = videoRepository.isPurchasedAndIsReplied(loginMember.getMemberId(), video.getVideoId());

                    //then
                    assertThat(purchasedAndIsReplied.get(0)).isTrue();
                    assertThat(purchasedAndIsReplied.get(1)).isTrue();
                })
        );
    }

    @TestFactory
    @DisplayName("memberId 로 해당 채널의 video 목록을 조회한다.")
    Collection<DynamicTest> findChannelVideoByCategoryPaging() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Member otherMember = createAndSaveMember();
        Channel otherChannel = createAndSaveChannel(otherMember);

        Category category1 = createAndSaveCategory("java");
        Category category2 = createAndSaveCategory("spring");

        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel, 1); // 조회수 1
        Video video3 = createAndSaveVideo(channel, 5.0F); // 별점 5
        Video video4 = createAndSaveFreeVideo(channel);
        Video video5 = createAndSaveVideo(channel);
        Video video6 = createAndSaveVideo(channel);
        createAndSaveOrderComplete(member, List.of(video4, video5, video6)); // member 가 video4, video5, video6 구매

        for(int i = 1; i <= 100; i++) {
            Video otherVideo = createAndSaveVideo(otherChannel);// otherMember 의 video
            createAndSaveVideoCategory(otherVideo, category1, category2);
        }
        createAndSaveVideoCategory(video1, category1, category2); // video1 은 java, spring 카테고리
        createAndSaveVideoCategory(video2, category1); // video2 는 java 카테고리
        createAndSaveVideoCategory(video3, category2); // video3 는 spring 카테고리
        createAndSaveVideoCategory(video4, category1); // video4 는 java 카테고리
        createAndSaveVideoCategory(video5, category2); // video5 는 spring 카테고리
        createAndSaveVideoCategory(video6, category1); // video6 는 java 카테고리

        em.flush();
        em.clear();

        PageRequest pageRequest = PageRequest.of(0, 10);

        return List.of(
                dynamicTest("채널의 비디오를 페이징으로 최신순으로 조회한다.", () -> {
                    //given
                    ChannelVideoGetDataRequest request = new ChannelVideoGetDataRequest(
                            member.getMemberId(),
                            null,
                            pageRequest,
                            null,
                            null,
                            true);

                    //when
                    Page<Video> videos = videoRepository.findChannelVideoByCategoryPaging(request);

                    //then
                    assertThat(videos.getContent()).hasSize(6)
                            .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());
                    assertThat(videos.getContent())
                            .allSatisfy(video -> assertThat(video.getChannel())
                                    .extracting("member")
                                    .extracting("memberId")
                                    .isEqualTo(member.getMemberId()));
                }),
                dynamicTest("채널의 비디오를 페이징으로 조회수순으로 조회한다. video 2 가 먼저 조회된 후 최신순으로 조회된다.", () -> {
                    //given
                    ChannelVideoGetDataRequest request = new ChannelVideoGetDataRequest(
                            member.getMemberId(),
                            null,
                            pageRequest,
                            "view",
                            null,
                            true);

                    //when
                    Page<Video> videos = videoRepository.findChannelVideoByCategoryPaging(request);

                    //then
                    assertThat(videos.getContent()).hasSize(6);

                    assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video2.getVideoId());

                    List<Video> sortedVideos = videos.getContent().subList(1, videos.getContent().size());
                    assertThat(sortedVideos)
                            .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());
                }),
                dynamicTest("채널의 비디오를 페이징으로 별점순으로 조회한다. video 3 이 먼저 조회된 후 최신순으로 조회된다.", () -> {
                    //given
                    ChannelVideoGetDataRequest request = new ChannelVideoGetDataRequest(
                            member.getMemberId(),
                            null,
                            pageRequest,
                            "star",
                            null,
                            true);

                    //when
                    Page<Video> videos = videoRepository.findChannelVideoByCategoryPaging(request);

                    //then
                    assertThat(videos.getContent()).hasSize(6);

                    assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video3.getVideoId());

                    List<Video> sortedVideos = videos.getContent().subList(1, videos.getContent().size());
                    assertThat(sortedVideos)
                            .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());
                }),
                dynamicTest("채널의 비디오를 페이징으로 java 카테고리를 조회한다. video 1, 2, 4, 6 이 조회된다.", () -> {
                    //given
                    ChannelVideoGetDataRequest request = new ChannelVideoGetDataRequest(
                            member.getMemberId(),
                            "java",
                            pageRequest,
                            "star",
                            null,
                            true);

                    //when
                    Page<Video> videos = videoRepository.findChannelVideoByCategoryPaging(request);

                    //then
                    assertHasCategoryName(assertThat(videos.getContent()).hasSize(4), "java");
                }),
                dynamicTest("채널의 비디오를 페이징으로 spring 카테고리를 조회한다. video 1, 3, 5 가 조회된다.", () -> {
                    //given
                    ChannelVideoGetDataRequest request = new ChannelVideoGetDataRequest(
                            member.getMemberId(),
                            "spring",
                            pageRequest,
                            null,
                            null,
                            true);

                    //when
                    Page<Video> videos = videoRepository.findChannelVideoByCategoryPaging(request);

                    //then
                    assertHasCategoryName(assertThat(videos.getContent()).hasSize(3), "spring");
                }),
                dynamicTest("채널의 비디오를 페이징으로 조회순, java 카테고리를 조회한다. video 2 가 먼저 조회되고, 1, 4, 6 이 조회된다.", () -> {
                    //given
                    ChannelVideoGetDataRequest request = new ChannelVideoGetDataRequest(
                            member.getMemberId(),
                            "java",
                            pageRequest,
                            "view",
                            null,
                            true);

                    //when
                    Page<Video> videos = videoRepository.findChannelVideoByCategoryPaging(request);

                    //then
                    assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video2.getVideoId());

                    List<Video> sortedVideos = videos.getContent().subList(1, videos.getContent().size());
                    assertThat(sortedVideos)
                            .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());

                    assertHasCategoryName(assertThat(videos.getContent()).hasSize(4), "java");
                }),
                dynamicTest("채널의 비디오를 페이징으로 별점순, spring 카테고리를 조회한다. video 3 이 먼저 조회되고, 1, 5 가 조회된다.", () -> {
                    //given
                    ChannelVideoGetDataRequest request = new ChannelVideoGetDataRequest(
                            member.getMemberId(),
                            "spring",
                            pageRequest,
                            "star",
                            null,
                            true);

                    //when
                    Page<Video> videos = videoRepository.findChannelVideoByCategoryPaging(request);

                    //then
                    assertThat(videos.getContent().get(0).getVideoId()).isEqualTo(video3.getVideoId());

                    List<Video> sortedVideos = videos.getContent().subList(1, videos.getContent().size());
                    assertThat(sortedVideos)
                            .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());

                    assertHasCategoryName(assertThat(videos.getContent()).hasSize(3), "spring");
                }),
                dynamicTest("other 채널의 비디오를 페이징으로 조회한다. 총 개수가 100개로 나오고, 최신순으로 조회된다.", () -> {
                    //given
                    ChannelVideoGetDataRequest request = new ChannelVideoGetDataRequest(
                            otherMember.getMemberId(),
                            null,
                            pageRequest,
                            "star",
                            null,
                            true);

                    //when
                    Page<Video> videos = videoRepository.findChannelVideoByCategoryPaging(request);

                    //then
                    assertThat(videos.getContent()).hasSize(10)
                            .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());

                    assertThat(videos.getTotalElements()).isEqualTo(100);
                }),
                dynamicTest("채널의 비디오를 중 무료인 동영상을 조회하면 video 4 만 조회된다.", () -> {
                    //given
                    ChannelVideoGetDataRequest request = new ChannelVideoGetDataRequest(
                            member.getMemberId(),
                            null,
                            pageRequest,
                            null,
                            true,
                            true);

                    //when
                    Page<Video> videos = videoRepository.findChannelVideoByCategoryPaging(request);

                    //then
                    videos.getContent().forEach(video ->
                            assertThat(List.of(video4.getVideoId())).contains(video.getVideoId()));
                }),
                dynamicTest("채널의 비디오를 중 유료인 동영상을 조회하면 video 4 를 제외하고 조회된다.", () -> {
                    //given
                    ChannelVideoGetDataRequest request = new ChannelVideoGetDataRequest(
                            member.getMemberId(),
                            null,
                            pageRequest,
                            null,
                            false,
                            true);

                    //when
                    Page<Video> videos = videoRepository.findChannelVideoByCategoryPaging(request);

                    //then
                    assertThat(videos.getContent())
                            .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());
                    videos.getContent().forEach(video ->
                            assertThat(List.of(video4.getVideoId())).doesNotContain(video.getVideoId()));
                }),
                dynamicTest("채널의 비디오 중 구매안한 video 인 1, 2, 3 만 조회한다.", ()-> {
                    //given
                    ChannelVideoGetDataRequest request = new ChannelVideoGetDataRequest(
                            member.getMemberId(),
                            null,
                            pageRequest,
                            null,
                            null,
                            false);

                    //when
                    Page<Video> videos = videoRepository.findChannelVideoByCategoryPaging(request);

                    //then
                    assertThat(videos.getContent())
                            .isSortedAccordingTo(Comparator.comparing(Video::getCreatedDate).reversed());
                    videos.getContent().forEach(video ->
                            assertThat(List.of(video4.getVideoId(), video5.getVideoId(), video6.getVideoId()))
                                    .doesNotContain(video.getVideoId()));
                })
        );
    }

    @Test
    @DisplayName("memberId, videoName 을 통해 비디오를 찾는다. ")
    void findVideoByNameWithMember() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        //when
        Video findVideo = videoRepository.findVideoByNameWithMember(member.getMemberId(), video.getVideoName()).orElseThrow();

        //then
        assertThat(findVideo.getVideoId()).isEqualTo(video.getVideoId());
    }

    private void createAndSaveReply(Member member, Video video) {
        Reply reply = new Reply();
        reply.setMember(member);
        reply.setVideo(video);
        reply.setContent("reply");
        reply.setStar(5);

        em.persist(reply);
    }

    private void assertHasCategoryName(ListAssert<Video> videos, String categoryNames) {
        videos
                .allSatisfy(video -> {
                    List<String> videoCategoryNames = video.getVideoCategories()
                            .stream()
                            .map(videoCategory -> videoCategory.getCategory().getCategoryName())
                            .collect(Collectors.toList());

                    boolean containsAtLeastOneCategory = videoCategoryNames.contains(categoryNames);

                    assertThat(containsAtLeastOneCategory).isTrue();
                });
    }
}