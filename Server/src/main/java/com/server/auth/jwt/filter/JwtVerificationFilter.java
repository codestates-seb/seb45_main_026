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

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.auth.jwt.service.CustomUserDetails;
import com.server.auth.jwt.service.JwtProvider;
import com.server.global.exception.businessexception.BusinessException;
import com.server.global.exception.businessexception.authexception.JwtExpiredException;
import com.server.global.reponse.ApiSingleResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

public class JwtVerificationFilter extends OncePerRequestFilter {
	private final JwtProvider jwtProvider;

	public JwtVerificationFilter(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		try{
			Claims claims = verifyClaims(request);
			setAuthenticationToContext(claims);
		} catch (JwtExpiredException jwtException) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write(new ObjectMapper().writeValueAsString(ApiSingleResponse.fail(new JwtExpiredException())));
			return;
		} catch(BusinessException be){
			request.setAttribute(BUSINESS_EXCEPTION, be);
		}catch(Exception e){
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
