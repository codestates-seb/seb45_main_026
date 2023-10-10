package com.server.domain.video.service;

import com.server.domain.cart.entity.Cart;
import com.server.domain.cart.repository.CartRepository;
import com.server.domain.category.entity.Category;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.domain.video.entity.VideoStatus;
import com.server.domain.video.service.dto.request.VideoCreateServiceRequest;
import com.server.domain.video.service.dto.request.VideoCreateUrlServiceRequest;
import com.server.domain.video.service.dto.request.VideoGetServiceRequest;
import com.server.domain.video.service.dto.request.VideoUpdateServiceRequest;
import com.server.domain.video.service.dto.response.VideoCreateUrlResponse;
import com.server.domain.video.service.dto.response.VideoDetailResponse;
import com.server.domain.video.service.dto.response.VideoPageResponse;
import com.server.domain.video.service.dto.response.VideoUrlResponse;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.videoexception.*;
import com.server.global.testhelper.ServiceTest;
import com.server.module.s3.service.dto.FileType;
import com.server.module.s3.service.dto.ImageType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

class VideoServiceTest extends ServiceTest {

    @Autowired VideoService videoService;
    @Autowired CartRepository cartRepository;

    @TestFactory
    @DisplayName("page, size, sort, category, memberId, subscribe 를 받아서 비디오 리스트를 반환한다.")
    Collection<DynamicTest> getVideos() {
        //given
        Member owner1 = createMemberWithChannel();

        Member owner2 = createMemberWithChannel();

        Member loginMember = createAndSaveMember(); // 로그인한 회원

        createAndSaveSubscribe(loginMember, owner1.getChannel()); // loginMember 가 owner1 의 channel1 을 구독

        Video video1 = createAndSaveVideo(owner1.getChannel());
        Video video2 = createAndSaveVideo(owner1.getChannel());
        Video video3 = createAndSaveVideo(owner1.getChannel());
        Video video4 = createAndSaveVideo(owner1.getChannel());
        Video video5 = createAndSaveVideo(owner2.getChannel());
        Video video6 = createAndSaveVideo(owner2.getChannel());

        Category category1 = createAndSaveCategory("java");
        Category category2 = createAndSaveCategory("spring");

        createAndSaveVideoCategory(video1, category1, category2); // video1 은 java, spring 카테고리
        createAndSaveVideoCategory(video2, category1); // video2 는 java 카테고리
        createAndSaveVideoCategory(video3, category2); // video3 는 spring 카테고리
        createAndSaveVideoCategory(video4, category1); // video4 는 java 카테고리
        createAndSaveVideoCategory(video5, category2); // video5 는 spring 카테고리
        createAndSaveVideoCategory(video6, category1); // video6 는 java 카테고리

        createAndSaveOrderWithPurchaseComplete(loginMember, List.of(video1, video5), 0); // otherMember 가 video1, video5 를 구매

        em.flush();
        em.clear();

        return List.of(
                dynamicTest("조건 없이 video 를 검색한다. 최신순으로 검색되며 5, 1 은 구매했다고 정보가 나오며 4, 3, 2, 1 채널은 구독했다고 나온다.", () -> {
                    //given
                    VideoGetServiceRequest request = new VideoGetServiceRequest(loginMember.getMemberId(), 0, 10, null, null, false, null, true);

                    //when
                    Page<VideoPageResponse> videos = videoService.getVideos(request);

                    //then
                    assertThat(videos.getContent()).hasSize(6);
                    assertThat(videos.getContent())
                            .isSortedAccordingTo(Comparator.comparing(VideoPageResponse::getCreatedDate).reversed());

                    //구매 여부
                    videos.getContent().forEach(
                            video -> {
                                if(List.of(video1.getVideoId(), video5.getVideoId()).contains(video.getVideoId())){
                                    assertThat(video.getIsPurchased()).isTrue();
                                }else{
                                    assertThat(video.getIsPurchased()).isFalse();
                                }
                            }
                    );

                    //비디오가 속한 채널의 구독 여부
                    videos.getContent().forEach(
                            video -> {
                                if(List.of(video1.getVideoId(), video2.getVideoId(), video3.getVideoId(), video4.getVideoId())
                                        .contains(video.getVideoId())){
                                    assertThat(video.getChannel().getIsSubscribed()).isTrue();
                                }else{
                                    assertThat(video.getChannel().getIsSubscribed()).isFalse();
                                }
                            }
                    );

                    //카테고리가 잘 있는지
                    videos.getContent().forEach(
                            video -> {
                                if(List.of(video2.getVideoId(), video4.getVideoId(), video6.getVideoId())
                                        .contains(video.getVideoId())){
                                    assertThat(video.getCategories().get(0).getCategoryName()).isEqualTo(category1.getCategoryName());
                                }else if(List.of(video3.getVideoId(), video5.getVideoId())
                                        .contains(video.getVideoId())){
                                    assertThat(video.getCategories().get(0).getCategoryName()).isEqualTo(category2.getCategoryName());
                                }else {
                                    assertThat(video.getCategories().get(0).getCategoryName()).isEqualTo(category1.getCategoryName());
                                    assertThat(video.getCategories().get(1).getCategoryName()).isEqualTo(category2.getCategoryName());
                                }
                            }
                    );
                })
        );
    }

