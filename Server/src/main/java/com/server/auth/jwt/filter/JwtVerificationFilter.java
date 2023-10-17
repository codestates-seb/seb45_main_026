package com.server.auth.jwt.filter;

import static com.server.auth.util.AuthConstant.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.server.global.exception.businessexception.memberexception.MemberBlockedException;
import com.server.module.redis.service.RedisService;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.server.auth.jwt.service.CustomUserDetails;
import com.server.auth.jwt.service.JwtProvider;
import com.server.auth.util.AuthUtil;
import com.server.global.exception.businessexception.BusinessException;
import com.server.global.exception.businessexception.authexception.JwtExpiredException;
import com.server.global.exception.businessexception.authexception.JwtNotValidException;

import io.jsonwebtoken.Claims;

public class JwtVerificationFilter extends OncePerRequestFilter {
	private final JwtProvider jwtProvider;
	private final RedisService redisService;

	public JwtVerificationFilter(JwtProvider jwtProvider, RedisService redisService) {
		this.jwtProvider = jwtProvider;
		this.redisService = redisService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		try{
			Claims claims = verifyClaims(request);
			setAuthenticationToContext(claims);
			MDC.put("email", claims.getSubject());

			if(redisService.isExist(claims.get(CLAIM_ID).toString())) {

				String reason = redisService.getData(claims.get(CLAIM_ID).toString());

				AuthUtil.setResponse(response, new MemberBlockedException(reason));
				return;
			}

		} catch (JwtExpiredException | JwtNotValidException jwtException) {
			AuthUtil.setResponse(response, jwtException);
			return;
		} catch(BusinessException be){
			request.setAttribute(BUSINESS_EXCEPTION, be);
		} catch(Exception e){
			request.setAttribute(EXCEPTION, e);
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {

		String accessToken = getAccessToken(request);

		return accessToken == null || !accessToken.startsWith(BEARER);
	}

	private Claims verifyClaims(HttpServletRequest request) {

		String accessToken = getAccessToken(request).replace(BEARER, "");

		return jwtProvider.getClaims(accessToken);
	}

	private String getAccessToken(HttpServletRequest request) {

		return request.getHeader(AUTHORIZATION);
	}

	private void setAuthenticationToContext(Claims claims) {

		Collection<? extends GrantedAuthority> authorities = getRoles(claims);

		CustomUserDetails principal =
			new CustomUserDetails(claims.get(CLAIM_ID, Long.class), claims.getSubject(), "", authorities);

		Authentication authentication =
			new UsernamePasswordAuthenticationToken(principal, null, authorities);

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private List<SimpleGrantedAuthority> getRoles(Claims claims) {
		return Arrays.stream(claims.get(CLAIM_AUTHORITY).toString().split(","))
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());
	}
}
