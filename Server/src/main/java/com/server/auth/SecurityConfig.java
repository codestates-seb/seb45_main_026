package com.server.auth;

import java.util.List;

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
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
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
			.authorizeRequests(getAuthorizeRequests());

		return http.build();
	}

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

	private Customizer<ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry> getAuthorizeRequests() {
		return (http) -> http
			.antMatchers("/members/**").hasRole("USER")

			.antMatchers(HttpMethod.GET, "/channels/**").permitAll()
			.antMatchers("/channels/**").hasRole("USER")

			.antMatchers(HttpMethod.GET, "/videos/*/questions").hasRole("USER")
			.antMatchers(HttpMethod.GET, "/videos/**").permitAll()
			.antMatchers("/videos/**").hasRole("USER")

			.antMatchers(HttpMethod.GET, "/replies/*").permitAll()
			.antMatchers("/replies/**").hasRole("USER")

			.antMatchers("/questions/**").hasRole("USER")
			.antMatchers("/orders/**").hasRole("USER")

			.antMatchers(HttpMethod.GET, "/announcements/*").permitAll()
			.antMatchers("/announcements/**").hasRole("USER")

			.antMatchers("/auth/**").permitAll()
			.anyRequest().permitAll();
	}

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
	public DefaultOAuth2UserService defaultOAuth2UserService() {
		return new DefaultOAuth2UserService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
