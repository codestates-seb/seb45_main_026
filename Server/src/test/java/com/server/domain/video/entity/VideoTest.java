package com.server.domain.video.entity;

import com.server.domain.category.entity.Category;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.reply.entity.Reply;
import com.server.domain.videoCategory.entity.VideoCategory;
import com.server.global.exception.businessexception.videoexception.VideoAlreadyCreatedException;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

class VideoTest {

    @Test
    @DisplayName("videoName, price, description, 카테고리 목록으로 Video 를 생성한다.")
    void createVideo() {
        //given
        Channel channel = Channel.builder()
                .channelId(1L)
                .channelName("channelName")
                .build();
        String videoName = "videoName";

        //when
        Video video = Video.createVideo(channel, videoName);

        //then
        assertThat(video.getChannel()).isEqualTo(channel);
        assertThat(video.getVideoName()).isEqualTo(videoName);
        assertThat(video.getPrice()).isEqualTo(0);
        assertThat(video.getDescription()).isEqualTo("uploading");
        assertThat(video.getVideoStatus()).isEqualTo(VideoStatus.UPLOADING);
    }

    @Test
    @DisplayName("video 의 view 를 1 증가시킨다.")
    void addView() {
        //given
        Video video = createVideo("video");
        int initView = video.getView();

        //when
        video.addView();

        //then
        assertThat(video.getView()).isEqualTo(initView + 1);
    }

    @Test
    @DisplayName("video 의 star 의 평균을 소수점 첫째자리까지 계산한 뒤 star 에 저장한다.")
    void calculateStar() {
        //given
        Reply reply1 = createReply(1);
        Reply reply2 = createReply(2);
        Reply reply3 = createReply(3);
        Reply reply4 = createReply(4);

        float expected = 2.5f;

        Video video = createVideo(List.of(reply1, reply2, reply3, reply4));

        //when
        video.calculateStar();

        //then
        assertThat(video.getStar()).isEqualTo(expected);
    }

    @TestFactory
    @DisplayName("videoCategories 를 업데이트 한다.")
    Collection<DynamicTest> updateCategory() {
        //given
        Video video = createVideo("video");

        Category category1 = createCategory("category1");
        Category category2 = createCategory("category2");
        Category category3 = createCategory("category3");

        return List.of(
            dynamicTest("처음에 category1, 2 를 저장한다.", ()-> {
                //given
                List<Category> categories = List.of(category1, category2);

                //when
                video.updateCategory(categories);

                //then
                List<VideoCategory> videoCategories = video.getVideoCategories();
                assertThat(videoCategories.size()).isEqualTo(2);
                assertThat(videoCategories.get(0).getCategory().getCategoryName()).isEqualTo("category1");
                assertThat(videoCategories.get(1).getCategory().getCategoryName()).isEqualTo("category2");
            }),
            dynamicTest("category2, 3 를 저장하면 1 은 없어지고 3 이 저장된다.", ()-> {
                //given
                List<Category> categories = List.of(category2, category3);

                //when
                video.updateCategory(categories);

                //then
                List<VideoCategory> videoCategories = video.getVideoCategories();
                assertThat(videoCategories.size()).isEqualTo(2);
                assertThat(videoCategories.get(0).getCategory().getCategoryName()).isEqualTo("category2");
                assertThat(videoCategories.get(1).getCategory().getCategoryName()).isEqualTo("category3");
            })
        );
    }

    @Test
    @DisplayName("video 의 추가적인 업로드 프로세스를 진행하여 가격, 상태, 설명, 썸네일, 파일 위치, 카테고리를 업데이트 한다.")
    void additionalCreateProcess() {
        //given
        Member member = createMember();
        Long videoId = 1L;
        Video video = createUploadingVideo(videoId, member);

        int price = 1000;
        String description = "description";
        List<Category> categories = List.of(createCategory("category1"), createCategory("category2"));

        //when
        video.additionalCreateProcess(price, description, categories, true);

        //then
        assertThat(video.getPrice()).isEqualTo(price);
        assertThat(video.getDescription()).isEqualTo(description);
        assertThat(video.getVideoStatus()).isEqualTo(VideoStatus.CREATED);
        assertThat(video.getThumbnailFile()).isEqualTo(member.getMemberId() + "/videos/" + videoId + "/" + video.getVideoName());
        assertThat(video.getVideoFile()).isEqualTo(member.getMemberId() + "/videos/" + videoId + "/" + video.getVideoName());
        assertThat(video.getVideoCategories()).hasSize(2)
                .extracting("category.categoryName")
                .containsExactlyInAnyOrder("category1", "category2");
    }

