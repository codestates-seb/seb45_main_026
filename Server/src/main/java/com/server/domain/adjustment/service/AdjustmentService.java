package com.server.domain.adjustment.service;

import com.server.domain.account.domain.Account;
import com.server.domain.account.repository.AccountRepository;
import com.server.domain.adjustment.domain.Adjustment;
import com.server.domain.adjustment.domain.AdjustmentStatus;
import com.server.domain.adjustment.repository.AdjustmentRepository;
import com.server.domain.adjustment.repository.dto.AdjustmentData;
import com.server.domain.adjustment.repository.dto.VideoAdjustmentData;
import com.server.domain.adjustment.service.dto.request.AccountUpdateServiceRequest;
import com.server.domain.adjustment.service.dto.response.*;
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

    public List<MonthAdjustmentResponse> totalAdjustment(Long loginMemberId, Integer year) {

        List<Adjustment> monthlyData = adjustmentRepository.findMonthlyData(loginMemberId, year);

        List<MonthAdjustmentResponse> monthAdjustmentResponses = monthlyData.stream()
                .map(MonthAdjustmentResponse::of)
                .collect(Collectors.toList());

        //없는 데이터 넣기
        addAdditionalMonthData(monthAdjustmentResponses, year);

        return monthAdjustmentResponses;
    }

    public AccountResponse getAccount(Long loginMemberId) {

        Account account = getAccountOrNull(loginMemberId);

        return AccountResponse.of(account);
    }

    @Transactional
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

    public List<VideoAdjustmentResponse> calculateVideoRate(Long loginMemberId, Integer month, Integer year) {

        List<VideoAdjustmentData> datas = adjustmentRepository.calculateVideo(loginMemberId, month, year);

        int total = datas.stream().map(VideoAdjustmentData::getAmount).mapToInt(Integer::intValue).sum();

        return datas.stream().map(data -> VideoAdjustmentResponse.of(data, total)).collect(Collectors.toList());
    }

    private Account getAccountOrNull(Long loginMemberId) {
        return accountRepository.findByMemberId(loginMemberId).orElse(null);
    }

    private void addAdditionalMonthData(List<MonthAdjustmentResponse> monthAdjustmentResponses, Integer year) {

        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        if(year == null) {

            //이번달 데이터 세팅
            int amount = adjustmentRepository.calculateAmount(1L, currentMonth, currentYear);
            monthAdjustmentResponses.add(MonthAdjustmentResponse.of(currentYear, currentMonth, amount, AdjustmentStatus.NOT_ADJUSTED));

            //지난달 데이터가 있는지 확인
            if (currentMonth == 1) {
                currentMonth = 12;
                currentYear -= 1;
            } else {
                currentMonth -= 1;
            }

            boolean hasLastMonth = false;

            for(MonthAdjustmentResponse monthAdjustmentResponse : monthAdjustmentResponses) {
                if (monthAdjustmentResponse.isSameMonthAndYear(currentYear, currentMonth)) {
                    hasLastMonth = true;
                    break;
                }
            }

            //지난달 데이터가 없다면 세팅
            if (!hasLastMonth) {
                amount = adjustmentRepository.calculateAmount(1L, currentMonth, currentYear);
                monthAdjustmentResponses.add(MonthAdjustmentResponse.of(currentYear, currentMonth, amount, AdjustmentStatus.NOT_ADJUSTED));
            }

            //2023년부터 빈 데이터를 확인해서 0원으로 넣어주기
            for(int i = 2023; i <= currentYear; i++) {
                for(int j = 1; j <= 12; j++) {
                    boolean hasData = false;
                    for(MonthAdjustmentResponse monthAdjustmentResponse : monthAdjustmentResponses) {
                        if (monthAdjustmentResponse.isSameMonthAndYear(i, j)) {
                            hasData = true;
                            break;
                        }
                    }
                    if(!hasData) {
                        monthAdjustmentResponses.add(MonthAdjustmentResponse.of(i, j, 0, AdjustmentStatus.NO_ADJUSTMENT));
                    }
                }
            }
        }else if(year.equals(currentYear)) {

            //이번달 데이터 세팅
            int amount = adjustmentRepository.calculateAmount(1L, currentMonth, currentYear);
            monthAdjustmentResponses.add(MonthAdjustmentResponse.of(currentYear, currentMonth, amount, AdjustmentStatus.NOT_ADJUSTED));

            boolean hasLastMonth = false;

            //지난달 데이터가 올해인지 확인
            if (currentMonth == 1) {
                hasLastMonth = true;
            } else {
                currentMonth -= 1;
            }

            if(!hasLastMonth) {

                for(MonthAdjustmentResponse monthAdjustmentResponse : monthAdjustmentResponses) {
                    if (monthAdjustmentResponse.isSameMonthAndYear(currentYear, currentMonth)) {
                        hasLastMonth = true;
                        break;
                    }
                }
            }

            //지난달 데이터가 없다면 세팅
            if (!hasLastMonth) {
                amount = adjustmentRepository.calculateAmount(1L, currentMonth, currentYear);
                monthAdjustmentResponses.add(MonthAdjustmentResponse.of(currentYear, currentMonth, amount, AdjustmentStatus.NOT_ADJUSTED));
            }

            //빈 데이터를 확인해서 0원으로 넣어주기
            for (int j = 1; j <= 12; j++) {
                boolean hasData = false;
                for (MonthAdjustmentResponse monthAdjustmentResponse : monthAdjustmentResponses) {
                    if (monthAdjustmentResponse.isSameMonthAndYear(year, j)) {
                        hasData = true;
                        break;
                    }
                }
                if (!hasData) {
                    monthAdjustmentResponses.add(MonthAdjustmentResponse.of(year, j, 0, AdjustmentStatus.NO_ADJUSTMENT));
                }
            }
        }else {
            //현재 월이 1월이라면 지난달 12월 데이터 넣어주기
            if(currentMonth == 1 && currentYear == year + 1) {

                boolean hasLastMonth = false;

                for(MonthAdjustmentResponse monthAdjustmentResponse : monthAdjustmentResponses) {
                    if (monthAdjustmentResponse.isSameMonthAndYear(year, 12)) {
                        hasLastMonth = true;
                        break;
                    }
                }

                //지난달 데이터가 없다면 세팅
                if (!hasLastMonth) {
                    int amount = adjustmentRepository.calculateAmount(1L, 12, year);
                    monthAdjustmentResponses.add(MonthAdjustmentResponse.of(year, 12, amount, AdjustmentStatus.NOT_ADJUSTED));
                }
            }

            //빈 데이터를 확인해서 0원으로 넣어주기
            for (int j = 1; j <= 12; j++) {
                boolean hasData = false;
                for (MonthAdjustmentResponse monthAdjustmentResponse : monthAdjustmentResponses) {
                    if (monthAdjustmentResponse.isSameMonthAndYear(year, j)) {
                        hasData = true;
                        break;
                    }
                }
                if (!hasData) {
                    monthAdjustmentResponses.add(MonthAdjustmentResponse.of(year, j, 0, AdjustmentStatus.NO_ADJUSTMENT));
                }
            }
        }


    }

    private Member verifiedMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }
}
