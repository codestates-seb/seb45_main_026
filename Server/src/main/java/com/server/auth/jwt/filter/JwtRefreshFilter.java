package com.server.auth.jwt.filter;

import static com.server.auth.util.AuthConstant.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.server.auth.jwt.service.JwtProvider;
import com.server.auth.util.AuthUtil;
import com.server.global.exception.businessexception.BusinessException;
import com.server.global.exception.businessexception.authexception.JwtNotFoundException;
import com.server.global.exception.businessexception.requestexception.RequestNotPostException;

public class JwtRefreshFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;

	public JwtRefreshFilter(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {
		if(!request.getMethod().equals("POST")){
			AuthUtil.setResponse(response, new RequestNotPostException());
		}
		else{
			try {
				String refreshToken = getRefreshToken(request);

				jwtProvider.validateToken(refreshToken);

				String refilledAccessToken =
					jwtProvider.refillAccessToken(refreshToken, ACCESS_TOKEN_EXPIRE_TIME);

				response.setHeader(AUTHORIZATION, BEARER + refilledAccessToken);
			} catch (BusinessException exception) {
				AuthUtil.setResponse(response, exception);
			} catch (Exception exception) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "알 수 없는 오류입니다. 다시 시도해주세요.");
			}
		}
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {

		return !request.getRequestURI().equals(REFRESH_URL);
	}

	private String getRefreshToken(HttpServletRequest request) {

		String refreshToken = request.getHeader(REFRESH);

		if (refreshToken == null) {
			throw new JwtNotFoundException();
		}

		return refreshToken.replace(BEARER, "");
	}
}
