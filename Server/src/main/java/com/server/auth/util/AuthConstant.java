package com.server.auth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConstant {
	public static final String AUTHORIZATION = "Authorization";
	public static final String REFRESH = "Refresh";
	public static final String LOCATION = "Location";
	public static final String LOGIN_URL = "/auth/login";
	public static Long ACCESS_TOKEN_EXPIRE_TIME;
	public static Long REFRESH_TOKEN_EXPIRE_TIME;
	public static final String BEARER = "Bearer ";
	public static final String CLAIM_AUTHORITY = "auth";
	public static final String CLAIM_ID = "id";
	public static final String ALLOW = "Allow";
	public static final String REFRESH_URL = "/auth/refresh";
	public static final String BUSINESS_EXCEPTION = "businessException";
	public static final String EXCEPTION = "exception";

	@Value("${jwt.access.expiration}")
	public void setAccessTokenExpireTime(long value) {
		ACCESS_TOKEN_EXPIRE_TIME = value;
	}

	@Value("${jwt.refresh.expiration}")
	public void setRefreshTokenExpireTime(long value) {
		REFRESH_TOKEN_EXPIRE_TIME = value;
	}
}
