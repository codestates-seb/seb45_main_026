package com.server.domain.adjustment.service;

import com.server.domain.account.domain.Account;
import com.server.domain.account.repository.AccountRepository;
import com.server.domain.adjustment.domain.Adjustment;
import com.server.domain.adjustment.domain.AdjustmentStatus;
import com.server.domain.adjustment.repository.AdjustmentRepository;
import com.server.domain.adjustment.repository.dto.AdjustmentData;
import com.server.domain.adjustment.service.dto.request.AccountUpdateServiceRequest;
import com.server.domain.adjustment.service.dto.response.AccountResponse;
import com.server.domain.adjustment.service.dto.response.AdjustmentResponse;
import com.server.domain.adjustment.service.dto.response.MonthAdjustmentResponse;
import com.server.domain.adjustment.service.dto.response.ToTalAdjustmentResponse;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AdjustmentService {

    private final AdjustmentRepository adjustmentRepository;
    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;

    public AdjustmentService(AdjustmentRepository adjustmentRepository,
                             AccountRepository accountRepository, MemberRepository memberRepository) {
        this.adjustmentRepository = adjustmentRepository;
        this.accountRepository = accountRepository;
        this.memberRepository = memberRepository;
    }

    public Page<AdjustmentResponse> adjustment(Long loginMemberId, int page, int size, Integer month, Integer year, String sort) {

        Pageable pageable = PageRequest.of(page, size);

        Page<AdjustmentData> datas = adjustmentRepository.findByPeriod(loginMemberId, pageable, month, year, sort);

        return datas.map(AdjustmentResponse::of);
    }

    public Integer calculateAmount(Long loginMemberId, Integer month, Integer year) {

        return adjustmentRepository.calculateAmount(loginMemberId, month, year);
    }

    public ToTalAdjustmentResponse totalAdjustment(Long loginMemberId, Integer month, Integer year) {

        List<Adjustment> monthlyData = adjustmentRepository.findMonthlyData(loginMemberId, year);

        List<MonthAdjustmentResponse> monthAdjustmentResponses = monthlyData.stream()
                .map(MonthAdjustmentResponse::of)
                .collect(Collectors.toList());

        //최근 2달 데이터 넣기
        addCurrentMonthData(monthAdjustmentResponses);

        //필요한 데이터 세팅
        return getTotalAdjustment(monthAdjustmentResponses, monthlyData, month, year);
    }

    private void addCurrentMonthData(List<MonthAdjustmentResponse> monthAdjustmentResponses) {

        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        //이번달 데이터 세팅
        int amount = adjustmentRepository.calculateAmount(1L, month, year);
        monthAdjustmentResponses.add(MonthAdjustmentResponse.of(month, year, amount));

        //지난달 데이터가 있는지 확인
        if (month == 1) {
            month = 12;
            year -= 1;
        } else {
            month -= 1;
        }

        boolean hasLastMonth = false;

        for(MonthAdjustmentResponse monthAdjustmentResponse : monthAdjustmentResponses) {
            if (monthAdjustmentResponse.isSameMonthAndYear(month, year)) {
                hasLastMonth = true;
                break;
            }
        }

        //지난달 데이터가 없다면 세팅
        if (!hasLastMonth) {
            amount = adjustmentRepository.calculateAmount(1L, month, year);
            monthAdjustmentResponses.add(MonthAdjustmentResponse.of(month, year, amount));
        }
    }

    private ToTalAdjustmentResponse getTotalAdjustment(List<MonthAdjustmentResponse> monthAdjustmentResponses,
                                                       List<Adjustment> monthlyData,
                                                       Integer month,
                                                       Integer year) {

        int amount = 0;
        AdjustmentStatus status = AdjustmentStatus.NO_ADJUSTMENT;
        String reason = null;

        if(year == null) {

            amount = adjustmentRepository.calculateAmount(1L, null, null);
            status = AdjustmentStatus.TOTAL;

            return ToTalAdjustmentResponse.of(amount, status, reason, monthAdjustmentResponses);
        }

        if(month == null) {

            amount = adjustmentRepository.calculateAmount(1L, null, year);
            status = AdjustmentStatus.TOTAL;

            return ToTalAdjustmentResponse.of(amount, status, reason, monthAdjustmentResponses);
        }

        for(MonthAdjustmentResponse monthAdjustmentResponse : monthAdjustmentResponses) {
            if (monthAdjustmentResponse.isSameMonthAndYear(month, year)) {
                amount = monthAdjustmentResponse.getAmount();
                status = AdjustmentStatus.NOT_ADJUSTED;
            }
        }

        for(Adjustment adjustment : monthlyData) {
            if (adjustment.isSameMonthAndYear(month, year)) {
                status = adjustment.getAdjustmentStatus();
                reason = adjustment.getReason() == null ? status.getDescription() : adjustment.getReason();
            }
        }

        return ToTalAdjustmentResponse.of(amount, status, reason, monthAdjustmentResponses);
    }

    public AccountResponse getAccount(Long loginMemberId) {

        Account account = getAccountOrNull(loginMemberId);

        return AccountResponse.of(account);
    }

    public void updateAccount(Long loginMemberId, AccountUpdateServiceRequest request) {

        Account account = getAccountOrNull(loginMemberId);

        if(account == null) {
            Member member = verifiedMember(loginMemberId);
            Account createdAccount = Account.createAccount(request.getName(), request.getAccount(), request.getBank(), member);
            accountRepository.save(createdAccount);
        }else {
            account.updateAccount(request.getName(), request.getAccount(), request.getBank());
        }

    }

    private Account getAccountOrNull(Long loginMemberId) {
        return accountRepository.findByMemberId(loginMemberId).orElse(null);
    }

    private Member verifiedMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }
}
