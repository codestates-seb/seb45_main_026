package com.server.auth.jwt.handler;

import static com.server.auth.util.AuthConstant.*;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.server.auth.util.AuthUtil;
import com.server.global.exception.businessexception.BusinessException;
import com.server.global.exception.businessexception.authexception.JwtExpiredException;
import com.server.global.exception.businessexception.globalexception.UnknownException;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;

@Component
public class MemberAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ae) throws
		IOException {

		BusinessException businessException = (BusinessException) request.getAttribute(BUSINESS_EXCEPTION);
		Exception exception = (Exception) request.getAttribute(EXCEPTION);

		if(businessException != null){
			AuthUtil.setResponse(response, businessException);
			return;
		}

		if(ae instanceof InsufficientAuthenticationException){
			AuthUtil.setResponse(response, new MemberAccessDeniedException());
			return;
		}

		if(exception != null){
			AuthUtil.setResponse(response, new UnknownException());
		}

		AuthUtil.setResponse(response, new UnknownException());
	}
}
