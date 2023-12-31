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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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

        List<Video> videos1 = createAndSaveVideos(owner1, 20, category1, category2);
        createAndSaveVideos(owner2, 20, category1, category2);

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
        assertThat(videos.getTotalElements()).isEqualTo(20);
        assertThat(videos.getNumber()).isEqualTo(0);
        assertThat(videos.getSize()).isEqualTo(10);
        assertThat(videos.getContent()).extracting("videoId")
                .containsAnyElementsOf(videos1.stream().map(Video::getVideoId).collect(Collectors.toList()));
    }

    @Test
    @DisplayName("채널의 비디오 목록을 조회할 때 구매된 비디오는 구매되었다고 표시된다.")
    void getChannelVideosPurchase() {
        //given
        Member owner = createMemberWithChannel();

        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());

        Member loginMember = createMemberWithChannel(); // 로그인한 회원
        createAndSaveOrderWithPurchaseComplete(loginMember, List.of(video1), 0);

        em.flush();
        em.clear();

        ChannelVideoGetServiceRequest request = ChannelVideoGetServiceRequest.builder()
                .memberId(owner.getMemberId())
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
        assertThat(findVideo(video1, videos).getIsPurchased()).isTrue();
        assertThat(findVideo(video2, videos).getIsPurchased()).isFalse();
    }

    @Test
    @DisplayName("채널의 비디오 목록을 조회할 때 장바구니에 담긴 비디오는 장바구니에 담겼다고 표시된다.")
    void getChannelVideosInCart() {
        //given
        Member owner = createMemberWithChannel();

        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());

        Member loginMember = createMemberWithChannel(); // 로그인한 회원
        createAndSaveCart(loginMember, video1); // 장바구니에 담기

        em.flush();
        em.clear();

        ChannelVideoGetServiceRequest request = ChannelVideoGetServiceRequest.builder()
                .memberId(owner.getMemberId())
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
        assertThat(findVideo(video1, videos).getIsInCart()).isTrue();
        assertThat(findVideo(video2, videos).getIsInCart()).isFalse();
    }

    @Test
    @DisplayName("채널의 비디오 목록을 조회할 때 비디오의 카테고리는 모두 조회한다.")
    void getChannelVideosWithCategory() {
        //given
        Category category1 = createAndSaveCategory("java");
        Category category2 = createAndSaveCategory("spring");

        Member owner = createMemberWithChannel();

        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());

        createAndSaveVideoCategory(video1, category1);
        createAndSaveVideoCategory(video2, category1, category2);

        Member loginMember = createMemberWithChannel(); // 로그인한 회원

        em.flush();
        em.clear();

        ChannelVideoGetServiceRequest request = ChannelVideoGetServiceRequest.builder()
                .memberId(owner.getMemberId())
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
        assertThat(findVideo(video1, videos).getCategories())
                .extracting("categoryName")
                .containsExactlyInAnyOrder("java");
        assertThat(findVideo(video2, videos).getCategories())
                .extracting("categoryName")
                .containsExactlyInAnyOrder("java", "spring");
    }

    private ChannelVideoResponse findVideo(Video video1, Page<ChannelVideoResponse> videos) {
        return videos.getContent().stream()
                .filter(cr -> cr.getVideoId().equals(video1.getVideoId()))
                .findFirst().orElseThrow();
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
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        //when
        ChannelInfo channelInfo = channelService.getChannel(member.getMemberId(), member.getMemberId());

        //then
        assertThat(channelInfo.getMemberId()).isEqualTo(member.getMemberId());
        assertThat(channelInfo.getChannelName()).isEqualTo(channel.getChannelName());
        assertThat(channelInfo.getIsSubscribed()).isFalse();
        assertThat(channelInfo.getSubscribers()).isEqualTo(channel.getSubscribers());
        assertThat(channelInfo.getDescription()).isEqualTo(channel.getDescription());
        assertThat(channelInfo.getImageUrl()).isEqualTo(awsService.getFileUrl(
                member.getImageFile(),
                FileType.PROFILE_IMAGE
        ));
        assertThat(channelInfo.getCreatedDate()).isEqualTo(channel.getCreatedDate());
    }

    @Test
    @DisplayName("Channel을 조회할 때 존재하지 않으면 ChannelNotFoundException이 발생한다.")
    void getChannelNotFoundException(){
        //given
        Member loginMemberId = createAndSaveMember();
        Channel channel = createAndSaveChannel(loginMemberId);

        //then
        assertThatThrownBy(() -> channelService.getChannel(channel.getMember().getMemberId()+99L, loginMemberId.getMemberId()))
                .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("ChannelName과 ChannelDescription을 받아서 Channel정보를 수정한다.")
    void updateChannel(){
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        String updateChannelName = "updateChannelName";
        String updateDescription = "updateDescription";

        channelRepository.save(channel);

        ChannelUpdate channelUpdate = ChannelUpdate.builder()
                .channelName(updateChannelName)
                .description(updateDescription)
                .build();

        //when
        channelService.updateChannelInfo(member.getMemberId(), member.getMemberId(), channelUpdate);

        //then
        assertThat(channel.getChannelName()).isEqualTo(updateChannelName);
        assertThat(channel.getDescription()).isEqualTo(updateDescription);
    }

    @Test
    @DisplayName("Channel을 수정할 때 해당 채널이 존재하지 않으면 ChannelNotFoundException이 발생한다.")
    void updateChannelNotFoundException(){
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        String updateChannelName = "updateChannelName";
        String updateDescription = "updateDescription";

        //when
        ChannelUpdate channelUpdate = ChannelUpdate.builder()
                .channelName(updateChannelName)
                .description(updateDescription)
                .build();

        //then
        assertThatThrownBy(() -> channelService.updateChannelInfo(member.getMemberId(), channel.getMember().getMemberId()+999L, channelUpdate));
    }

    @Test
    @DisplayName("Channel을 수정할 때 해당 채널의 주인이 아니면 MemberAccessDeniedException이 발생한다.")
    void updateChannelMemberAccessDeniedException(){
        //given
        Member member = createAndSaveMember();

        String updateChannelName = "updateChannelName";
        String updateDescription = "updateDescription";

        ChannelUpdate channelUpdate = ChannelUpdate.builder()
                .channelName(updateChannelName)
                .description(updateDescription)
                .build();

        //then
        assertThatThrownBy(() -> channelService.updateChannelInfo(member.getMemberId()+99L, member.getMemberId(), channelUpdate))
                .isInstanceOf(MemberAccessDeniedException.class);
    }


    @Test
    @DisplayName("Channel이 구독중이지 않으면 구독한다.")
    void updateSubscribe(){
        //given
        Member loginMember = createAndSaveMember();
        Channel channel = createAndSaveChannel(loginMember);

        //when
        boolean subscribe = channelService.updateSubscribe(loginMember.getMemberId(), channel.getMember().getMemberId());

        //then
        assertTrue(subscribe);
        assertThat(subscribeRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Channel이 구독중이면 구독해지한다.")
    void updateSubscribeCancel() {
        //given
        Member loginMember = createAndSaveMember();
        Channel channel = createAndSaveChannel(loginMember);

        //when
        boolean initialSubscribe = channelService.updateSubscribe(loginMember.getMemberId(), channel.getMember().getMemberId());

        boolean unsubscribe = channelService.updateSubscribe(loginMember.getMemberId(), channel.getMember().getMemberId());

        //then
        assertThat(initialSubscribe).isTrue();
        assertFalse(unsubscribe);
        assertThat(subscribeRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("비로그인 사용자가 채널을 조회하면 구독여부는 나오지 않는다")
    void getChannelNotLogin(){
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        //when
        ChannelInfo channelInfo = channelService.getChannel(member.getMemberId(), null);

        //then
        assertThat(channelInfo.getMemberId()).isEqualTo(channel.getMember().getMemberId());
        assertThat(channelInfo.getChannelName()).isEqualTo(channel.getChannelName());
        assertThat(channelInfo.getIsSubscribed()).isFalse();
        assertThat(channelInfo.getDescription()).isEqualTo(channel.getDescription());
        assertThat(channelInfo.getSubscribers()).isEqualTo(channel.getSubscribers());
        assertThat(channelInfo.getImageUrl()).isEqualTo(awsService.getFileUrl(
                member.getImageFile(),
                FileType.PROFILE_IMAGE
        ));
        assertThat(channelInfo.getCreatedDate()).isEqualTo(channel.getCreatedDate());
    }

    @Test
    @DisplayName("로그인한 멤버가 타 유저의 채널을 구독하면 True가 나온다")
    void subscribeToOtherUserChannelReturnsTrue() {
        //given
        Member loginMember = createAndSaveMember();
        Member otherMember = createAndSaveMember();
        Channel otherMemberChannel = createAndSaveChannel(otherMember);

        //when
        boolean isSubscribed = channelService.updateSubscribe(otherMemberChannel.getMember().getMemberId(), loginMember.getMemberId());

        //then
        assertThat(subscribeRepository.findAll().size()).isEqualTo(1);

        assertTrue(isSubscribed);
    }

    @Test
    @DisplayName("로그인한 멤버가 타유저의 채널을 구독해지하면 False가 나온다")
    void updateSubscribeFalse() {
        //given
        Member loginMember = createAndSaveMember();
        Member otherMember = createAndSaveMember();
        Channel otherMemberChannel = createAndSaveChannel(otherMember);

        //when
        boolean isSubscribed = channelService.updateSubscribe(otherMemberChannel.getMember().getMemberId(), loginMember.getMemberId());
        boolean unsubscribe = channelService.updateSubscribe(otherMemberChannel.getMember().getMemberId(), loginMember.getMemberId());

        //then
        assertThat(isSubscribed).isTrue();
        assertThat(subscribeRepository.findAll().size()).isEqualTo(0);
        assertFalse(unsubscribe);
    }

    @Test
    @DisplayName("채널이 존재하지 않으면 ChannelNotFoundException이 발생한다.")
    void updateSubscribeChannelNotFoundException(){
        //given
        Member loginMember = createAndSaveMember();
        Member otherMember = createAndSaveMember();
        Channel otherMemberChannel = createAndSaveChannel(otherMember);

        //then
        assertThatThrownBy(() -> channelService.updateSubscribe(loginMember.getMemberId()+999L, otherMemberChannel.getMember().getMemberId()))
                .isInstanceOf(ChannelNotFoundException.class);
    }

    @Test
    @DisplayName("채널을 구독하면 구독자 수가 1 증가한다.")
    void updateSubscribeIncreaseSubscribers(){
        //given
        Member loginMember = createAndSaveMember();
        Member otherMember = createAndSaveMember();
        Channel otherMemberChannel = createAndSaveChannel(otherMember);

        //when
        channelService.updateSubscribe(otherMemberChannel.getMember().getMemberId(), loginMember.getMemberId());

        //then
        Channel channel = channelRepository.findById(otherMemberChannel.getChannelId()).get();
        assertThat(channel.getSubscribers()).isEqualTo(1);
    }

    @Test
    @DisplayName("타유저의 채널을 구독해지하면 구독자 수가 1 감소한다.")
    void updateSubscribeDecreaseSubscribers() {
        //given
        Member loginMember = createAndSaveMember();
        Member otherMember = createAndSaveMember();
        Channel otherMemberChannel = createAndSaveChannel(otherMember);
        Long Subscribers = otherMemberChannel.getMember().getMemberId();

        //when
        channelService.updateSubscribe(otherMemberChannel.getMember().getMemberId(), loginMember.getMemberId());
        channelService.updateSubscribe(otherMemberChannel.getMember().getMemberId(), loginMember.getMemberId());

        //then
        Long updatedChannel = otherMemberChannel.getMember().getMemberId();
        assertThat(updatedChannel).isEqualTo(Subscribers);
    }

    @Test
    @DisplayName("로그인한 사용자만 채널을 수정 할 수 있다.")
    void OnlyUserUpdateChannelInfo() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Long loginMemberId = member.getMemberId();
        Long ownerId = channel.getMember().getMemberId();

        //when
        if(!loginMemberId.equals(ownerId)){
            throw new MemberAccessDeniedException();
        } else {
            channel.updateChannel("updateChannelName", "updateDescription");
        }

        //then
        assertThat(channel.getChannelName()).isEqualTo("updateChannelName");
        assertThat(channel.getDescription()).isEqualTo("updateDescription");

    }

    @Test
    @DisplayName("비회원은 구독취소를 할 수 없다.")
    void OnlyUserUpdateSubscribeCancel() {
        //given
        Member loginMember = createAndSaveMember();
        Member otherMember = createAndSaveMember();
        Channel otherMemberChannel = createAndSaveChannel(otherMember);

        Long loginMemberId = loginMember.getMemberId();
        Long otherMemberId = otherMemberChannel.getMember().getMemberId();

        channelService.updateSubscribe(otherMemberId, loginMemberId);

        //when&then
        assertThrows(MemberAccessDeniedException.class, () -> {
            if (!loginMemberId.equals(otherMemberId)) {
                throw new MemberAccessDeniedException();
            } else {
                channelService.updateSubscribe(otherMemberChannel.getMember().getMemberId(), loginMember.getMemberId());
            }
        });

        assertThat(subscribeRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("로그인하지 않은 사용자는 -1이 반환된다")
    void getChannelNotLoginMemberId() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        //when
        ChannelInfo channelInfo = channelService.getChannel(member.getMemberId(), null);

        //then
        assertThat(channelInfo.getMemberId()).isEqualTo(channel.getMember().getMemberId());
        assertThat(channelInfo.getChannelName()).isEqualTo(channel.getChannelName());
        assertThat(channelInfo.getIsSubscribed()).isFalse();
        assertThat(channelInfo.getDescription()).isEqualTo(channel.getDescription());
        assertThat(channelInfo.getSubscribers()).isEqualTo(channel.getSubscribers());
        assertThat(channelInfo.getImageUrl()).isEqualTo(awsService.getFileUrl(
                member.getImageFile(),
                FileType.PROFILE_IMAGE
        ));
        assertThat(channelInfo.getCreatedDate()).isEqualTo(channel.getCreatedDate());
    }
}