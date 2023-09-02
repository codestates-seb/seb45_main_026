package com.server.domain.channel.service;

import com.server.domain.category.entity.Category;
import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.service.dto.ChannelInfo;
import com.server.domain.channel.service.dto.ChannelUpdate;
import com.server.domain.channel.service.dto.request.ChannelVideoGetServiceRequest;
import com.server.domain.channel.service.dto.response.ChannelVideoResponse;
import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.global.exception.businessexception.channelException.ChannelNotFoundException;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
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

        int i = 0;
        for (ChannelVideoResponse video : videos) {
            if (i % 2 == 0) {
                assertThat(video.getCategories().get(0).getCategoryName()).isEqualTo("java");
            }else {
                assertThat(video.getCategories().get(0).getCategoryName()).isEqualTo("spring");
            }
            if(i % 3 == 0) {
                assertThat(video.getIsPurchased()).isTrue();
            }
            i++;
        }
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

    @Test
    @DisplayName("memberId를 통해 Channel을 조회한다.")
    void getChannel(){
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        ChannelInfo channelInfo = channelService.getChannel( member.getMemberId(), member.getMemberId());

        assertThat(channelInfo.getMemberId()).isEqualTo(member.getMemberId());
        assertThat(channelInfo.getChannelName()).isEqualTo(channel.getChannelName());
        assertThat(channelInfo.getSubscribers()).isEqualTo(channel.getSubscribers());
        assertThat(channelInfo.getDescription()).isEqualTo(channel.getDescription());
        assertThat(channelInfo.getImageUrl()).isEqualTo(member.getImageFile());
        assertThat(channelInfo.isSubscribed()).isFalse();
        assertThat(channelInfo.getCreatedDate()).isEqualTo(channel.getCreatedDate());
    }

    @Test
    @DisplayName("Channel을 조회할 때 채널이 존재하지 않으면 ChannelNotFoundException이 발생한다.")
    void getChannelNotFoundException(){
        Member loginMemberId = createAndSaveMember();
        Long findChannel = 99L;

        assertThatThrownBy(() -> channelService.getChannel(findChannel, loginMemberId.getMemberId()))
                .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("ChannelName과 ChannelDescription을 받아서 Channel정보를 수정한다.")
    void updateChannel(){
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        String updateChannelName = "updateChannelName";
        String updateDescription = "updateDescription";

        ChannelUpdate channelUpdate = ChannelUpdate.builder()
                .channelName(updateChannelName)
                .description(updateDescription)
                .build();

        channelService.updateChannelInfo(member.getMemberId(), member.getMemberId(), channelUpdate);

        assertThat(channel.getChannelName()).isEqualTo(updateChannelName);
        assertThat(channel.getDescription()).isEqualTo(updateDescription);
    }

    @Test
    @DisplayName("Channel정보 수정시 loginMember와 요청한 채널을 소유한 멤버 ID가 다르면 MemberAccessDeniedException이 발생한다.")
    void updateChannelMemberAccessDeniedException(){

        Member loginMemberId = createAndSaveMember();
        Member owner = createAndSaveMember();

        String updateChannelName = "updateChannelName";
        String updateDescription = "updateDescription";

        ChannelUpdate channelUpdate = ChannelUpdate.builder()
                .channelName(updateChannelName)
                .description(updateDescription)
                .build();

        assertThatThrownBy(() -> channelService.updateChannelInfo(loginMemberId.getMemberId(), owner.getMemberId()+99L, channelUpdate))
                .isInstanceOf(MemberAccessDeniedException.class);
    }


    @Test
    @DisplayName("Channel이 구독중이지 않으면 구독한다.")
    void updateSubscribe(){
        Member member = createAndSaveMember();

        boolean subscribe = channelService.updateSubscribe(member.getMemberId(), member.getMemberId());

        assertThat(subscribe).isTrue();
    }

    @Test
    @DisplayName("Channel이 구독중이면 구독해지한다.")
    void updateSubscribeCancel(){
        Member member = createAndSaveMember();

        boolean subscribe = channelService.updateSubscribe(member.getMemberId(), member.getMemberId());
        assertThat(subscribe).isTrue(); //구독상태로 만들어줌

        boolean unsubscribe = channelService.updateSubscribe(member.getMemberId(), member.getMemberId());
        assertThat(unsubscribe).isTrue(); //구독을취소
    }
}