    @Test
    @DisplayName("비디오 조회 시 장바구니에 담은 비디오는 isPurchased 가 true 로 나온다.")
    void getVideosInCart() {
        //given
        Member owner1 = createMemberWithChannel();

        Member loginMember = createAndSaveMember(); // 로그인한 회원

        Video video1 = createAndSaveVideo(owner1.getChannel());
        Video video2 = createAndSaveVideo(owner1.getChannel());

        createAndSaveCart(loginMember, video1);

        VideoGetServiceRequest request =
                new VideoGetServiceRequest(loginMember.getMemberId(), 0, 10, null, null, false, null, true);

        em.flush();
        em.clear();

        //when
        Page<VideoPageResponse> videos = videoService.getVideos(request);

        //then
        assertThat(findVideos(videos, video1).getIsInCart()).isTrue();
        assertThat(findVideos(videos, video2).getIsInCart()).isFalse();
    }

    private VideoPageResponse findVideos(Page<VideoPageResponse> videos, Video video1) {
        return videos.getContent().stream()
                .filter(video -> video.getVideoId().equals(video1.getVideoId()))
                .findFirst()
                .orElseThrow();
    }

    @TestFactory
    @DisplayName("videoId 로 비디오의 세부정보를 조회한다.")
    Collection<DynamicTest> getVideo() {
        //given
        Member owner = createMemberWithChannel();

        Member loginMember = createMemberWithChannel(); // 로그인한 회원
        Long anonymousMemberId = -1L; // 비회원

        createAndSaveSubscribe(loginMember, owner.getChannel()); // loginMember 가 owner1 의 channel1 을 구독

        Video video = createAndSaveVideo(owner.getChannel());

        Category category1 = createAndSaveCategory("java");
        Category category2 = createAndSaveCategory("spring");

        createAndSaveVideoCategory(video, category1); // video1 은 java, spring 카테고리
        createAndSaveVideoCategory(video, category2);

        em.flush();
        em.clear();

        setFileGetUrlSuccess();

        return List.of(
                dynamicTest("비회원이 비디오 세부정보를 조회한다. 구독여부, 댓글여부, 구매여부는 모두 false 이다.", () -> {
                    //when
                    VideoDetailResponse response = videoService.getVideo(anonymousMemberId, video.getVideoId());

                    //then
                    //비디오 정보
                    assertThat(response.getVideoId()).isEqualTo(video.getVideoId());
                    assertThat(response.getVideoName()).isEqualTo(video.getVideoName());
                    assertThat(response.getDescription()).isEqualTo(video.getDescription());
                    assertThat(response.getPreviewUrl()).isNotNull();
                    assertThat(response.getThumbnailUrl()).isNotNull();
                    assertThat(response.getStar()).isEqualTo(video.getStar());
                    assertThat(response.getPrice()).isEqualTo(video.getPrice());
                    assertThat(response.getReward()).isEqualTo(video.getPrice() / 100);
                    assertThat(response.getCreatedDate().toString().substring(0, 21)).isEqualTo(video.getCreatedDate().toString().substring(0, 21));
                    assertThat(response.getVideoStatus()).isEqualTo(VideoStatus.CREATED);

                    //카테고리 정보
                    assertThat(response.getCategories()).hasSize(2)
                            .extracting("categoryName")
                            .containsExactlyInAnyOrder(category1.getCategoryName(), category2.getCategoryName());

                    //채널 정보
                    assertThat(response.getChannel().getMemberId()).isEqualTo(owner.getMemberId());
                    assertThat(response.getChannel().getChannelName()).isEqualTo(owner.getChannel().getChannelName());
                    assertThat(response.getChannel().getImageUrl()).isNotNull();
                    assertThat(response.getChannel().getSubscribes()).isEqualTo(owner.getChannel().getSubscribers());

                    //로그인한 회원에 따라 달라지는 정보
                    assertThat(response.getChannel().getIsSubscribed()).isFalse();
                    assertThat(response.getIsPurchased()).isFalse();
                    assertThat(response.getIsReplied()).isFalse();
                }),
                dynamicTest("구독한 회원이 로그인을 하고 조회를 하면 구독 여부가 true 가 된다.", ()-> {
                    //when
                    VideoDetailResponse response = videoService.getVideo(loginMember.getMemberId(), video.getVideoId());

                    //then
                    assertThat(response.getChannel().getIsSubscribed()).isTrue();
                    assertThat(response.getIsPurchased()).isFalse();
                    assertThat(response.getIsReplied()).isFalse();
                }),
                dynamicTest("구독한 회원이 video 를 구매하면 구매여부가 true 가 된다.", ()-> {
                    //given
                    createAndSaveOrderWithPurchaseComplete(loginMember, List.of(video), 0);

                    em.flush();
                    em.clear();

                    //when
                    VideoDetailResponse response = videoService.getVideo(loginMember.getMemberId(), video.getVideoId());

                    //then
                    assertThat(response.getChannel().getIsSubscribed()).isTrue();
                    assertThat(response.getIsPurchased()).isTrue();
                    assertThat(response.getIsReplied()).isFalse();
                }),
                dynamicTest("구독한 회원이 video 에 댓글을 남기면 댓글 여부가 true 가 된다.", ()-> {
                    //given
                    createAndSaveReply(loginMember, video);

                    em.flush();
                    em.clear();

                    //when
                    VideoDetailResponse response = videoService.getVideo(loginMember.getMemberId(), video.getVideoId());

                    //then
                    assertThat(response.getChannel().getIsSubscribed()).isTrue();
                    assertThat(response.getIsPurchased()).isTrue();
                    assertThat(response.getIsReplied()).isTrue();
                }),
                dynamicTest("구독한 회원이 구독을 취소하면 구독 여부만 false 가 된다.", ()-> {
                    //given
                    subscribeRepository.deleteAll();

                    em.flush();
                    em.clear();

                    //when
                    VideoDetailResponse response = videoService.getVideo(loginMember.getMemberId(), video.getVideoId());

                    //then
                    assertThat(response.getChannel().getIsSubscribed()).isFalse();
                    assertThat(response.getIsPurchased()).isTrue();
                    assertThat(response.getIsReplied()).isTrue();
                })
        );
    }

