package com.server.domain.account.domain;

import com.server.global.entity.BaseEnum;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public enum Bank implements BaseEnum {
    KB("KB 국민은행", (attribute) -> {
        String[] chunk = attribute.split("-");
        if(chunk.length == 3) {
            return checkLen(chunk[0], 6) && checkLen(chunk[1], 2) && checkLen(chunk[2], 6);
        }else if(chunk.length == 4) {
            return checkLen(chunk[0], 3) &&
                    checkLen(chunk[1], 2) &&
                    checkLen(chunk[2], 4) &&
                    checkLen(chunk[3], 3);
        }
        return false;
    }),
    IBK("IBK 기업은행", (attribute) -> {
        String[] chunk = attribute.split("-");
        if(chunk.length == 4) {
            return checkLen(chunk[0], 3) &&
                    checkLen(chunk[1], 6) &&
                    checkLen(chunk[2], 2) &&
                    checkLen(chunk[3], 3);
        }
        return false;
    }),
    NH("농협은행", (attribute) -> {
        String[] chunk = attribute.split("-");
        if(chunk.length == 4) {
            return checkLen(chunk[0], 3) &&
                    checkLen(chunk[1], 4) &&
                    checkLen(chunk[2], 4) &&
                    checkLen(chunk[3], 2);
        }
        return false;
    }),
    SH("신한은행", (attribute) -> {
        String[] chunk = attribute.split("-");
        if(chunk.length == 3) {
            return (checkLen(chunk[0], 3) &&
                    checkLen(chunk[1], 2) &&
                    checkLen(chunk[2], 6)) ||
                    (checkLen(chunk[0], 3) &&
                    checkLen(chunk[1], 3) &&
                    checkLen(chunk[2], 6));
        }
        return false;
    }),
    WR("우리은행", (attribute) -> {
        String[] chunk = attribute.split("-");
        if (chunk.length == 3) {
            return checkLen(chunk[0], 4) && checkLen(chunk[1], 3) && checkLen(chunk[2], 6);
        }
        return false;
    }),
    HN("KEB 하나은행(구 외환은행)", (attribute) -> {
        String[] chunk = attribute.split("-");
        if(chunk.length == 3) {
            return (checkLen(chunk[0], 3) &&
                    checkLen(chunk[1], 6) &&
                    checkLen(chunk[2], 5)) ||
                    (checkLen(chunk[0], 3) &&
                    checkLen(chunk[1], 6) &&
                    checkLen(chunk[2], 3));
        }
        return false;
    }),
    CITY("씨티은행", (attribute) -> {
        String[] chunk = attribute.split("-");
        if (chunk.length == 3) {
            return checkLen(chunk[0], 3) && checkLen(chunk[1], 6) && checkLen(chunk[2], 3);
        }
        return false;
    }),
    DGB("DGB 대구은행", (attribute) -> {
        String[] chunk = attribute.split("-");
        if(chunk.length == 4) {
            return checkLen(chunk[0], 3) &&
                    checkLen(chunk[1], 2) &&
                    checkLen(chunk[2], 6) &&
                    checkLen(chunk[3], 1);
        }
        return false;
    }),
    BNK("BNK 부산은행", (attribute) -> {
        String[] chunk = attribute.split("-");
        if(chunk.length == 4) {
            return checkLen(chunk[0], 3) &&
                    checkLen(chunk[1], 4) &&
                    checkLen(chunk[2], 4) &&
                    checkLen(chunk[3], 2);
        }
        return false;
    }),
    SC("SC 제일은행", (attribute) -> {
        String[] chunk = attribute.split("-");
        if (chunk.length == 3) {
            return checkLen(chunk[0], 3) && checkLen(chunk[1], 2) && checkLen(chunk[2], 6);
        }
        return false;
    }),
    KBANK("케이뱅크", (attribute) -> {
        String[] chunk = attribute.split("-");
        if (chunk.length == 3) {
            return checkLen(chunk[0], 3) && checkLen(chunk[1], 3) && checkLen(chunk[2], 6);
        }
        return false;
    }),
    KAKAO("카카오뱅크", (attribute) -> {
        String[] chunk = attribute.split("-");
        if (chunk.length == 3) {
            return checkLen(chunk[0], 4) && checkLen(chunk[1], 2) && checkLen(chunk[2], 7);
        }
        return false;
    })
    ;

    private final String description;

    private final Function<String, Boolean> checkAccount;

    public boolean checkAccount(String account) {
        return this.checkAccount.apply(account);
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    private static boolean checkLen(String chunk, int len) {
        try {
            Long.parseLong(chunk);
            return chunk.length() == len;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
