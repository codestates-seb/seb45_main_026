package com.server.domain.adjustment.domain;

import com.server.domain.account.domain.Bank;
import com.server.domain.member.entity.Member;
import com.server.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Adjustment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adjustmentId;

    private Integer adjustmentYear;

    private Integer adjustmentMonth;

    private String name;

    private String account;

    private Bank bank;

    private Integer amount;

    @Enumerated(EnumType.STRING)
    private AdjustmentStatus adjustmentStatus;

    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Adjustment(Integer year, Integer month, String name, String account, Bank bank, Integer amount, AdjustmentStatus adjustmentStatus, String reason, Member member) {
        this.adjustmentYear = year;
        this.adjustmentMonth = month;
        this.name = name;
        this.account = account;
        this.bank = bank;
        this.amount = amount;
        this.adjustmentStatus = adjustmentStatus;
        this.reason = reason;
        this.member = member;
    }

    public static Adjustment createAdjustment(Integer year,
                                              Integer month,
                                              Member member,
                                              Integer amount,
                                              AdjustmentStatus adjustmentStatus,
                                              String reason) {
        return new Adjustment(
                year,
                month,
                member.getAccount().getName(),
                member.getAccount().getAccount(),
                member.getAccount().getBank(),
                amount,
                adjustmentStatus,
                reason,
                member
        );
    }

    public boolean isSameMonthAndYear(Integer year, Integer month) {
        return this.adjustmentYear.equals(year) && this.adjustmentMonth.equals(month);
    }
}
