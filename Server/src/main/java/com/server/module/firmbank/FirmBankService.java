package com.server.module.firmbank;

import com.server.domain.account.domain.Bank;
import com.server.module.firmbank.response.AdjustmentResult;

public interface FirmBankService {

    AdjustmentResult adjustment(String name, String account, Bank bank, int amount);
}
