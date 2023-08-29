package com.server.global.exception.businessexception.categoryexception;

import com.server.global.exception.businessexception.memberexception.MemberException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CategoryNotFoundException extends CategoryException {

    public static final String MESSAGE = "존재하지 않거나 중복된 카테고리입니다.";
    public static final String CODE = "CATEGORY-404";

    public CategoryNotFoundException() {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE);
    }
}
