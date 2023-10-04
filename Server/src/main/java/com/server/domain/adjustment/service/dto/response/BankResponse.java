package com.server.domain.adjustment.service.dto.response;

import com.server.domain.account.domain.Bank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class BankResponse {

    private String bank;
    private String description;

    public static BankResponse of(Bank bank) {
        return BankResponse.builder()
                .bank(bank.name())
                .description(bank.getDescription())
                .build();
    }
}
