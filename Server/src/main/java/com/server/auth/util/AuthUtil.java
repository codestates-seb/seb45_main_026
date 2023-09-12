package com.server.auth.util;

import static com.server.auth.util.AuthConstant.*;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.global.exception.businessexception.BusinessException;
import com.server.global.exception.businessexception.authexception.JwtExpiredException;
import com.server.global.reponse.ApiSingleResponse;

public class AuthUtil {
	static ObjectMapper objectMapper = new ObjectMapper();

	public static void setResponse(HttpServletResponse response, BusinessException be) throws IOException {
		response.setStatus(be.getHttpStatus().value());
		response.setContentType("application/json");
		response.setHeader(LOCATION, "/auth/refresh");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(
			objectMapper.writeValueAsString(
				ApiSingleResponse.fail(be)
			)
		);
	}

	public static void setResponse(HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.setHeader(LOCATION, "/auth/refresh");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}
}