    @Test
    @DisplayName("admin 이 video 를 조회 시 isPurchased 를 true 로 반환한다.")
    void getVideoAdmin() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());

        Member admin = createAdminWithChannel();

        //when
        VideoDetailResponse response = videoService.getVideo(admin.getMemberId(), video.getVideoId());

        //then
        assertThat(response.getIsPurchased()).isTrue();
    }

    @Test
    @DisplayName("admin 이 video 를 조회 시 close 된 video 도 조회할 수 있다.")
    void getVideoClosedAdmin() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        video.close();

        Member admin = createAdminWithChannel();

        //when & then
        assertThatNoException()
                .isThrownBy(() -> videoService.getVideo(admin.getMemberId(), video.getVideoId()));
    }

    @Test
    @DisplayName("videoId 로 videoUrl 을 조회한다.")
    void getVideoUrl() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());

        Member member = createMemberWithChannel();
        createAndSaveOrderWithPurchaseComplete(member, List.of(video), 0);

        String videoUrl = "https://s3.ap-northeast-2.amazonaws.com/test/test.mp4";

        given(awsService.getFileUrl(anyString(), any(FileType.class))).willReturn(videoUrl);

        //when
        VideoUrlResponse response = videoService.getVideoUrl(member.getMemberId(), video.getVideoId());

        //then
        assertThat(response.getVideoUrl()).isEqualTo(videoUrl);
    }

    @Test
    @DisplayName("구매하지 않은 사용자가 closed 된 비디오를 조회하면 VideoClosedException 이 발생한다.")
    void getVideoVideoClosedException() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        video.close();

        Member loginMember = createMemberWithChannel(); // 로그인한 회원

        Category category1 = createAndSaveCategory("java");
        Category category2 = createAndSaveCategory("spring");

        createAndSaveVideoCategory(video, category1, category2); // video1 은 java, spring 카테고리

        em.flush();
        em.clear();

        //when
        assertThatThrownBy(() -> videoService.getVideo(loginMember.getMemberId(), video.getVideoId()))
                .isInstanceOf(VideoClosedException.class)
                .hasMessage("강의가 폐쇄되었습니다. 강의명 : " + video.getVideoName());
    }

    @Test
    @DisplayName("video 를 구매한 사용자는 closed 된 비디오를 조회할 수 있다.")
    void getClosedVideoWhenPurchased() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        video.close();

        Member loginMember = createMemberWithChannel(); // 로그인한 회원

        createAndSaveOrderWithPurchaseComplete(loginMember, List.of(video), 0); // video 를 구매한 사용자

        Category category1 = createAndSaveCategory("java");
        Category category2 = createAndSaveCategory("spring");

        createAndSaveVideoCategory(video, category1, category2); // video1 은 java, spring 카테고리

        em.flush();
        em.clear();

        //when & then
        assertThatNoException()
                .isThrownBy(() -> videoService.getVideo(loginMember.getMemberId(), video.getVideoId()));
    }

    @TestFactory
    @DisplayName("비디오를 조회하면 조회 수를 1 증가시킨다.")
    Collection<DynamicTest> watch() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        int currentViews = video.getView();

        Member loginMember = createMemberWithChannel();

        return List.of(
                dynamicTest("익명의 사용자가 방문하면 조회 수는 증가지만 Watch 레코드를 만들어지지 않는다.", ()-> {
                    //when
                    videoService.watch(-1L, video.getVideoId());

                    //then
                    assertThat(video.getView()).isEqualTo(currentViews + 1);
                }),
                dynamicTest("로그인 사용자가 방문하면 조회 수가 증가하고 Watch 레코드가 생성된다.", ()-> {
                    //when
                    videoService.watch(loginMember.getMemberId(), video.getVideoId());

                    //then
                    assertThat(video.getView()).isEqualTo(currentViews + 2);
                    assertThat(watchRepository.count()).isEqualTo(1);

                }),
                dynamicTest("로그인 사용자가 재방문하면 watch 레코드는 생성되지만 조회 수는 증가하지 않는다.", ()-> {
                    //when
                    videoService.watch(loginMember.getMemberId(), video.getVideoId());

                    //then
                    assertThat(video.getView()).isEqualTo(currentViews + 3);
                    assertThat(watchRepository.count()).isEqualTo(1);
                })
        );
    }

    @TestFactory
    @DisplayName("비디오를 생성한다.")
    Collection<DynamicTest> createVideo() {
        //given
        Member owner = createMemberWithChannel();

        Category category1 = createAndSaveCategory("category1");
        Category category2 = createAndSaveCategory("category2");

        String videoName = "test";

        setFileGetUrlSuccess();
        setVideoUploadSuccess();

        return List.of(
                dynamicTest("imageType, fileName 을 받아서 파일을 저장할 수 있는 url 을 반환한다.", ()-> {
                    //given
                    VideoCreateUrlServiceRequest request = VideoCreateUrlServiceRequest.builder()
                            .imageType(ImageType.PNG)
                            .fileName(videoName)
                            .build();

                    //when
                    VideoCreateUrlResponse videoCreateUrl = videoService.getVideoCreateUrl(owner.getMemberId(), request);

                    //then
                    assertThat(videoCreateUrl.getVideoUrl()).matches("^https?://.+");
                    assertThat(videoCreateUrl.getThumbnailUrl()).matches("^https?://.+");

                    //video 가 생성되고 status 가 uploading 인지 확인
                    Video video = videoRepository.findVideoByNameWithMember(owner.getMemberId(), videoName).orElseThrow();
                    assertThat(video.getVideoStatus()).isEqualTo(VideoStatus.UPLOADING);
                }),
                dynamicTest("해당 fileName 으로 비디오를 생성한다.", ()-> {
                    //given
                    VideoCreateServiceRequest request = VideoCreateServiceRequest.builder()
                            .videoName(videoName)
                            .price(1000)
                            .description("test")
                            .categories(List.of(category1.getCategoryName(), category2.getCategoryName()))
                            .build();

                    //when
                    Long video = videoService.createVideo(owner.getMemberId(), request);

                    //then
                    Video createdVideo = videoRepository.findById(video).orElseThrow();

                    assertThat(createdVideo.getVideoName()).isEqualTo(videoName);
                    assertThat(createdVideo.getPrice()).isEqualTo(1000);
                    assertThat(createdVideo.getDescription()).isEqualTo("test");

                    //카테고리 확인
                    assertThat(createdVideo.getVideoCategories()).hasSize(2);
                    assertThat(createdVideo.getVideoCategories().get(0).getCategory().getCategoryName()).isEqualTo(category1.getCategoryName());
                    assertThat(createdVideo.getVideoCategories().get(1).getCategory().getCategoryName()).isEqualTo(category2.getCategoryName());

                    //video 가 생성되고 status 가 created 인지 확인
                    assertThat(createdVideo.getVideoStatus()).isEqualTo(VideoStatus.CREATED);
                })
        );
    }

    @Test
    @DisplayName("비디오 생성 시 0 원 비디오면 member 의 리워드가 100원 적립된다.")
    void createVideoMemberAddReward() {
        //given
        Member owner = createMemberWithChannel();
        Video video = Video.createVideo(owner.getChannel(), "test");
        videoRepository.save(video);

        createAndSaveCategory("category1");

        VideoCreateServiceRequest request = VideoCreateServiceRequest.builder()
                .videoName(video.getVideoName())
                .price(0)
                .description("test")
                .categories(List.of("category1"))
                .build();

        int beforeReward = owner.getReward();

        setFileGetUrlSuccess();
        setVideoUploadSuccess();

        //when
        videoService.createVideo(owner.getMemberId(), request);

        //then
        assertThat(owner.getReward()).isEqualTo(beforeReward + 100);
    }

    @TestFactory
    @DisplayName("비디오를 생성 시 최초 요청한 videoName 으로 요청하지 않으면 예외가 발생한다.")
    Collection<DynamicTest> createVideoException() {
        //given
        Member owner = createMemberWithChannel();

        Category category1 = createAndSaveCategory("category1");
        Category category2 = createAndSaveCategory("category2");

        String videoName = "test";

        setFileGetUrlSuccess();

        return List.of(
                dynamicTest("동일한 videoName 으로 요청하지 않으면 VideoNotFoundException 이 발생한다.", ()-> {
                    //given
                    VideoCreateServiceRequest request = VideoCreateServiceRequest.builder()
                            .videoName(videoName)
                            .price(1000)
                            .description("test")
                            .categories(List.of(category1.getCategoryName(), category2.getCategoryName()))
                            .build();

                    //when & then
                    assertThatThrownBy(()-> {
                        videoService.createVideo(owner.getMemberId(), request);
                    }).isInstanceOf(VideoNotFoundException.class);
                }),
                dynamicTest("imageType, fileName 을 받아서 파일을 저장할 수 있는 url 을 반환한다.", ()-> {
                    //given
                    VideoCreateUrlServiceRequest request = VideoCreateUrlServiceRequest.builder()
                            .imageType(ImageType.PNG)
                            .fileName(videoName)
                            .build();

                    //when
                    VideoCreateUrlResponse videoCreateUrl = videoService.getVideoCreateUrl(owner.getMemberId(), request);

                    //then
                    assertThat(videoCreateUrl.getVideoUrl()).matches("^https?://.+");
                    assertThat(videoCreateUrl.getThumbnailUrl()).matches("^https?://.+");
                }),
                dynamicTest("해당 fileName 이 아닌 다른 이름으로 비디오를 생성하려고 하면 VideoNotFoundException 이 발생한다.", ()-> {
                    //given
                    VideoCreateServiceRequest request = VideoCreateServiceRequest.builder()
                            .videoName(videoName + "1")
                            .price(1000)
                            .description("test")
                            .categories(List.of(category1.getCategoryName(), category2.getCategoryName()))
                            .build();

                    //when & then
                    assertThatThrownBy(
                            ()-> videoService.createVideo(owner.getMemberId(), request))
                            .isInstanceOf(VideoNotFoundException.class);
                })
        );
    }

    @Test
    @DisplayName("비디오 생성 시 이미 생성된 비디오(status=created) 로 요청하면 VideoAlreadyCreatedException 이 발생한다.")
    void createVideoVideoAlreadyCreatedException() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel()); // 이미 생성된 비디오

        createAndSaveCategory("category1");

        VideoCreateServiceRequest request = VideoCreateServiceRequest.builder()
                .videoName(video.getVideoName())
                .price(1000)
                .description("test")
                .categories(List.of("category1"))
                .build();

        //when & then
        assertThatThrownBy(() -> videoService.createVideo(owner.getMemberId(), request))
                .isInstanceOf(VideoAlreadyCreatedException.class);
    }

    @Test
    @DisplayName("비디오 생성 시 해당 멤버가 요청한 비디오 생성이 아니면 VideoNotFoundException 이 발생한다.")
    void createVideoMemberNotFoundException() {
        //given
        Member owner = createMemberWithChannel();
        Video video = Video.createVideo(owner.getChannel(), "test");
        videoRepository.save(video);

        createAndSaveCategory("category1");

        Member otherMember = createMemberWithChannel();

        VideoCreateServiceRequest request = VideoCreateServiceRequest.builder()
                .videoName(video.getVideoName())
                .price(1000)
                .description("test")
                .categories(List.of("category1"))
                .build();

        //when & then
        assertThatThrownBy(() -> videoService.createVideo(otherMember.getMemberId(), request))
                .isInstanceOf(VideoNotFoundException.class);
    }

    @Test
    @DisplayName("비디오 생성 시 비디오가 아직 업로드 되지 않았으면 VideoNotUploadedException 이 발생한다.")
    void createVideoVideoNotUploadedExceptionWithVideo() {
        //given
        Member owner = createMemberWithChannel();
        Video video = Video.createVideo(owner.getChannel(), "test");
        videoRepository.save(video);

        createAndSaveCategory("category1");

        VideoCreateServiceRequest request = VideoCreateServiceRequest.builder()
                .videoName(video.getVideoName())
                .price(1000)
                .description("test")
                .categories(List.of("category1"))
                .build();

        given(awsService.isExistFile(anyString(), eq(FileType.THUMBNAIL)))
                .willReturn(true);
        given(awsService.isExistFile(anyString(), eq(FileType.VIDEO)))
                .willReturn(false);

        //when & then
        assertThatThrownBy(() -> videoService.createVideo(owner.getMemberId(), request))
                .isInstanceOf(VideoNotUploadedException.class);
    }

    @Test
    @DisplayName("비디오 생성 시 썸네일이 아직 업로드 되지 않았으면 ThumbnailNotUploadedException 이 발생한다.")
    void createVideoVideoNotUploadedExceptionWithThumbnail() {
        //given
        Member owner = createMemberWithChannel();
        Video video = Video.createVideo(owner.getChannel(), "test");
        videoRepository.save(video);

        createAndSaveCategory("category1");

        VideoCreateServiceRequest request = VideoCreateServiceRequest.builder()
                .videoName(video.getVideoName())
                .price(1000)
                .description("test")
                .categories(List.of("category1"))
                .build();

        given(awsService.isExistFile(anyString(), eq(FileType.THUMBNAIL)))
                .willReturn(false);
        given(awsService.isExistFile(anyString(), eq(FileType.VIDEO)))
                .willReturn(true);

        //when & then
        assertThatThrownBy(() -> videoService.createVideo(owner.getMemberId(), request))
                .isInstanceOf(ThumbnailNotUploadedException.class);
    }

    @Test
    @DisplayName("비디오 put url 요청 시 존재하지 않는 memberId 면 MemberNotFoundException 이 발생한다.")
    void getVideoCreateUrlMemberNotFoundException() {
        //given
        Member owner = createAndSaveMember();

        VideoCreateUrlServiceRequest request = VideoCreateUrlServiceRequest.builder()
                .imageType(ImageType.PNG)
                .fileName("test")
                .build();

        Long requestMemberId = owner.getMemberId() + 999L; // 존재하지 않는 memberId

        //when & then
        assertThatThrownBy(() -> videoService.getVideoCreateUrl(requestMemberId, request))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("비디오 put url 요청 시 문자 \"/\" 가 포함되면 VideoNameNotValidException 이 발생한다.")
    void getVideoCreateUrlVideoNameNotValidException() {
        //given
        Member owner = createMemberWithChannel();

        VideoCreateUrlServiceRequest request = VideoCreateUrlServiceRequest.builder()
                .imageType(ImageType.PNG)
                .fileName("test/test")
                .build();

        //when & then
        assertThatThrownBy(() -> videoService.getVideoCreateUrl(owner.getMemberId(), request))
                .isInstanceOf(VideoNameNotValidException.class);
    }

    @Test
    @DisplayName("비디오 put url 을 받을 때 채널 내 비디오명이 중복되면 VideoNameDuplicateException 이 발생한다.")
    void getVideoCreateUrlVideoNameDuplicateException() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());

        VideoCreateUrlServiceRequest request = VideoCreateUrlServiceRequest.builder()
                .imageType(ImageType.PNG)
                .fileName(video.getVideoName())
                .build();

        //when & then
        assertThatThrownBy(() -> videoService.getVideoCreateUrl(owner.getMemberId(), request))
                .isInstanceOf(VideoNameDuplicateException.class);
    }

    @Test
    @DisplayName("비디오 Put url 을 받을 때 다른 채널의 비디오명과 중복되어도 정상적으로 받을 수 있다.")
    void getVideoCreateUrlVideoNameDuplicateWithOtherChannel() {
        //given
        Member owner = createMemberWithChannel();

        Member otherMember = createAndSaveMember();
        Video video = createAndSaveVideo(otherMember.getChannel());

        VideoCreateUrlServiceRequest request = VideoCreateUrlServiceRequest.builder()
                .imageType(ImageType.PNG)
                .fileName(video.getVideoName())
                .build();

        //when & then
        assertThatNoException()
                .isThrownBy(() -> videoService.getVideoCreateUrl(owner.getMemberId(), request));
    }
    
    @Test
    @DisplayName("description 를 받아서 비디오를 수정한다.")
    void updateVideo() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());

        VideoUpdateServiceRequest request = VideoUpdateServiceRequest.builder()
                .videoId(video.getVideoId())
                .description("update description")
                .build();

        setAuthentication(owner);

        em.flush();
        em.clear();

        //when
        videoService.updateVideo(owner.getMemberId(), request);

        //then
        Video updatedVideo = videoRepository.findById(video.getVideoId()).orElseThrow();

        assertThat(updatedVideo.getDescription()).isEqualTo("update description");
    }



    @Test
    @DisplayName("video 수정 시 수정 권한이 없는 memberId 면 VideoAccessDeniedException 이 발생한다.")
    void updateVideoVideoAccessDeniedException() {
        //given
        Member owner = createMemberWithChannel();
        Member notOwner = createMemberWithChannel();

        Video video = createAndSaveVideo(owner.getChannel());

        VideoUpdateServiceRequest request = VideoUpdateServiceRequest.builder()
                .videoId(video.getVideoId())
                .description("update description")
                .build();

        setAuthentication(notOwner);

        em.flush();
        em.clear();

        //when & then (없는 memberId 로 요청)
        assertThatThrownBy(() -> videoService.updateVideo(notOwner.getMemberId(), request))
                .isInstanceOf(VideoAccessDeniedException.class);

        //then (업데이트가 되지 않았는지 확인)
        Video updatedVideo = videoRepository.findById(video.getVideoId()).orElseThrow();

        assertThat(updatedVideo.getDescription()).isNotEqualTo("update description");
    }

    @Test
    @DisplayName("video 수정 시 존재하지 않는 videoId 면 VideoNotFoundException 이 발생한다.")
    void updateVideoVideoNotFoundException() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());

        VideoUpdateServiceRequest request = VideoUpdateServiceRequest.builder()
                .videoId(video.getVideoId() + 999L)
                .description("update description")
                .build();

        em.flush();
        em.clear();

        //when & then (없는 videoId 로 요청)
        assertThatThrownBy(() -> videoService.updateVideo(owner.getMemberId(), request))
                .isInstanceOf(VideoNotFoundException.class);

        //then (업데이트가 되지 않았는지 확인)
        Video updatedVideo = videoRepository.findById(video.getVideoId()).orElseThrow();

        assertThat(updatedVideo.getDescription()).isNotEqualTo("update description");
    }

    @TestFactory
    @DisplayName("장바구니에 video 를 추가/삭제한다.")
    Collection<DynamicTest> changeCart() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());

        Member loginMember = createMemberWithChannel();

        return List.of(
                dynamicTest("loginMember 의 장바구니에 video 를 추가한다.", ()-> {
                    //when
                    videoService.changeCart(loginMember.getMemberId(), video.getVideoId());

                    //then
                    assertThat(cartRepository.findByMemberAndVideo(loginMember, video)).isNotNull();
                }),
                dynamicTest("loginMember 의 장바구니에 video 를 삭제한다.", ()-> {
                    //when
                    videoService.changeCart(loginMember.getMemberId(), video.getVideoId());

                    //then
                    assertThat(cartRepository.findByMemberAndVideo(loginMember, video).isEmpty()).isTrue();
                })
        );
    }

    @Test
    @DisplayName("장바구니 추가 시 closed 된 video 면 VideoClosedException 이 발생한다.")
    void changeCartVideoClosedException() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());
        video.close();

        Member loginMember = createAndSaveMember();

        //when & then (없는 videoId 로 요청)
        assertThatThrownBy(() -> videoService.changeCart(loginMember.getMemberId(), video.getVideoId()))
                .isInstanceOf(VideoClosedException.class)
                .hasMessageContaining("강의가 폐쇄되었습니다. 강의명 : " + video.getVideoName());
    }

    @Test
    @DisplayName("장바구니 추가 시 없는 video 로 요청하면 VideoNotFoundException 이 발생한다.")
    void changeCartVideoNotFoundException() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());

        Member loginMember = createAndSaveMember();

        //when & then (없는 videoId 로 요청)
        assertThatThrownBy(() -> videoService.changeCart(loginMember.getMemberId(), video.getVideoId() + 999L))
                .isInstanceOf(VideoNotFoundException.class);
    }

    @Test
    @DisplayName("장바구니 추가 시 없는 memberId 로 요청하면 MemberNotFoundException 이 발생한다.")
    void changeCartMemberNotFoundException() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());

        Member loginMember = createAndSaveMember();

        //when & then (없는 memberId 로 요청)
        assertThatThrownBy(() -> videoService.changeCart(loginMember.getMemberId() + 999L, video.getVideoId()))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("장바구니에 추가된 video 를 videoIds 리스트를 통해 삭제할 수 있다.")
    void deleteCarts() {
        //given
        Member owner = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());
        Video video3 = createAndSaveVideo(owner.getChannel());

        Member loginMember = createMemberWithChannel();

        Cart cart1 = createAndSaveCart(loginMember, video1);
        Cart cart2 = createAndSaveCart(loginMember, video2);
        Cart cart3 = createAndSaveCart(loginMember, video3);

        //(1, 2번 비디오 삭제)
        List<Long> videoIds = List.of(video1.getVideoId(), video2.getVideoId());

        //when
        videoService.deleteCarts(loginMember.getMemberId(), videoIds);

        //then
        assertThat(cartRepository.findAll()).hasSize(1)
                .extracting("cartId").containsExactly(cart3.getCartId());
    }

    @Test
    @DisplayName("장바구니에 추가된 video 를 videoIds 리스트를 통해 삭제할 수 있다. (videoIds 에 없는 videoId 는 무시한다.)")
    void deleteCartsNotInCart() {
        //given
        Member owner = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());
        Video video3 = createAndSaveVideo(owner.getChannel());
        Video video4 = createAndSaveVideo(owner.getChannel());

        Member loginMember = createMemberWithChannel();

        Cart cart1 = createAndSaveCart(loginMember, video1);
        Cart cart2 = createAndSaveCart(loginMember, video2);
        Cart cart3 = createAndSaveCart(loginMember, video3);

        //(1, 2, 4번 비디오 삭제, 4번 비디오는 cart 에 없음)
        List<Long> videoIds = List.of(video1.getVideoId(), video2.getVideoId(), video4.getVideoId());

        //when
        videoService.deleteCarts(loginMember.getMemberId(), videoIds);

        //then
        assertThat(cartRepository.findAll()).hasSize(1)
                .extracting("cartId").containsExactly(cart3.getCartId());
    }

    @TestFactory
    @DisplayName("video 소유자는 video 를 상태변경할 수 있다. 상태변경하면 video status 가 변경된다.")
    Collection<DynamicTest> changeVideoStatus() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());

        return List.of(
            dynamicTest("비디오를 폐쇄한다.", ()-> {
                //given
                setAuthentication(owner);

                //when
                boolean result = videoService.changeVideoStatus(owner.getMemberId(), video.getVideoId());

                //then
                assertThat(video.getVideoStatus()).isEqualTo(VideoStatus.CLOSED);
                assertThat(result).isFalse();
            }),
            dynamicTest("비디오를 다시 연다.", ()-> {
                //given
                setAuthentication(owner);

                //when
                boolean result = videoService.changeVideoStatus(owner.getMemberId(), video.getVideoId());

                //then
                assertThat(video.getVideoStatus()).isEqualTo(VideoStatus.CREATED);
                assertThat(result).isTrue();
            })
        );
    }

    @TestFactory
    @DisplayName("admin 은 video 를 상태변경할 수 있다. 상태변경하면 video status 가 변경된다.")
    Collection<DynamicTest> changeVideoStatusAdmin() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());

        Member admin = createAdminWithChannel();

        return List.of(
                dynamicTest("owner 가 비디오를 폐쇄한다.", ()-> {
                    //given
                    setAuthentication(owner);

                    //when
                    boolean result = videoService.changeVideoStatus(owner.getMemberId(), video.getVideoId());

                    //then
                    assertThat(video.getVideoStatus()).isEqualTo(VideoStatus.CLOSED);
                    assertThat(result).isFalse();
                }),
                dynamicTest("admin 이 비디오를 다시 연다.", ()-> {
                    //given
                    setAuthentication(admin);

                    //when
                    boolean result = videoService.changeVideoStatus(admin.getMemberId(), video.getVideoId());

                    //then
                    assertThat(video.getVideoStatus()).isEqualTo(VideoStatus.CREATED);
                    assertThat(result).isTrue();
                }),
                dynamicTest("admin 이 비디오를 닫는다.", ()-> {
                    //given
                    setAuthentication(admin);

                    //when
                    boolean result = videoService.changeVideoStatus(admin.getMemberId(), video.getVideoId());

                    //then
                    assertThat(video.getVideoStatus()).isEqualTo(VideoStatus.ADMIN_CLOSED);
                    assertThat(result).isFalse();
                }),
                dynamicTest("admin 이 닫으면 owner 는 비디오를 열 수 없다.", ()-> {
                    //given
                    setAuthentication(owner);

                    //when & then
                    assertThatThrownBy(() -> videoService.changeVideoStatus(owner.getMemberId(), video.getVideoId()))
                            .isInstanceOf(VideoAdminClosedException.class);
                }),
                dynamicTest("admin 은 비디오를 다시 열 수 있다.", ()-> {
                    //given
                    setAuthentication(admin);

                    //when
                    boolean result = videoService.changeVideoStatus(admin.getMemberId(), video.getVideoId());

                    //then
                    assertThat(video.getVideoStatus()).isEqualTo(VideoStatus.CREATED);
                    assertThat(result).isTrue();
                })
        );
    }

    @Test
    @DisplayName("video 소유자가 아니면 video 상태변경 시 VideoAccessDeniedException 이 발생한다.")
    void changeVideoStatusVideoAccessDeniedException() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());

        Member loginMember = createAndSaveMember(); // video 소유자가 아닌 다른 멤버

        setAuthentication(loginMember);

        //when & then
        assertThatThrownBy(() -> videoService.changeVideoStatus(loginMember.getMemberId(), video.getVideoId()))
                .isInstanceOf(VideoAccessDeniedException.class);

        //then (삭제가 되지 않았는지 확인)
        assertThat(videoRepository.findById(video.getVideoId()).isEmpty()).isFalse();
    }

    @Test
    @DisplayName("video 상태변경 시 존재하지 않는 videoId 면 VideoNotFoundException 이 발생한다.")
    void changeVideoStatusVideoNotFoundException() {
        //given
        Member owner = createMemberWithChannel();
        Video video = createAndSaveVideo(owner.getChannel());

        //when & then
        assertThatThrownBy(() -> videoService.changeVideoStatus(owner.getMemberId(), video.getVideoId() + 999L))
                .isInstanceOf(VideoNotFoundException.class);

        //then (삭제가 되지 않았는지 확인)
        assertThat(videoRepository.findById(video.getVideoId()).isEmpty()).isFalse();

    }

    void setVideoUploadSuccess() {
        given(awsService.isExistFile(anyString(), any(FileType.class))).willReturn(true);
    }

    void setFileGetUrlSuccess() {
        given(awsService.getFileUrl(anyString(), any(FileType.class))).willReturn("https://test.com");
        given(awsService.getUploadVideoUrl(anyLong(), anyString())).willReturn("https://test.com");
        given(awsService.getPublicUploadUrl(anyLong(), anyString(), any(FileType.class), any(ImageType.class))).willReturn("https://test.com");
    }
}