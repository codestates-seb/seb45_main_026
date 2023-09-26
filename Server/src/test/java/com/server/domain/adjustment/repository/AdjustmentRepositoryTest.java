package com.server.domain.adjustment.repository;

import com.server.domain.account.domain.Account;
import com.server.domain.adjustment.domain.Adjustment;
import com.server.domain.adjustment.domain.AdjustmentStatus;
import com.server.domain.adjustment.repository.dto.AdjustmentData;
import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.order.repository.OrderRepository;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
}