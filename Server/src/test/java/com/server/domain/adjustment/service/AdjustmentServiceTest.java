package com.server.domain.adjustment.service;

import com.server.domain.account.domain.Account;
import com.server.domain.account.domain.Bank;
import com.server.domain.adjustment.service.dto.request.AccountUpdateServiceRequest;
import com.server.domain.member.entity.Member;
import com.server.global.testhelper.ServiceTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

class AdjustmentServiceTest extends ServiceTest {

    @Autowired AdjustmentService adjustmentService;

    @TestFactory
    @DisplayName("계좌 정보를 수정한다.")
    Collection<DynamicTest> updateAccount() {
        //given
        Member member = createMemberWithChannel();

        em.flush();
        em.clear();

        return List.of(
            dynamicTest("계좌정보가 없다면 생성한다.", ()-> {
                //given
                String accountNumber = "123-12-123456";
                Bank bank = Bank.SH;
                String name = "홍길동";

                AccountUpdateServiceRequest request = new AccountUpdateServiceRequest(name, accountNumber, bank);

                //when
                adjustmentService.updateAccount(member.getMemberId(), request);

                //then
                Account account = accountRepository.findByMemberId(member.getMemberId()).orElseThrow();
                assertThat(account.getAccount()).isEqualTo(accountNumber);
            }),
                dynamicTest("계좌정보가 있다면 수정한다.", ()-> {
                    //given
                    String accountNumber = "123-12-123456";
                    Bank bank = Bank.SH;
                    String name = "홍길동2";

                    AccountUpdateServiceRequest request = new AccountUpdateServiceRequest(name, accountNumber, bank);

                    //when
                    adjustmentService.updateAccount(member.getMemberId(), request);

                    //then
                    Account account = accountRepository.findByMemberId(member.getMemberId()).orElseThrow();
                    assertThat(account.getAccount()).isEqualTo(accountNumber);
                    assertThat(accountRepository.findAll().size()).isEqualTo(1);
                })
        );


    }
}