package com.server.auth;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.server.auth.jwt.filter.JwtAuthenticationFilter;
import com.server.auth.jwt.filter.JwtRefreshFilter;
import com.server.auth.jwt.filter.JwtVerificationFilter;
import com.server.auth.jwt.handler.MemberAuthenticationFailureHandler;
import com.server.auth.jwt.handler.MemberAuthenticationSuccessHandler;
import com.server.auth.jwt.service.JwtProvider;

import static com.server.auth.util.AuthConstant.*;

import javax.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {
	private JwtProvider jwtProvider;

	public SecurityConfig(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.headers().frameOptions().sameOrigin() //브라우저에서 H2 사용하기 위해 추가 나중에 삭제할 것
			.and()
			.httpBasic().disable()
			.csrf().disable()
			.cors(getCors())
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.apply(new CustomFilterConfigurer())
			.and()
			.exceptionHandling()
			.accessDeniedHandler((request, response, accessDeniedException) -> response.sendError(HttpServletResponse.SC_FORBIDDEN))
			.authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증되지 않은 사용자입니다."))
			.and()
			.authorizeRequests(getAuthorizeRequests());

		return http.build();
	}

	// Cors 설정
	@Bean
	public Customizer<CorsConfigurer<HttpSecurity>> getCors() {

		return cors -> {
			CorsConfiguration configuration = new CorsConfiguration();
			configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://www.itprometheus.net"));
			configuration.addAllowedMethod("*");
			configuration.addAllowedHeader("*");
			configuration.setAllowCredentials(true);
			configuration.setExposedHeaders(List.of(AUTHORIZATION, REFRESH, LOCATION));

			UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
			source.registerCorsConfiguration("/**", configuration);

			cors.configurationSource(source);
		};
	}

	// 요청별 인가 설정
	private Customizer<ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry> getAuthorizeRequests() {
		return (http) -> http
			.antMatchers("/auth/**").permitAll()
			.anyRequest().permitAll();
	}

	// JWT 필터가 유저네임 패스워드 자리에 들어가고 이후에 Refresh필터와 인증필터 추가
	public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
		@Override
		public void configure(HttpSecurity builder) {
			AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

			JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtProvider, authenticationManager);
			jwtAuthenticationFilter.setFilterProcessesUrl(LOGIN_URL);
			// jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberAuthenticationSuccessHandler());
			// jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberAuthenticationFailureHandler());

			JwtRefreshFilter jwtRefreshFilter = new JwtRefreshFilter(jwtProvider);
			JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtProvider);

			builder
				.addFilter(jwtAuthenticationFilter)
				.addFilterAfter(jwtRefreshFilter, JwtAuthenticationFilter.class)
				.addFilterAfter(jwtVerificationFilter, JwtRefreshFilter.class);
		}
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
