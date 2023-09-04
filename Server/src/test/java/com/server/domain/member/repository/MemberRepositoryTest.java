package com.server.domain.member.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.cart.entity.Cart;
import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.dto.MemberVideoData;
import com.server.domain.member.service.dto.response.WatchsResponse;
import com.server.domain.order.entity.Order;
import com.server.domain.video.entity.Video;
import com.server.domain.watch.entity.Watch;
import com.server.global.testhelper.RepositoryTest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static com.server.domain.channel.entity.QChannel.*;
import static com.server.domain.video.entity.QVideo.*;
import static com.server.domain.watch.entity.QWatch.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

import javax.persistence.PersistenceContext;

class MemberRepositoryTest extends RepositoryTest {

    @Autowired MemberRepository memberRepository;
    private JPAQueryFactory queryFactory;

    @Test
    @DisplayName("회원이 비디오를 구매한 적이 있는지 확인한다.")
    void checkMemberPurchaseVideo() {
        // given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);
        Order order = createAndSaveOrder(member, List.of(video));
        order.completeOrder();
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

        order.deleteOrder();

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
            dynamicTest("sort를 channel로 하면 비디오들을 채널별로 묶어서 채널의 이름순으로 조회된다.",
                () ->
                {
                    Page<Video> result = memberRepository.findPlaylistsOrderBySort(user.getMemberId(), pageable, "channel");

                    List<Video> content = result.getContent();
                    for (int i = 1; i < content.size(); i++) {
                        Video current = content.get(i);
                        Video previous = content.get(i - 1);
						assertTrue(
							current.getChannel().getChannelName()
                                .compareTo(previous.getChannel().getChannelName()) >= 0);
                    }
                }
            ),
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

    // @TestFactory
    // @DisplayName("테스트 할 내용 요약")
    // Collection<DynamicTest> template() {
    //     //given
    //
    //     //when
    //     return List.of(
    //         dynamicTest(
    //             "",
    //             () -> {
    //                 //then
    //             }
    //         )
    //     );
    // }

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