package com.server.domain.adjustment.controller.dto.request;

import com.server.domain.adjustment.service.dto.request.AccountUpdateServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@Builder
public class AccountUpdateApiRequest {

    @NotBlank(message = "{validation.account.name.notBlank}")
    private String name;
    @NotBlank(message = "{validation.account.account.notBlank}")
    private String account;
    @NotBlank(message = "{validation.account.bank.notBlank}")
    private String bank;

    public AccountUpdateServiceRequest toServiceRequest() {
        return AccountUpdateServiceRequest.builder()
                .name(name)
                .account(account)
                .bank(bank)
                .build();
    }


}
