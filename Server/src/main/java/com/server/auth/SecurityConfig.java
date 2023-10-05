package com.server.auth;

import java.util.List;

import com.server.module.redis.service.RedisService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.server.auth.jwt.filter.JwtAuthenticationFilter;
import com.server.auth.jwt.filter.JwtRefreshFilter;
import com.server.auth.jwt.filter.JwtVerificationFilter;
import com.server.auth.jwt.handler.MemberAuthenticationEntryPoint;
import com.server.auth.jwt.service.JwtProvider;

import static com.server.auth.util.AuthConstant.*;

import javax.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {
	private final JwtProvider jwtProvider;
	private final RedisService redisService;

	public SecurityConfig(JwtProvider jwtProvider, RedisService redisService) {
		this.jwtProvider = jwtProvider;
		this.redisService = redisService;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.headers().frameOptions().sameOrigin()
				.and()
			.httpBasic().disable()
			.csrf().disable()
			.cors(getCors())
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.apply(new CustomFilterConfigurer())
			.and()
			.oauth2Login()
			.and()
			.exceptionHandling()
			.accessDeniedHandler((request, response, accessDeniedException) -> response.sendError(HttpServletResponse.SC_FORBIDDEN))
			.authenticationEntryPoint(new MemberAuthenticationEntryPoint())
			.and()
			.authorizeRequests(getAuthorizeRequests())
		;

		return http.build();
	}

	@Bean
	public Customizer<CorsConfigurer<HttpSecurity>> getCors() {

		return cors -> {
			CorsConfiguration configuration = new CorsConfiguration();
			configuration.setAllowedOrigins(List.of(
					"http://localhost:3000",
					"https://www.itprometheus.net",
					"https://admin.itprometheus.net",
					"file://", "http://jxy.me"));
			configuration.addAllowedMethod("*");
			configuration.addAllowedHeader("*");
			configuration.setAllowCredentials(true);
			configuration.setExposedHeaders(List.of(AUTHORIZATION, REFRESH, LOCATION));

			UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
			source.registerCorsConfiguration("/**", configuration);

			cors.configurationSource(source);
		};
	}

	private Customizer<ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry> getAuthorizeRequests() {
		return (http) -> http
			.antMatchers("/members/**").hasAnyRole("USER", "ADMIN")

			.antMatchers(HttpMethod.GET, "/channels/**").permitAll()
			.antMatchers("/channels/**").hasAnyRole("USER", "ADMIN")

			.antMatchers(HttpMethod.GET, "/videos/reports").hasAnyRole("ADMIN")
			.antMatchers(HttpMethod.GET, "/videos/*/reports").hasAnyRole("ADMIN")
			.antMatchers(HttpMethod.GET, "/videos/*/questions").hasAnyRole("USER", "ADMIN")
			.antMatchers(HttpMethod.GET, "/videos/**").permitAll()
			.antMatchers("/videos/**").hasAnyRole("USER", "ADMIN")

			.antMatchers(HttpMethod.GET, "/replies/*").permitAll()
			.antMatchers("/replies/**").hasAnyRole("USER", "ADMIN")

			.antMatchers("/questions/**").hasAnyRole("USER", "ADMIN")
			.antMatchers("/orders/**").hasAnyRole("USER", "ADMIN")

			.antMatchers(HttpMethod.GET, "/announcements/*").permitAll()
			.antMatchers("/announcements/**").hasAnyRole("USER", "ADMIN")

			.antMatchers("/reports/**").hasAnyRole("ADMIN")

			.antMatchers("/admin/**").hasAnyRole("ADMIN")

			.antMatchers("/user/**").hasAnyRole("USER", "ADMIN")

			.antMatchers("/auth/**").permitAll()
			.anyRequest().permitAll();
	}

	public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
		@Override
		public void configure(HttpSecurity builder) {
			AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

			JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtProvider, authenticationManager, redisService);
			jwtAuthenticationFilter.setFilterProcessesUrl(LOGIN_URL);
			// jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberAuthenticationSuccessHandler());
			// jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberAuthenticationFailureHandler());

			JwtRefreshFilter jwtRefreshFilter = new JwtRefreshFilter(jwtProvider);
			JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtProvider, redisService);

			builder
				.addFilter(jwtAuthenticationFilter)
				.addFilterAfter(jwtRefreshFilter, JwtAuthenticationFilter.class)
				.addFilterAfter(jwtVerificationFilter, JwtRefreshFilter.class);
		}
	}

	@Bean
	public DefaultOAuth2UserService defaultOAuth2UserService() {
		return new DefaultOAuth2UserService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
