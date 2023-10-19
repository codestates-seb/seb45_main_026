package com.server.domain.adjustment.repository;

import com.server.domain.account.domain.Account;
import com.server.domain.adjustment.domain.Adjustment;
import com.server.domain.adjustment.domain.AdjustmentStatus;
import com.server.domain.adjustment.repository.dto.AdjustmentData;
import com.server.domain.adjustment.repository.dto.VideoAdjustmentData;
import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.order.entity.OrderVideo;
import com.server.domain.order.repository.OrderRepository;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.RepositoryTest;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.*;
import static org.junit.jupiter.api.DynamicTest.*;

class AdjustmentRepositoryTest extends RepositoryTest {

    @Autowired private AdjustmentRepository adjustmentRepository;

    @Test
    @DisplayName("기간 내 정산 결과를 얻는다.")
    void findByPeriod() {
        //given
        Member owner = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());

        Member member1 = createMemberWithChannel();
        Member member2 = createMemberWithChannel();

        Order order1 = createAndSaveOrderComplete(member1, List.of(video1, video2));
        Order order2 = createAndSaveOrderComplete(member2, List.of(video1, video2));

        order1.cancelVideoOrder(order1.getOrderVideos().get(0));

        LocalDateTime now = LocalDateTime.now();
        int month = now.getMonthValue();
        int year = now.getYear();
        Pageable pageable = PageRequest.of(0, 10);

        int totalSaleAmount = video1.getPrice() + video2.getPrice();
        String sort = "sellingPrice";

        //when
        Page<AdjustmentData> adjustment = adjustmentRepository.findByPeriod(owner.getMemberId(), pageable, month, year, sort);

        //then
        assertThat(adjustment.getContent()).hasSize(2)
                .extracting("videoName")
                .containsExactly(video1.getVideoName(), video2.getVideoName());

        assertThat(adjustment.getContent()).extracting("totalSaleAmount")
                .containsExactly(totalSaleAmount, totalSaleAmount);

