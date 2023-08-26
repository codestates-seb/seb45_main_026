package com.server.auth;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SecurityConfig {
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
			.anyRequest().permitAll()
			.and()
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
