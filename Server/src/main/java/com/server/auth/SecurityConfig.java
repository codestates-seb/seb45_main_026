package com.server.auth;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.server.auth.jwt.filter.JwtAuthenticationFilter;
import com.server.auth.jwt.service.JwtProvider;

@Configuration
public class SecurityConfig {
	private JwtProvider jwtProvider;

	public SecurityConfig(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.headers().frameOptions().sameOrigin()
			.and()
			.httpBasic().disable()
			.csrf().disable()
			.cors(c -> {
					CorsConfigurationSource source = request -> {
						CorsConfiguration config = new CorsConfiguration();
						config.setAllowedOrigins(
							List.of("http://localhost:3000", "https://www.itprometheus.net")
						);
						config.setAllowedMethods(
							List.of("GET", "POST", "PUT", "DELETE", "PATCH")
						);
						config.setAllowedHeaders(List.of("*"));
						config.setAllowCredentials(true);
						config.setExposedHeaders(List.of("Authorization", "Refresh", "Location"));
						return config;
					};
					c.configurationSource(source);
				}
			)
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authorizeRequests()
			.antMatchers("/auth/test").hasRole("USER")
			.anyRequest().permitAll()
			.and()
			.addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
			.exceptionHandling()
			.accessDeniedHandler((request, response, accessDeniedException) -> {
				response.setStatus(403);
				response.setCharacterEncoding("utf-8");
				response.setContentType("text/html; charset=UTF-8");
				response.getWriter().write("권한이 없는 사용자입니다.");
			})
			.authenticationEntryPoint((request, response, authException) -> {
				response.setStatus(401);
				response.setCharacterEncoding("utf-8");
				response.setContentType("text/html; charset=UTF-8");
				response.getWriter().write("인증되지 않은 사용자입니다.");
			});

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
