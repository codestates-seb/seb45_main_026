package com.server.domain.account.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class BankTest {

    @TestFactory
    @DisplayName("계좌번호 유효성 검사")
    Collection<DynamicTest> checkAccount() {
        return List.of(
                dynamicTest("(구) KB 국민은행", () -> {
                    //given
                    String account = "123-12-1234-123";

                    //when
                    boolean result = Bank.KB.checkAccount(account);

                    //then
                    assertThat(result).isTrue();
                }),
                dynamicTest("(신) KB 국민은행", () -> {
                    //given
                    String account = "123456-12-123456";

                    //when
                    boolean result = Bank.KB.checkAccount(account);

                    //then
                    assertThat(result).isTrue();
                }),
                dynamicTest("IBK", () -> {
                    //given
                    String account = "123-123456-12-123";

                    //when
                    boolean result = Bank.IBK.checkAccount(account);

                    //then
                    assertThat(result).isTrue();
                }),
                dynamicTest("농협", () -> {
                    //given
                    String account = "123-1234-1234-12";

                    //when
                    boolean result = Bank.NH.checkAccount(account);

                    //then
                    assertThat(result).isTrue();
                }),
                dynamicTest("(구) 신한은행", () -> {
                    //given
                    String account = "123-12-123456";

                    //when
                    boolean result = Bank.SH.checkAccount(account);

                    //then
                    assertThat(result).isTrue();
                }),
                dynamicTest("(신) 신한은행", () -> {
                    //given
                    String account = "123-123-123456";

                    //when
                    boolean result = Bank.SH.checkAccount(account);

                    //then
                    assertThat(result).isTrue();
                }),
                dynamicTest("우리은행", () -> {
                    //given
                    String account = "1234-123-123456";

                    //when
                    boolean result = Bank.WR.checkAccount(account);

                    //then
                    assertThat(result).isTrue();
                }),
                dynamicTest("KEB 하나은행", () -> {
                    //given
                    String account = "123-123456-12345";

                    //when
                    boolean result = Bank.HN.checkAccount(account);

                    //then
                    assertThat(result).isTrue();
                }),
                dynamicTest("(구) 외환은행", () -> {
                    //given
                    String account = "123-123456-123";

                    //when
                    boolean result = Bank.HN.checkAccount(account);

                    //then
                    assertThat(result).isTrue();
                }),
                dynamicTest("씨티은행", () -> {
                    //given
                    String account = "123-123456-123";

                    //when
                    boolean result = Bank.CITY.checkAccount(account);

                    //then
                    assertThat(result).isTrue();
                }),
                dynamicTest("DGB 대구은행", () -> {
                    //given
                    String account = "123-12-123456-1";

                    //when
                    boolean result = Bank.DGB.checkAccount(account);

                    //then
                    assertThat(result).isTrue();
                }),
                dynamicTest("BNK 부산은행", () -> {
                    //given
                    String account = "123-1234-1234-12";

                    //when
                    boolean result = Bank.BNK.checkAccount(account);

                    //then
                    assertThat(result).isTrue();
                }),
                dynamicTest("SC 제일은행", () -> {
                    //given
                    String account = "123-12-123456";

                    //when
                    boolean result = Bank.SC.checkAccount(account);

                    //then
                    assertThat(result).isTrue();
                }),
                dynamicTest("케이뱅크", () -> {
                    //given
                    String account = "123-123-123456";

                    //when
                    boolean result = Bank.KBANK.checkAccount(account);

                    //then
                    assertThat(result).isTrue();
                }),
                dynamicTest("카카오뱅크", () -> {
                    //given
                    String account = "1234-12-1234567";

                    //when
                    boolean result = Bank.KAKAO.checkAccount(account);

                    //then
                    assertThat(result).isTrue();
                })
        );
    }

}