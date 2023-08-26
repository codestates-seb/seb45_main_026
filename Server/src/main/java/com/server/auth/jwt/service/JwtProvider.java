package com.server.auth.jwt.service;

import static com.server.auth.util.AuthConstant.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import com.server.global.exception.businessexception.authexception.JwtExpiredException;
import com.server.global.exception.businessexception.authexception.JwtNotValidException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtProvider {
	@Value("${jwt.key}")
	private String key;
	private Key secretKey;

	private final JpaUserDetailsService userDetailsService;

	@PostConstruct
	protected void init() {
		secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
	}

	// 액세스 토큰 생성
	public String createAccessToken(Authentication authentication, Long tokenExpireTime) {

		return Jwts.builder()
			.setSubject(authentication.getName())
			.claim(CLAIM_ID, getId(authentication))
			.claim(CLAIM_AUTHORITY, getAuthorities(authentication))
			.setExpiration(getExpiration(tokenExpireTime))
			.signWith(secretKey, SignatureAlgorithm.HS512)
			.compact();
	}

	// 리프래쉬 토큰 생성
	public String createRefreshToken(Authentication authentication, long tokenExpireTime){

		return Jwts.builder()
			.setSubject(authentication.getName())
			.claim(CLAIM_ID, getId(authentication))
			.setExpiration(getExpiration(tokenExpireTime))
			.signWith(secretKey, SignatureAlgorithm.HS512)
			.compact();
	}

	// 리프래쉬 토큰으로 액세스 토큰 리필
	public String refillAccessToken(String refreshToken, long tokenExpireTime) {
		Claims claims = getClaims(refreshToken);
		String username = claims.getSubject();

		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
			userDetails, null, userDetails.getAuthorities());

		return createAccessToken(authentication, tokenExpireTime);
	}

	private Long getId(Authentication authentication) {

		if(authentication.getPrincipal() instanceof CustomUserDetails){
			CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
			return customUserDetails.getMemberId();
		}

		if(authentication.getPrincipal() instanceof DefaultOAuth2User){
			DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
			return principal.getAttribute(CLAIM_ID);
		}

		return null;
	}

	private String getAuthorities(Authentication authentication) {

		return authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));
	}

	private Date getExpiration(Long tokenExpireTime) {
		return new Date(new Date().getTime() + tokenExpireTime);
		// return new Date(System.currentTimeMillis() + tokenExpireTime);
	}

	public Claims getClaims(String token) {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException e) {
			throw new JwtExpiredException();
		} catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
			throw new JwtNotValidException();
		}
	}

	public void validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
		} catch (ExpiredJwtException e) {
			throw new JwtExpiredException();
		} catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
			throw new JwtNotValidException();
		}
	}
}
