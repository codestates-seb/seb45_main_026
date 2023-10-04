package com.server.domain.adjustment.controller.dto.request;

import com.server.domain.account.domain.Bank;
import com.server.domain.adjustment.service.dto.request.AccountUpdateServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
@Builder
public class AccountUpdateApiRequest {

    @NotBlank(message = "{validation.account.name.notBlank}")
    @Size(min = 2, max = 10, message = "{validation.size}")
    private String name;
    @NotBlank(message = "{validation.account.account.notBlank}")
    @Size(min = 10, message = "{validation.size}")
    private String account;
    @NotNull(message = "{validation.account.bank.notBlank}")
    private Bank bank;

    public AccountUpdateServiceRequest toServiceRequest() {
        return AccountUpdateServiceRequest.builder()
                .name(name)
                .account(account)
                .bank(bank)
                .build();
    }


}
