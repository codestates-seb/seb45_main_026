package com.server.global.exception.businessexception.memberexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MemberBlockedException extends MemberException {

    public static final String MESSAGE = "차단된 회원입니다. 고객센터를 통해 문의해주세요. 사유 : ";
    public static final String CODE = "MEMBER-403";

    public MemberBlockedException(String reason) {
        super(CODE, HttpStatus.FORBIDDEN, MESSAGE + reason);
    }
}
