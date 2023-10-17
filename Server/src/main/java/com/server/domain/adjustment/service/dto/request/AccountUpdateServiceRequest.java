package com.server.domain.adjustment.service.dto.request;

import com.server.domain.account.domain.Bank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class AccountUpdateServiceRequest {

    private String name;
    private String account;
    private Bank bank;
}
