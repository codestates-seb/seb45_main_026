package com.server.module.firmbank;

import org.springframework.stereotype.Service;

@Service
public class MockFirmBankService implements FirmBankService {

    @Override
    public String adjustment(String name, String account, String bank, int amount) {
        return "OK";
    }
}