    @Test
    @DisplayName("video 의 추가적인 업로드 프로세스 진행 중 비디오가 UPLOADING 상태가 아니면 VideoAlreadyCreatedException 이 발생한다.")
    void additionalCreateProcessVideoAlreadyCreatedException() {
        //given
        Video video = createVideoCreated();

        int price = 1000;
        String description = "description";
        List<Category> categories = List.of(createCategory("category1"), createCategory("category2"));

        //when
        assertThatThrownBy(() -> video.additionalCreateProcess(price, description, categories, true))
                .isInstanceOf(VideoAlreadyCreatedException.class);
    }

    @Test
    @DisplayName("video 추가적인 업로드 프로세스 진행 중 가격이 0원이면 생성자에게 100원의 리워드를 제공한다.")
    void additionalCreateProcessProvideReward() {
        //given
        Member member = createMember();
        Long videoId = 1L;
        Video video = createUploadingVideo(videoId, member);
        int beforeReward = member.getReward();

        int price = 0;
        String description = "description";
        List<Category> categories = List.of(createCategory("category1"), createCategory("category2"));

        //when
        video.additionalCreateProcess(price, description, categories, true);

        //then
        assertThat(member.getReward()).isEqualTo(beforeReward + 100);

    }

    @TestFactory
    @DisplayName("video 의 videoStatus 를 변경한다.")
    Collection<DynamicTest> openAndClose() {
        //given
        Video video = createVideoCreated();

        return List.of(
                dynamicTest("비디오 상태를 체크한다. closed 가 아니다", ()-> {
                    //when
                    boolean closed = video.isClosed();

                    //then
                    assertThat(closed).isFalse();
                }),
                dynamicTest("비디오를 closed 로 변경한다.", ()-> {
                    //when
                    video.close();

                    //then
                    assertThat(video.getVideoStatus()).isEqualTo(VideoStatus.CLOSED);
                }),
                dynamicTest("비디오를 created 로 변경한다.", ()-> {
                    //when
                    video.open();

                    //ehn
                    assertThat(video.getVideoStatus()).isEqualTo(VideoStatus.CREATED);
                })
        );
    }

    private Member createMember() {

        Channel channel = Channel.builder()
                .build();

        Member member = Member.builder()
                .memberId(1L)
                .channel(channel)
                .build();

        channel.setMember(member);

        return member;
    }

    private Video createUploadingVideo(Long videoId, Member member) {
       return Video.builder()
               .videoId(videoId)
               .channel(member.getChannel())
               .videoName("videoName")
               .price(0)
               .description("uploading")
               .videoStatus(VideoStatus.UPLOADING)
               .view(0)
               .star(0f)
               .videoCategories(new ArrayList<>())
               .build();

    }

    private Video createVideo(String videoName) {
        return Video.builder()
                .videoName(videoName)
                .price(1000)
                .description("description")
                .view(0)
                .star(0f)
                .videoCategories(new ArrayList<>())
                .build();
    }

    private Video createVideo(List<Reply> replies) {
        return Video.builder()
                .videoName("videoName")
                .price(1000)
                .description("description")
                .view(0)
                .replies(replies)
                .videoCategories(new ArrayList<>())
                .star(0f)
                .build();
    }

    private Video createVideoCreated() {
        return Video.builder()
                .videoName("videoName")
                .price(1000)
                .description("description")
                .view(0)
                .videoStatus(VideoStatus.CREATED)
                .videoCategories(new ArrayList<>())
                .star(0f)
                .build();
    }

    private Reply createReply(int star) {
        Reply reply = Reply.builder()
                .star(star)
                .build();

        return reply;
    }

    private Category createCategory(String categoryName) {
        return Category.builder()
                .categoryName(categoryName)
                .build();
    }
}