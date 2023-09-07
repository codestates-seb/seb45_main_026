package com.server.domain.member.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.cart.entity.Cart;
import com.server.domain.channel.entity.Channel;
import com.server.domain.channel.respository.ChannelRepository;
import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.dto.MemberVideoData;
import com.server.domain.order.entity.Order;
import com.server.domain.subscribe.entity.Subscribe;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import com.server.domain.watch.entity.Watch;
import com.server.global.exception.businessexception.channelException.ChannelNotFoundException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.videoexception.VideoNotFoundException;
import com.server.global.testhelper.RepositoryTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.server.domain.channel.entity.QChannel.*;
import static com.server.domain.video.entity.QVideo.*;
import static com.server.domain.watch.entity.QWatch.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;


class MemberRepositoryTest extends RepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired ChannelRepository channelRepository;
    @Autowired VideoRepository videoRepository;
    private JPAQueryFactory queryFactory;

    @Test
    @DisplayName("회원이 비디오를 구매한 적이 있는지 확인한다.")
    void checkMemberPurchaseVideo() {
        // given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);
        Order order = createAndSaveOrder(member, List.of(video));
        order.completeOrder(LocalDateTime.now());
        em.persist(order);

        em.flush();
        em.clear();

        // when
        Boolean isPurchased = memberRepository.checkMemberPurchaseVideo(member.getMemberId(), video.getVideoId());

        // then
        assertThat(isPurchased).isTrue();
    }

    @Test
    @DisplayName("회원이 비디오를 구매한 적이 있는지 확인한다. 없으면 false 를 반환한다.")
    void checkMemberPurchaseVideoFalse() {
        // given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        em.flush();
        em.clear();

        // when
        Boolean isPurchased = memberRepository.checkMemberPurchaseVideo(member.getMemberId(), video.getVideoId());

        // then
        assertThat(isPurchased).isFalse();
    }

    @Test
    @DisplayName("회원이 주문을 했다가 취소한 경우에 구매한 적이 없다고 판단한다.")
    void checkMemberPurchaseVideoOrderCancel() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);
        Order order = createAndSaveOrder(member, List.of(video));

        order.cancelAllOrder();

        em.flush();
        em.clear();

        //when
        Boolean isPurchased = memberRepository.checkMemberPurchaseVideo(member.getMemberId(), video.getVideoId());

        //then
        assertThat(isPurchased).isFalse();
    }

    @Test
    @DisplayName("videoId 목록으로 특정 멤버가 구매했는지 확인한다.")
    void checkMemberPurchaseVideos() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);
        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);
        Video video3 = createAndSaveVideo(channel);

        Member member = createAndSaveMember();
        createAndSaveOrderComplete(member, List.of(video1, video2)); //video1, video2 구매

        List<Long> videoIds = List.of(video1.getVideoId(), video2.getVideoId(), video3.getVideoId());

        em.flush();
        em.clear();

        //when (video1, 2, 3 을 구매했는지 확인)
        List<Boolean> isPurchased = memberRepository.checkMemberPurchaseVideos(member.getMemberId(), videoIds);

        //then
        assertThat(isPurchased).hasSize(3)
            .containsExactlyInAnyOrder(true, true, false);

    }

    @Test
    @DisplayName("회원 id 로 회원이 구매한 비디오를 모두 조회한다.")
    void getMemberPurchaseVideo() {
        // given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video1 = createAndSaveVideo(channel);
        Video video2 = createAndSaveVideo(channel);
        Video video3 = createAndSaveVideo(channel); // 구매안한 비디오

        Order order = createAndSaveOrder(member, List.of(video1, video2));

        em.flush();
        em.clear();

        // when
        List<MemberVideoData> memberPurchaseVideo = memberRepository.getMemberPurchaseVideo(member.getMemberId());

        // then
        assertThat(memberPurchaseVideo).hasSize(2)
            .extracting("videoId").containsExactlyInAnyOrder(video1.getVideoId(), video2.getVideoId());
    }

    @Test
    @DisplayName("회원의 특정 채널 구독 여부를 리스트 형태로 조회한다.")
    void checkMemberSubscribeChannel() {
        //given
        Member member1 = createAndSaveMember();
        Member member2 = createAndSaveMember();
        Member member3 = createAndSaveMember();
        Channel channel1 = createAndSaveChannel(member1);
        Channel channel2 = createAndSaveChannel(member2);
        Channel channel3 = createAndSaveChannel(member3);

        Member member = createAndSaveMember(); // member1  은 channel1, 2 를 구독
        createAndSaveSubscribe(member, channel1);
        createAndSaveSubscribe(member, channel2);

        //구독을 확인할 memberId
        List<Long> memberIds = List.of(member1.getMemberId(), member2.getMemberId(), member3.getMemberId());

        em.flush();
        em.clear();

        //when
        List<Boolean> isSubscribed = memberRepository.checkMemberSubscribeChannel(member.getMemberId(), memberIds);

        //then
        assertThat(isSubscribed).hasSize(3)
            .containsExactly(true, true, false);
    }

    @Test
    @DisplayName("회원의 구독한 채널 목록을 페이지 형태로 조회한다.")
    void findSubscribeWithChannelForMember() throws InterruptedException {
        Member owner1 = createAndSaveMember();
        Channel channel1 = createAndSaveChannel(owner1);

        Member owner2 = createAndSaveMember();
        Channel channel2 = createAndSaveChannel(owner2);

        Member owner3 = createAndSaveMember();
        Channel channel3 = createAndSaveChannel(owner3);

        Member loginMember = createAndSaveMember();

        createAndSaveSubscribe(loginMember, channel1);
        Thread.sleep(100L);
        createAndSaveSubscribe(loginMember, channel2);
        Thread.sleep(100L);
        createAndSaveSubscribe(loginMember, channel3);

        em.flush();
        em.clear();

        int page = 1, size = 10;
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Channel> channels =
            memberRepository.findSubscribeWithChannelForMember(loginMember.getMemberId(), pageable);

        assertThat(channels.getContent()).isSortedAccordingTo(
            Comparator.comparing(Channel::getCreatedDate).reversed()
        );
    }

    @Test
    @DisplayName("회원의 결제 목록을 날짜순으로 조회한다.")
    void findOrdersOrderByCreatedDateForMember() {
        int page = 1, size = 10;
        Pageable pageable = PageRequest.of(page - 1, size);

        Member user = createAndSaveMember();

        for (int x = 1; x < 21; x++) {
            List<Video> videos = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                Member member = createAndSaveMember();
                Channel channel = createAndSaveChannel(member);
                Video video = createAndSaveVideo(channel);

                videos.add(video);
            }

            createAndSaveOrderComplete(user, videos);
        }

        em.flush();
        em.clear();

        Page<Order> result =
            memberRepository.findOrdersOrderByCreatedDateForMember(user.getMemberId(), pageable, 1);

        assertThat(result.getContent()).isSortedAccordingTo(
            Comparator.comparing(Order::getCreatedDate).reversed()
        );
    }

    @Test
    @DisplayName("회원의 장바구니 목록을 최신순으로 20개 조회한다.")
    void findCartsOrderByCreatedDateForMember() {
        Member user = createAndSaveMember();

        List<Cart> carts = new ArrayList<>();
        List<Video> videos = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Member member = createAndSaveMember();
            Channel channel = createAndSaveChannel(member);
            Video video = createAndSaveVideo(channel);

            videos.add(video);
            carts.add(createAndSaveCartWithVideo(user, video));
        }

        em.flush();
        em.clear();

        int page = 1, size = 20;
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Cart> result =
            memberRepository.findCartsOrderByCreatedDateForMember(user.getMemberId(), pageable);

        assertThat(result.getContent()).isSortedAccordingTo(
            Comparator.comparing(Cart::getCreatedDate).reversed()
        );
        assertThat(result.getTotalElements()).isEqualTo(20);
    }

    @TestFactory
    @DisplayName("회원의 구매한 영상 보관함을 채널순, 영상이름순, 업로드순, 별점순으로 조회한다.")
    Collection<DynamicTest> findPlaylistsOrderBySort() {
        Member user = createAndSaveMember();

        for (int x = 1; x < 21; x++) {
            List<Video> videos = new ArrayList<>();

            Member member = createAndSaveMember();
            Channel channel = createAndSaveChannelWithName(member, generateRandomString());
            Video video = createAndSaveVideoWithRandomStarAndName(channel, generateRandomString());

            videos.add(video);

            createAndSaveOrderComplete(user, videos);
        }

        em.flush();
        em.clear();

        int page = 1, size = 16;
        Pageable pageable = PageRequest.of(page - 1, size);

        return List.of(
            dynamicTest("sort를 name으로 하면 비디오의 영상 이름순으로 조회된다.",
                () ->
                {
                    Page<Video> result = memberRepository.findPlaylistsOrderBySort(user.getMemberId(), pageable, "name");

                    List<Video> content = result.getContent();
                    for (int i = 1; i < content.size(); i++) {
                        Video current = content.get(i);
                        Video previous = content.get(i - 1);
                        assertTrue(current.getVideoName().compareTo(previous.getVideoName()) >= 0);
                    }
                }
            ),
            // dynamicTest("sort를 channel로 하면 비디오들을 채널별로 묶어서 채널의 이름순으로 조회된다.",
            //     () ->
            //     {
            //         Page<Video> result = memberRepository.findPlaylistsOrderBySort(user.getMemberId(), pageable, "channel");
            //
            //         List<Video> content = result.getContent();
            //         for (int i = 1; i < content.size(); i++) {
            //             Video current = content.get(i);
            //             Video previous = content.get(i - 1);
            //             assertTrue(
            //                 current.getChannel().getChannelName()
            //                     .compareTo(previous.getChannel().getChannelName()) >= 0);
            //         }
            //     }
            // ),
            dynamicTest("sort를 star로 하면 비디오들이 별점이 높은순으로 조회된다.",
                () ->
                {
                    Page<Video> result = memberRepository.findPlaylistsOrderBySort(user.getMemberId(), pageable, "star");

                    List<Video> content = result.getContent();
                    for (int i = 0; i < content.size() - 1; i++) {
                        Video current = content.get(i);
                        Video previous = content.get(i + 1);
                        assertTrue(current.getStar() >= previous.getStar());
                    }
                }
            ),
            dynamicTest("sort를 createdDate로 하면 비디오들이 가장 최근에 업로드한 순으로 조회된다.",
                () ->
                {
                    Page<Video> result = memberRepository.findPlaylistsOrderBySort(user.getMemberId(), pageable, "createdDate");

                    assertThat(result.getContent()).isSortedAccordingTo(
                        Comparator.comparing(Video::getCreatedDate).reversed()
                    );
                }
            )
        );
    }

    @TestFactory
    @DisplayName("회원의 시청 기록을 지정한 날짜의 범위에 한해 조회한다.")
    Collection<DynamicTest> findWatchesForMember() {
        Member user = createAndSaveMember();
        Long memberId = user.getMemberId();

        for (int i = 0; i < 20; i++) {
            String name = generateRandomString();

            Member member = createAndSaveMember();
            Channel channel = createAndSaveChannelWithName(member, name);
            Video video = createAndSaveVideo(channel);

            if(i < 10) {
                createAndSaveWatch(user, video); // 범위에 해당 하는 시청 기록
            } else {
                createAndSaveWatchWithTime(user, video, 9); // 해당 하지 않는 시청 기록
            }
        }

        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime startDateTime = endDateTime.minusDays(7);

        int page = 1, size = 20; //16
        Pageable pageable = PageRequest.of(page - 1, size);

        return List.of(
            dynamicTest(
                "지정한 날짜의 범위에 한해서 시청 기록이 조회 되는지 테스트",
                () -> {
                    Page<Watch> result = memberRepository.findWatchesForMember(user.getMemberId(), pageable, 7);

                    assertThat(result.getTotalElements()).isEqualTo(10);
                }
            ),
            dynamicTest(
                "지정한 날짜의 범위에 한해서 시청 기록이 조회 조건이 있는지 쿼리문 테스트",
                () -> {
                    queryFactory = new JPAQueryFactory(em);

                    String expectedWhereClause = "where watch.member.memberId = ?1 " +
                        "and watch.modifiedDate between ?2 and ?3";

                    JPAQuery<Watch> query = queryFactory
                        .selectFrom(watch)
                        .leftJoin(watch.video, video).fetchJoin()
                        .leftJoin(video.channel, channel).fetchJoin()
                        .where(
                            watch.member.memberId.eq(memberId)
                                .and(watch.modifiedDate.between(startDateTime, endDateTime))
                        )
                        .orderBy(watch.modifiedDate.desc());

                    String queryString = query.toString();

                    assertThat(queryString).contains(expectedWhereClause);
                }
            )
        );
    }

    @TestFactory
    @DisplayName("플레이리스트 채널별 그룹화 테스트")
    Collection<DynamicTest> findPlaylistGroupByChannelName() {
        //given
        Member user = createAndSaveMember();
        Member member1 = createAndSaveMember();
        Member member2 = createAndSaveMember();
        Channel channel1 = createAndSaveChannelWithName(member1, "1aaaaaaaaa");
        Channel channel2 = createAndSaveChannelWithName(member2, "0aaaaaaaa");
        createAndSaveVideo(channel1);
        createAndSaveVideo(channel1);
        createAndSaveVideo(channel1);
        createAndSaveVideo(channel1);
        createAndSaveSubscribe(user, channel1);

        for (int x = 1; x < 21; x++) {
            List<Video> videos = new ArrayList<>();

            Channel channel;
            Video video;

            Member member = createAndSaveMember();
            if (x < 5) {
                video = createAndSaveVideo(channel1);
            } else if (x > 4 && x < 10) {
                video = createAndSaveVideo(channel2);
            } else {
                channel = createAndSaveChannelWithName(member, generateRandomString());
                video = createAndSaveVideo(channel);
            }

            videos.add(video);

            createAndSaveOrderComplete(user, videos);
        }

        em.flush();
        em.clear();

        //when
        return List.of(
            dynamicTest(
                "채널명 순으로 정렬 되는지 테스트",
                () -> {
                    Page<Tuple> channels =
                        memberRepository.findPlaylistGroupByChannelName(
                            user.getMemberId(), PageRequest.of(0, 10)
                        );

                    Comparator<Tuple> comparator = Comparator.comparing(tuple -> tuple.get(1, String.class));
                    List<Tuple> sortedTuples = channels.get().sorted(comparator).collect(Collectors.toList());

                    assertThat(sortedTuples).isEqualTo(channels.getContent());
                }
            ),
            dynamicTest(
                "한 채널의 비디오를 여러개 구매한 경우 그룹화 되서 조회되는지 테스트",
                () -> {
                    Page<Tuple> channels =
                        memberRepository.findPlaylistGroupByChannelName(
                            user.getMemberId(), PageRequest.of(0, 20)
                        );

                    // 중복된 채널만큼 총 비디오의 개수 20개에서 마이너스 되서 13이 조회되야 함

                    assertThat(channels.getTotalElements()).isEqualTo(13);
                }
            )
        );
    }

    @TestFactory
    @DisplayName("특정 채널의 비디오 중에서 회원이 구매한 비디오만 조회")
    Collection<DynamicTest> findPlaylistChannelDetails() {
        //given
        Member user = createAndSaveMember();
        Member member1 = createAndSaveMember();
        Member member2 = createAndSaveMember();
        Channel channel1 = createAndSaveChannelWithName(member1, "1aaaaaaaaa");
        Channel channel2 = createAndSaveChannelWithName(member2, "0aaaaaaaa");
        createAndSaveVideoWithName(channel1, generateRandomString());
        createAndSaveVideoWithName(channel1, generateRandomString());
        createAndSaveVideoWithName(channel1, generateRandomString());
        createAndSaveVideoWithName(channel1, generateRandomString());
        createAndSaveSubscribe(user, channel1);

        for (int x = 1; x < 21; x++) {
            List<Video> videos = new ArrayList<>();

            Channel channel;
            Video video;

            Member member = createAndSaveMember();
            if (x < 5) {
                video = createAndSaveVideoWithName(channel1, generateRandomString());
            } else if (x > 4 && x < 10) {
                video = createAndSaveVideoWithName(channel2, generateRandomString());
            } else {
                channel = createAndSaveChannelWithName(member, generateRandomString());
                video = createAndSaveVideoWithName(channel, generateRandomString());
            }

            videos.add(video);

            createAndSaveOrderComplete(user, videos);
        }

        em.flush();
        em.clear();

        //when
        return List.of(
            dynamicTest(
                "특정 채널의 구매한 비디오만 조회 되는지 검증하는 테스트",
                () -> {
                    Page<Video> channels =
                        memberRepository.findPlaylistChannelDetails(
                            user.getMemberId(), member1.getMemberId()
                        );

                    assertThat(channels.getContent().get(0).getChannel().getMember().getMemberId())
                        .isEqualTo(channel1.getMember().getMemberId());

                    assertThat(channels.getContent().get(0).getChannel().getMember().getMemberId())
                        .isNotEqualTo(channel2.getMember().getMemberId());
                }
            ),
            dynamicTest(
                "조회한 비디오가 비디오의 이름순으로 정렬되었는지 검증하는 테스트",
                () -> {
                    Page<Video> channels =
                        memberRepository.findPlaylistChannelDetails(
                            user.getMemberId(), member1.getMemberId()
                        );

                    assertThat(channels.getContent()).isSortedAccordingTo(
                        Comparator.comparing(Video::getVideoName)
                    );
                }
            )
        );
    }

    // @TestFactory
    // @DisplayName("채널 혹은 다른 것들과 연관된 멤버의 삭제 테스트")
    Collection<DynamicTest> deleteMemberWithAll() {
        //1
        Member user = createAndSaveMember();
        Channel channel = createAndSaveChannelWithName(user, "1aaaaaaaaa");
        //2
        Member user2 = createAndSaveMember();
        Channel channel2 = createAndSaveChannelWithName(user2, "1aaaaaaaaa");
        Video video1 = createAndSaveVideo(channel2);
        Video video2 = createAndSaveVideo(channel2);
        //3
        Member user3 = createAndSaveMember();
        Channel channel3 = createAndSaveChannelWithName(user3, "1aaaaaaaaa");
        Video video3 = createAndSaveVideo(channel2);
        Video video4 = createAndSaveVideo(channel2);
        Member subscriber1 = createAndSaveMember();
        createAndSaveSubscribe(subscriber1, channel3);
        //4
        Member user4 = createAndSaveMember();
        Channel channel4 = createAndSaveChannelWithName(user4, "1aaaaaaaaa");
        Video video5 = createAndSaveVideo(channel2);
        Video video6 = createAndSaveVideo(channel2);
        Member subscriber2 = createAndSaveMember();
        Channel channel5 = createAndSaveChannelWithName(subscriber2, "1aaaaaaaaa");
        createAndSaveSubscribe(subscriber2, channel4);
        createAndSaveSubscribe(user4, channel5);

        em.flush();
        em.clear();

        //when
        return List.of(
            dynamicTest(
                "회원이 채널만 가지고 있는 경우 회원과 채널 모두 같이 삭제되는지 테스트",
                () -> {
                    memberRepository.delete(user);
                    em.flush();
                    em.clear();

                    assertThrows(MemberNotFoundException.class,
                        () -> memberRepository.findById(user.getMemberId()).orElseThrow(MemberNotFoundException::new));

                    assertThrows(ChannelNotFoundException.class,
                        () -> channelRepository.findById(channel.getChannelId()).orElseThrow(ChannelNotFoundException::new));
                }
            ),
            dynamicTest(
                "회원이 채널을 가지고 있고 채널에 비디오를 업로드한 경우 셋 모두 삭제되지 않고 회원과 채널만 삭제",
                () -> {
                    memberRepository.delete(user2);
                    em.flush();
                    em.clear();

                    assertThrows(MemberNotFoundException.class,
                        () -> memberRepository.findById(user2.getMemberId()).orElseThrow(MemberNotFoundException::new));

                    assertThrows(ChannelNotFoundException.class,
                        () -> channelRepository.findById(channel2.getChannelId()).orElseThrow(ChannelNotFoundException::new));

                    assertDoesNotThrow(() -> videoRepository.findById(video1.getVideoId()).orElseThrow(VideoNotFoundException::new));
                    assertDoesNotThrow(() -> videoRepository.findById(video2.getVideoId()).orElseThrow(VideoNotFoundException::new));
                }
            ),
            dynamicTest(
                "회원 + 채널 + 비디오 + 구독자가 있는 경우",
                () -> {
                    Member member = memberRepository.findById(subscriber1.getMemberId()).orElseThrow();

                    assertThat(channelRepository.findById(channel3.getChannelId()).orElseThrow().getSubscribes().size())
                        .isEqualTo(1);

                    memberRepository.delete(user3);
                    em.flush();
                    em.clear();

                    assertThrows(MemberNotFoundException.class,
                        () -> memberRepository.findById(user3.getMemberId()).orElseThrow(MemberNotFoundException::new));

                    assertThrows(ChannelNotFoundException.class,
                        () -> channelRepository.findById(channel3.getChannelId()).orElseThrow(ChannelNotFoundException::new));

                    assertDoesNotThrow(() -> videoRepository.findById(video3.getVideoId()).orElseThrow(VideoNotFoundException::new));
                    assertDoesNotThrow(() -> videoRepository.findById(video4.getVideoId()).orElseThrow(VideoNotFoundException::new));

                    // 탈퇴한 회원의 채널을 구독한 사람의 구독이 취소되지 않고 남아있음
                    assertThat(memberRepository.findById(member.getMemberId()).orElseThrow().getSubscribes().size()).isEqualTo(1);
                }
            ),
            dynamicTest(
                "회원 + 채널 + 비디오 + 구독자가 있고 자기도 구독한게 있는 경우",
                () -> {
                    Member member = memberRepository.findById(subscriber2.getMemberId()).orElseThrow();
                    List<Subscribe> user4Subscribes = memberRepository.findById(user4.getMemberId()).orElseThrow().getSubscribes();

                    assertThat(channelRepository.findById(channel4.getChannelId()).orElseThrow().getSubscribes().size())
                        .isEqualTo(1);
                    assertThat(user4Subscribes.size()).isEqualTo(1);

                    memberRepository.delete(user4);
                    em.flush();
                    em.clear();

                    assertThrows(MemberNotFoundException.class,
                        () -> memberRepository.findById(user4.getMemberId()).orElseThrow(MemberNotFoundException::new));

                    assertThrows(ChannelNotFoundException.class,
                        () -> channelRepository.findById(channel4.getChannelId()).orElseThrow(ChannelNotFoundException::new));

                    assertDoesNotThrow(() -> videoRepository.findById(video5.getVideoId()).orElseThrow(VideoNotFoundException::new));
                    assertDoesNotThrow(() -> videoRepository.findById(video6.getVideoId()).orElseThrow(VideoNotFoundException::new));

                    assertThat(memberRepository.findById(member.getMemberId()).orElseThrow().getSubscribes().size()).isEqualTo(1);
                    assertThat(channelRepository.findById(member.getChannel().getChannelId()).orElseThrow().getSubscribes().size()).isEqualTo(1);
                }
            )
        );
    }

    protected Channel createAndSaveChannelWithName(Member member, String channelName) {
        Channel channel = Channel.createChannel(channelName);
        channel.setMember(member);
        em.persist(channel);

        return channel;
    }

    protected Member createAndSaveMember() {
        Member member = Member.builder()
            .email("test@gmail.com")
            .password("1q2w3e4r!")
            .nickname("test")
            .authority(Authority.ROLE_USER)
            .reward(1000)
            .imageFile("imageFile")
            .build();

        em.persist(member);

        return member;
    }

    private String generateRandomString() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder(10);
        Random random = new SecureRandom();

        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            randomString.append(randomChar);
        }

        return randomString.toString();
    }

    private Watch createAndSaveWatch(Member loginMember, Video video) {
        Watch watch = Watch.createWatch(loginMember, video);
        em.persist(watch);

        return watch;
    }

    private Watch createAndSaveWatchWithTime(Member loginMember, Video video, int days) {
        Watch watch = Watch.createWatch(loginMember, video);
        em.persist(watch);
        watch.setLastWatchedTime(LocalDateTime.now().minusDays(days));
        return watch;
    }
}