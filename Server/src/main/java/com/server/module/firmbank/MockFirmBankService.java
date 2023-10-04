package com.server.module.firmbank;

import com.server.domain.account.domain.Bank;
import com.server.domain.adjustment.domain.AdjustmentStatus;
import com.server.module.firmbank.response.AdjustmentResult;
import org.springframework.stereotype.Service;

@Service
public class MockFirmBankService implements FirmBankService {

    @Override
    public AdjustmentResult adjustment(String name, String account, Bank bank, int amount) {
        return AdjustmentResult.builder()
                .status(AdjustmentStatus.ADJUSTED)
                .reason(AdjustmentStatus.ADJUSTED.getDescription())
                .build();
    }
}