        assertThat(adjustment.getContent()).extracting("refundAmount")
                .containsExactlyInAnyOrder(0, video1.getPrice());
    }

    @Test
    @DisplayName("해당 월의 총 판매금액을 얻는다.")
    void calculateAmount() {
        //given
        Member owner = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner.getChannel());
        Video video2 = createAndSaveVideo(owner.getChannel());

        Member member1 = createMemberWithChannel();
        Member member2 = createMemberWithChannel();

        Order order1 = createAndSaveOrderComplete(member1, List.of(video1, video2));
        Order order2 = createAndSaveOrderComplete(member2, List.of(video1, video2));

        order1.cancelVideoOrder(order1.getOrderVideos().get(0));

        LocalDateTime now = LocalDateTime.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        int totalSaleAmount = video1.getPrice() * 2 + video2.getPrice();

        //when
        Integer total = adjustmentRepository.calculateAmount(owner.getMemberId(), month, year);

        //then
        assertThat(total).isEqualTo(totalSaleAmount);
    }

    @Test
    @DisplayName("해당 연도의 정산 데이터를 얻는다.")
    void findMonthlyData() {
        //given
        Member member = createMemberWithChannel();
        Account account = createAndSaveAccount(member);

        Adjustment adjustment1 = createAndSaveAdjustment(member, 2023, 1);
        Adjustment adjustment2 = createAndSaveAdjustment(member, 2023, 2);
        Adjustment adjustment3 = createAndSaveAdjustment(member, 2023, 3);
        Adjustment adjustment4 = createAndSaveAdjustment(member, 2022, 1);

        //when
        List<Adjustment> adjustments = adjustmentRepository.findMonthlyData(member.getMemberId(), 2023);

        //then
        assertThat(adjustments).hasSize(3)
                .extracting("adjustmentId")
                .containsExactly(adjustment1.getAdjustmentId(), adjustment2.getAdjustmentId(), adjustment3.getAdjustmentId());
    }

    @Test
    @DisplayName("정산 데이터를 받을 때 연도를 특정하지 않으면 전체 데이터를 얻는다.")
    void findMonthlyDataTotal() {
        //given
        Member member = createMemberWithChannel();
        Account account = createAndSaveAccount(member);

        Adjustment adjustment1 = createAndSaveAdjustment(member, 2023, 1);
        Adjustment adjustment2 = createAndSaveAdjustment(member, 2023, 2);
        Adjustment adjustment3 = createAndSaveAdjustment(member, 2023, 3);
        Adjustment adjustment4 = createAndSaveAdjustment(member, 2022, 1);

        //when
        List<Adjustment> adjustments = adjustmentRepository.findMonthlyData(member.getMemberId(), null);

        //then
        assertThat(adjustments).hasSize(4)
                .extracting("adjustmentId")
                .containsExactly(adjustment1.getAdjustmentId(),
                        adjustment2.getAdjustmentId(),
                        adjustment3.getAdjustmentId(),
                        adjustment4.getAdjustmentId());

    }

    @TestFactory
    @DisplayName("해당 월, 해당 연도의 자신의 비디오 판매 금액을 모두 조회한다.")
    Collection<DynamicTest> calculateVideo() {
        //given
        Member owner1 = createMemberWithChannel();
        Video video1 = createAndSaveVideo(owner1.getChannel());
        Video video2 = createAndSaveVideo(owner1.getChannel());
        Video video3 = createAndSaveVideo(owner1.getChannel());

        Member buyer1 = createMemberWithChannel();
        buyer1.addReward(10000);
        Member buyer2 = createMemberWithChannel();
        buyer2.addReward(10000);

        Order order1 = createAndSaveOrder(buyer1, List.of(video1)); // 8월에 1 주문
        order1.completeOrder(LocalDateTime.of(2023, 8, 1, 0, 0), "paymentKey");

        Order order2 = createAndSaveOrder(buyer1, List.of(video2, video3)); //9월에 2, 3 주문하고 2 취소
        order2.completeOrder(LocalDateTime.of(2023, 9, 1, 0, 0), "paymentKey");
        OrderVideo orderVideo = order2.getOrderVideos().stream().filter(v -> v.getVideo().getVideoId().equals(video2.getVideoId())).findFirst().get();
        orderVideo.cancel();

        Order order3 = createAndSaveOrder(buyer2, List.of(video2)); // 9월에 2 주문
        order3.completeOrder(LocalDateTime.of(2023, 9, 1, 0, 0), "paymentKey");

        Order order4 = createAndSaveOrder(buyer2, List.of(video1)); // 8월에 1 주문
        order4.completeOrder(LocalDateTime.of(2023, 8, 1, 0, 0), "paymentKey");

        Order order5 = createAndSaveOrder(buyer2, List.of(video3)); // 2022 9월에 3 주문
        order5.completeOrder(LocalDateTime.of(2022, 9, 1, 0, 0), "paymentKey");

        em.flush();
        em.clear();

        return List.of(
                dynamicTest("2023년 9월 조회 시 2, 3 정산 내역이 나온다.", ()-> {
                    //given
                    Integer year = 2023;
                    Integer month = 9;

                    int video2Amount = video2.getPrice();
                    int video3Amount = video3.getPrice();

                    //when
                    List<VideoAdjustmentData> data = adjustmentRepository.calculateVideo(owner1.getMemberId(), month, year);

                    //then
                    assertThat(data).hasSize(2)
                            .extracting("videoId", "amount")
                            .containsExactlyInAnyOrder(
                                    tuple(video2.getVideoId(), video2Amount),
                                    tuple(video3.getVideoId(), video3Amount)
                            );
                }),
                dynamicTest("2023년 8월 조회 시 1 정산 내역이 나온다.", ()-> {
                    //given
                    Integer year = 2023;
                    Integer month = 8;

                    int video1Amount = video1.getPrice() * 2;

                    //when
                    List<VideoAdjustmentData> data = adjustmentRepository.calculateVideo(owner1.getMemberId(), month, year);

                    //then
                    assertThat(data).hasSize(1)
                            .extracting("videoId", "amount")
                            .containsExactlyInAnyOrder(
                                    tuple(video1.getVideoId(), video1Amount)
                            );
                }),
                dynamicTest("2023년 전체 조회 시 1, 2, 3 정산 내역이 나온다.", ()-> {
                    //given
                    Integer year = 2023;
                    Integer month = null;

                    int video1Amount = video1.getPrice() * 2;
                    int video2Amount = video2.getPrice();
                    int video3Amount = video3.getPrice();

                    //when
                    List<VideoAdjustmentData> data = adjustmentRepository.calculateVideo(owner1.getMemberId(), month, year);

                    //then
                    assertThat(data).hasSize(3)
                            .extracting("videoId", "amount")
                            .containsExactlyInAnyOrder(
                                    tuple(video1.getVideoId(), video1Amount),
                                    tuple(video2.getVideoId(), video2Amount),
                                    tuple(video3.getVideoId(), video3Amount)
                            );
                }),
                dynamicTest("2022년 전체 조회 시 3 정산 내역이 나온다.", ()-> {
                    //given
                    Integer year = 2022;
                    Integer month = null;

                    int video3Amount = video3.getPrice();

                    //when
                    List<VideoAdjustmentData> data = adjustmentRepository.calculateVideo(owner1.getMemberId(), month, year);

                    //then
                    assertThat(data).hasSize(1)
                            .extracting("videoId", "amount")
                            .containsExactlyInAnyOrder(
                                    tuple(video3.getVideoId(), video3Amount)
                            );
                }),
                dynamicTest("2022년 9월 조회 시 3 정산 내역이 나온다.", ()-> {
                    //given
                    Integer year = 2022;
                    Integer month = 9;

                    int video3Amount = video3.getPrice();

                    //when
                    List<VideoAdjustmentData> data = adjustmentRepository.calculateVideo(owner1.getMemberId(), month, year);

                    //then
                    assertThat(data).hasSize(1)
                            .extracting("videoId", "amount")
                            .containsExactlyInAnyOrder(
                                    tuple(video3.getVideoId(), video3Amount)
                            );
                }),
                dynamicTest("2023년 7월 조회 시 정산내역이 나오지 않는다.", ()-> {
                    //given
                    Integer year = 2022;
                    Integer month = 7;

                    //when
                    List<VideoAdjustmentData> data = adjustmentRepository.calculateVideo(owner1.getMemberId(), month, year);

                    //then
                    assertThat(data).hasSize(0);
                }),
                dynamicTest("전체 조회 시 1, 2, 3 의 전체 내역이 나온다.", ()-> {
                    //given
                    Integer year = null;
                    Integer month = null;

                    int video1Amount = video1.getPrice() * 2;
                    int video2Amount = video2.getPrice();
                    int video3Amount = video3.getPrice() * 2;

                    //when
                    List<VideoAdjustmentData> data = adjustmentRepository.calculateVideo(owner1.getMemberId(), month, year);

                    //then
                    assertThat(data).hasSize(3)
                            .extracting("videoId", "amount")
                            .containsExactlyInAnyOrder(
                                    tuple(video1.getVideoId(), video1Amount),
                                    tuple(video2.getVideoId(), video2Amount),
                                    tuple(video3.getVideoId(), video3Amount)
                            );
                })












        );

    }
}