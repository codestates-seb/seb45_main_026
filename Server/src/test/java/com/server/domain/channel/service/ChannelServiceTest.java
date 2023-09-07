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
import com.server.module.s3.service.AwsService;
import com.server.module.s3.service.dto.FileType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

class ChannelServiceTest extends ServiceTest {

    @Autowired ChannelService channelService;
    @Autowired AwsService awsService;

    @Test
    @DisplayName("채널의 비디오를 페이징하여 조회한다.")
    void getChannelVideos() {
        //given
        Member loginMember = createMemberWithChannel(); // 로그인한 회원

        Category category1 = createAndSaveCategory("java");
        Category category2 = createAndSaveCategory("spring");

        Member owner1 = createMemberWithChannel();

        Member owner2 = createMemberWithChannel();

        List<Video> videos1 = createAndSaveVideos(owner1, 60, category1, category2);
        createAndSaveVideos(owner2, 60, category1, category2);

        em.flush();
        em.clear();

        ChannelVideoGetServiceRequest request = ChannelVideoGetServiceRequest.builder()
                .memberId(owner1.getMemberId())
                .page(0)
                .size(10)
                .categoryName(null)
                .free(null)
                .isPurchased(true)
                .sort(null)
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

    private List<Video> createAndSaveVideos(Member member, int count, Category category1, Category category2) {

        List<Video> videos = new ArrayList<>();

        for(int i = 1; i <= count; i++) {

            Video video;

            if(i % 2 == 0) {
                video = createAndSaveVideo(member.getChannel());
                createAndSaveVideoCategory(video, category1);
            }else {
                video = createAndSaveVideo(member.getChannel());
                createAndSaveVideoCategory(video, category2);
            }
            if(i % 3 == 0) createAndSaveOrderWithPurchaseComplete(member, List.of(video), 0);

            videos.add(video);
        }

        return videos;
    }

    @Test
    @DisplayName("memberId를 통해 Channel을 조회한다.")
    void getChannel(){
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        ChannelInfo channelInfo = channelService.getChannel(member.getMemberId(), member.getMemberId());

        assertThat(channelInfo.getMemberId()).isEqualTo(member.getMemberId());
        assertThat(channelInfo.getChannelName()).isEqualTo(channel.getChannelName());
        assertThat(channelInfo.getIsSubscribed()).isFalse();
        assertThat(channelInfo.getSubscribers()).isEqualTo(channel.getSubscribers());
        assertThat(channelInfo.getDescription()).isEqualTo(channel.getDescription());
        assertThat(channelInfo.getImageUrl()).isEqualTo(awsService.getFileUrl(
                channel.getMember().getMemberId(),
                member.getImageFile(),
                FileType.PROFILE_IMAGE
        ));
        assertThat(channelInfo.getCreatedDate()).isEqualTo(channel.getCreatedDate());
    }

    @Test
    @DisplayName("Channel을 조회할 때 존재하지 않으면 ChannelNotFoundException이 발생한다.")
    void getChannelNotFoundException(){
        Member loginMemberId = createAndSaveMember();
        Channel channel = createAndSaveChannel(loginMemberId);

        assertThatThrownBy(() -> channelService.getChannel(channel.getMember().getMemberId()+99L, loginMemberId.getMemberId()))
                .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("ChannelName과 ChannelDescription을 받아서 Channel정보를 수정한다.")
    void updateChannel(){
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        String updateChannelName = "updateChannelName";
        String updateDescription = "updateDescription";

        channelRepository.save(channel);

        ChannelUpdate channelUpdate = ChannelUpdate.builder()
                .channelName(updateChannelName)
                .description(updateDescription)
                .build();

        channelService.updateChannelInfo(member.getMemberId(), member.getMemberId(), channelUpdate);

        assertThat(channel.getChannelName()).isEqualTo(updateChannelName);
        assertThat(channel.getDescription()).isEqualTo(updateDescription);
    }

    @Test
    @DisplayName("Channel을 수정할 때 해당 채널이 존재하지 않으면 ChannelNotFoundException이 발생한다.")
    void updateChannelNotFoundException(){
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);


        String updateChannelName = "updateChannelName";
        String updateDescription = "updateDescription";

        ChannelUpdate channelUpdate = ChannelUpdate.builder()
                .channelName(updateChannelName)
                .description(updateDescription)
                .build();

        assertThatThrownBy(() -> channelService.updateChannelInfo(member.getMemberId(), channel.getMember().getMemberId()+999L, channelUpdate));
    }

    @Test
    @DisplayName("Channel을 수정할 때 해당 채널의 주인이 아니면 MemberAccessDeniedException이 발생한다.")
    void updateChannelMemberAccessDeniedException(){
        Member member = createAndSaveMember();

        String updateChannelName = "updateChannelName";
        String updateDescription = "updateDescription";

        ChannelUpdate channelUpdate = ChannelUpdate.builder()
                .channelName(updateChannelName)
                .description(updateDescription)
                .build();

        assertThatThrownBy(() -> channelService.updateChannelInfo(member.getMemberId()+99L, member.getMemberId(), channelUpdate))
                .isInstanceOf(MemberAccessDeniedException.class);
    }


    @Test
    @DisplayName("Channel이 구독중이지 않으면 구독한다.")
    void updateSubscribe(){
        Member loginMember = createAndSaveMember();
        Channel channel = createAndSaveChannel(loginMember);

        boolean subscribe = channelService.updateSubscribe(loginMember.getMemberId(), channel.getMember().getMemberId());

        assertTrue(subscribe);
    }

    @Test
    @DisplayName("Channel이 구독중이면 구독해지한다.")
    void updateSubscribeCancel() {
        Member loginMember = createAndSaveMember();
        Channel channel = createAndSaveChannel(loginMember);

        boolean initialSubscribe = channelService.updateSubscribe(loginMember.getMemberId(), channel.getMember().getMemberId());

        boolean unsubscribe = channelService.updateSubscribe(loginMember.getMemberId(), channel.getMember().getMemberId());

        assertThat(initialSubscribe).isTrue();
        assertFalse(unsubscribe);
    }

    @Test
    @DisplayName("비로그인 사용자가 채널을 조회하면 구독여부는 나오지 않는다")
    void getChannelNotLogin(){
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        ChannelInfo channelInfo = channelService.getChannel(member.getMemberId(), null);

        assertThat(channelInfo.getMemberId()).isEqualTo(channel.getMember().getMemberId());
        assertThat(channelInfo.getChannelName()).isEqualTo(channel.getChannelName());
        assertThat(channelInfo.getIsSubscribed()).isFalse();
        assertThat(channelInfo.getDescription()).isEqualTo(channel.getDescription());
        assertThat(channelInfo.getSubscribers()).isEqualTo(channel.getSubscribers());
        assertThat(channelInfo.getImageUrl()).isEqualTo(awsService.getFileUrl(
                channel.getMember().getMemberId(),
                member.getImageFile(),
                FileType.PROFILE_IMAGE
        ));
        assertThat(channelInfo.getCreatedDate()).isEqualTo(channel.getCreatedDate());
    }

    @Test
    @DisplayName("로그인한 멤버가 타 유저의 채널을 구독하면 True가 나온다")
    void subscribeToOtherUserChannelReturnsTrue() {
        Member loginMember = createAndSaveMember();
        Member otherMember = createAndSaveMember();
        Channel otherMemberChannel = createAndSaveChannel(otherMember);

        boolean isSubscribed = channelService.updateSubscribe(otherMemberChannel.getMember().getMemberId(), loginMember.getMemberId());

        assertThat(subscribeRepository.findAll().size()).isEqualTo(1);

        assertTrue(isSubscribed);
    }

    @Test
    @DisplayName("로그인한 멤버가 타유저의 채널을 구독해지하면 False가 나온다")
    void updateSubscribeFalse() {
        Member loginMember = createAndSaveMember();
        Member otherMember = createAndSaveMember();
        Channel otherMemberChannel = createAndSaveChannel(otherMember);

        boolean isSubscribed = channelService.updateSubscribe(otherMemberChannel.getMember().getMemberId(), loginMember.getMemberId());
        boolean unsubscribe = channelService.updateSubscribe(otherMemberChannel.getMember().getMemberId(), loginMember.getMemberId());

        assertThat(isSubscribed).isTrue();
        assertThat(subscribeRepository.findAll().size()).isEqualTo(0);
        assertFalse(unsubscribe);
    }

    @Test
    @DisplayName("채널이 존재하지 않으면 ChannelNotFoundException이 발생한다.")
    void updateSubscribeChannelNotFoundException(){
        Member loginMember = createAndSaveMember();
        Member otherMember = createAndSaveMember();
        Channel otherMemberChannel = createAndSaveChannel(otherMember);

        assertThatThrownBy(() -> channelService.updateSubscribe(loginMember.getMemberId()+999L, otherMemberChannel.getMember().getMemberId()))
                .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("채널을 구독하면 구독자 수가 1 증가한다.")
    void updateSubscribeIncreaseSubscribers(){
        Member loginMember = createAndSaveMember();
        Member otherMember = createAndSaveMember();
        Channel otherMemberChannel = createAndSaveChannel(otherMember);

        channelService.updateSubscribe(otherMemberChannel.getMember().getMemberId(), loginMember.getMemberId());

        Channel channel = channelRepository.findById(otherMemberChannel.getChannelId()).get();

        assertThat(channel.getSubscribers()).isEqualTo(1);
    }

    @Test
    @DisplayName("타유저의 채널을 구독해지하면 구독자 수가 1 감소한다.")
    void updateSubscribeDecreaseSubscribers() {
        Member loginMember = createAndSaveMember();
        Member otherMember = createAndSaveMember();
        Channel otherMemberChannel = createAndSaveChannel(otherMember);
        Long Subscribers = otherMemberChannel.getMember().getMemberId();

        channelService.updateSubscribe(otherMemberChannel.getMember().getMemberId(), loginMember.getMemberId());
        channelService.updateSubscribe(otherMemberChannel.getMember().getMemberId(), loginMember.getMemberId());

        Long updatedChannel = otherMemberChannel.getMember().getMemberId();
        assertThat(updatedChannel).isEqualTo(Subscribers);
    }









}