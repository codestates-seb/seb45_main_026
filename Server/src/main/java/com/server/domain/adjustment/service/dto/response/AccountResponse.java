package com.server.domain.adjustment.service.dto.response;

import com.server.domain.account.domain.Account;
import com.server.domain.account.domain.Bank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class AccountResponse {

    private String name;
    private String account;
    private Bank bank;

    public static AccountResponse of(Account account) {

        if(account == null) {
            return AccountResponse.builder()
                    .name("계좌 정보가 없습니다.")
                    .account("계좌 정보가 없습니다.")
                    .build();
        }

        String name = putAsteriskName(account.getName());
        String accountNumber = putAsteriskAccount(account.getAccount());

        return AccountResponse.builder()
                .name(name)
                .account(accountNumber)
                .bank(account.getBank())
                .build();
    }

    private static String putAsteriskName(String name) {
        if(name.length() == 2) {
            return name.substring(0, 1) + "*";
        }else {
            return name.substring(0, 2) + "*";
        }
    }

    private static String putAsteriskAccount(String account) {

        String[] split = account.split("-");

        if(split.length > 2) {
            StringBuilder accountNumber = new StringBuilder();

            for(int i = 0; i < split.length; i++) {
                if(i != 1) {
                    accountNumber.append(split[i]);
                }else {
                    accountNumber.append("****");
                }
                if(i != split.length - 1) {
                    accountNumber.append("-");
                }
            }

            return accountNumber.toString();
        }
        if(split.length == 2) {
            return split[0] + "-****";
        }
        return split[0].substring(0, 5) + "****";
    }
}
