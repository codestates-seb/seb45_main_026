package com.server.module.firmbank;

public interface FirmBankService {

    String adjustment(String name, String account, String bank, int amount);
}
