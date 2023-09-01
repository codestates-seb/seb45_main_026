package com.server.domain.channel.service;

import com.server.domain.category.entity.Category;
import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.service.dto.request.ChannelVideoGetServiceRequest;
import com.server.domain.channel.service.dto.response.ChannelVideoResponse;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.ServiceTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

class ChannelServiceTest extends ServiceTest {

    @Autowired ChannelService channelService;

    @Test
    @DisplayName("채널의 비디오를 페이징하여 조회한다.")
    void getChannelVideos() {
        //given
        Member loginMember = createAndSaveMember(); // 로그인한 회원

        Category category1 = createAndSaveCategory("java");
        Category category2 = createAndSaveCategory("spring");

        Member owner1 = createAndSaveMember();
        Channel channel1 = createAndSaveChannel(owner1);

        Member owner2 = createAndSaveMember();
        Channel channel2 = createAndSaveChannel(owner2);

        List<Video> videos1 = createAndSaveVideos(loginMember, channel1, 60, category1, category2);
        createAndSaveVideos(loginMember, channel2, 60, category1, category2);

        em.flush();
        em.clear();

        ChannelVideoGetServiceRequest request = ChannelVideoGetServiceRequest.builder()
                .memberId(owner1.getMemberId())
                .page(0)
                .size(10)
                .categoryName(null)
                .build();

        //when
        Page<ChannelVideoResponse> videos = channelService.getChannelVideos(loginMember.getMemberId(), request);

        //then
        assertThat(videos.getTotalElements()).isEqualTo(60);
        assertThat(videos.getNumber()).isEqualTo(0);
        assertThat(videos.getSize()).isEqualTo(10);
        assertThat(videos.getContent()).extracting("videoId")
                .containsAnyElementsOf(videos1.stream().map(Video::getVideoId).collect(Collectors.toList()));
    }

    private List<Video> createAndSaveVideos(Member purchaseMember, Channel channel, int count, Category category1, Category category2) {

        List<Video> videos = new ArrayList<>();

        for(int i = 1; i <= 60; i++) {

            Video video;

            if(i % 2 == 0) {
                video = createAndSaveVideo(channel);
                createAndSaveVideoCategory(video, category1);
            }else {
                video = createAndSaveVideo(channel);
                createAndSaveVideoCategory(video, category2);
            }
            if(i % 3 == 0) createAndSaveOrderWithPurchaseComplete(purchaseMember, List.of(video), 0);

            videos.add(video);
        }

        return videos;
    }
}