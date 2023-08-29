//package com.server.domain.channel.service;
//
//import com.server.domain.channel.entity.Channel;
//import com.server.domain.channel.respository.ChannelRepository;
//import com.server.domain.channel.service.dto.ChannelDto;
//import com.server.domain.member.entity.Member;
//import com.server.domain.member.repository.MemberRepository;
//import com.server.domain.subscribe.entity.Subscribe;
//import com.server.domain.subscribe.entity.repository.SubscribeRepository;
//import com.server.domain.video.entity.Video;
//import com.server.domain.video.repository.VideoRepository;
//import com.server.global.exception.businessexception.channelException.ChannelNotFoundException;
//import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
//import com.server.module.s3.service.AwsService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.catchThrowable;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class ChannelServiceTest {
//
//    @Mock
//    private ChannelRepository channelRepository;
//
//    @Mock
//    private MemberRepository memberRepository;
//
//    @Mock
//    private VideoRepository videoRepository;
//
//    @InjectMocks
//    private ChannelService channelService;
//
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test
//    @DisplayName("채널정보조회")
//    void getChannelInfoTest() {
//        // Given
//        Long channelId = 1L;
//        Member member = new Member();
//        Channel channel = new Channel();
//        channel.setMember(member);
//        channel.setChannelName("tobesuccessful");
//        channel.setSubscribers(1111);
//        channel.setDescription("tobesuccessful");
//
//        when(channelRepository.findByChannelId(channelId)).thenReturn(channel);
//
//        // When
//        ChannelDto.ChannelInfo channelInfo = channelService.getChannelInfo(channelId);
//
//        // Then
//        assertThat(channelInfo).isNotNull();
//        assertThat(channelInfo.getMemberId()).isEqualTo(member.getMemberId());
//        assertThat(channelInfo.getChannelName()).isEqualTo("tobesuccessful");
//        assertThat(channelInfo.getSubscribers()).isEqualTo(1111);
//        assertThat(channelInfo.getDescription()).isEqualTo("tobesuccessful");
//    }
//
//
//    @DisplayName("접근제한 테스트")
//    @Test
//    public void subscribeAccessTest() {
//        Long memberId = 1L;
//        Long loginMemberId = 2L;
//
//        Member memberMock = mock(Member.class);
//        when(memberMock.getMemberId()).thenReturn(memberId);
//
//        Channel channel = new Channel();
//        when(memberMock.getChannel()).thenReturn(channel);
//
//        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberMock));
//
//        assertThrows(MemberAccessDeniedException.class, () -> {
//            channelService.updateSubscribe(memberId, loginMemberId);
//        });
//
//    }
//
//    @DisplayName("접근제한 테스트2")
//    @Test
//    public void subscribeAccessTest2() {
//        Long memberId = 1L;
//        Long loginMemberId = 1L;
//
//        Member memberMock = mock(Member.class);
//        when(memberMock.getMemberId()).thenReturn(memberId);
//
//        Channel channel = new Channel();
//        when(memberMock.getChannel()).thenReturn(channel);
//
//        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberMock));
//
//        channelService.updateSubscribe(memberId, loginMemberId);
//    }
//
//    @DisplayName("채널전체목록 조회")
//    @Test
//    public void testGetChannelVideos() {
//        Long loggedInMemberId = 1L;
//        Long memberId = 1L;
//        int page = 1;
//        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
//
//        Member memberMock = mock(Member.class);
//        when(memberMock.getMemberId()).thenReturn(memberId);
//
//        Channel channelMock = mock(Channel.class);
//        when(memberMock.getChannel()).thenReturn(channelMock);
//
//        Page<Video> videoPageMock = mock(Page.class);
//        when(videoPageMock.getContent()).thenReturn(new ArrayList<>());
//
//        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberMock));
//        when(channelRepository.findByMember(memberMock)).thenReturn(channelMock);
//        when(videoRepository.findByChannel(eq(channelMock), any(PageRequest.class))).thenReturn(videoPageMock);
//
//        List<ChannelDto.ChannelVideoResponseDto> result = channelService.getChannelVideos(loggedInMemberId, memberId, page, sort);
//
//    }
//
//    @DisplayName("일치하는사용자 테스트")
//    @Test
//    void sameUserTest() {
//        Long loginMemberId = 1L;
//        Long adminId = 1L;
//
//        Member adminMember = mock(Member.class);
//        when(adminMember.getMemberId()).thenReturn(adminId);
//
//        when(memberRepository.findById(adminId)).thenReturn(Optional.of(adminMember));
//
//        boolean result = channelService.isLoggedInMember(loginMemberId, adminId);
//
//        assertTrue(result);
//    }
//
//    @DisplayName("불일치하는사용자 테스트")
//    @Test
//    void differentUserTest() {
//        Long loginMemberId = 1L;
//        Long adminId = 2L;
//
//        Member adminMember = mock(Member.class);
//        when(adminMember.getMemberId()).thenReturn(adminId);
//
//        when(memberRepository.findById(adminId)).thenReturn(Optional.of(adminMember));
//
//        boolean result = channelService.isLoggedInMember(loginMemberId, adminId);
//
//        assertFalse(result);
//    }
//}