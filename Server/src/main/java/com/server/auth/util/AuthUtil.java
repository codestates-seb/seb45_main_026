package com.server.auth.util;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.global.exception.businessexception.BusinessException;
import com.server.global.exception.businessexception.authexception.JwtExpiredException;
import com.server.global.reponse.ApiSingleResponse;

public class AuthUtil {
	static ObjectMapper objectMapper = new ObjectMapper();

	public static void setResponse(HttpServletResponse response, BusinessException be) throws IOException {
		response.setStatus(be.getHttpStatus().value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(
			objectMapper.writeValueAsString(
				ApiSingleResponse.fail(be)
			)
		);
	}
}
