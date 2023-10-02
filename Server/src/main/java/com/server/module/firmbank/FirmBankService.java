package com.server.module.firmbank;

import com.server.module.firmbank.response.AdjustmentResult;

public interface FirmBankService {

    AdjustmentResult adjustment(String name, String account, String bank, int amount);
}
