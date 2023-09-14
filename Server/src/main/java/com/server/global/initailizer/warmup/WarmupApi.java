package com.server.global.initailizer.warmup;

import com.server.auth.jwt.service.JpaUserDetailsService;
import com.server.auth.jwt.service.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Profile("prod")
@Slf4j
public class WarmupApi implements ApplicationListener<ContextRefreshedEvent> {

    private final RestTemplate restTemplate;
    private final WarmupState warmupState;
    private final JpaUserDetailsService jpaUserDetailsService;
    private final JwtProvider jwtProvider;
    private static final int WARMUP_COUNT = 100;

    public WarmupApi(RestTemplate restTemplate, WarmupState warmupState,
                     JpaUserDetailsService jpaUserDetailsService, JwtProvider jwtProvider) {
        this.restTemplate = restTemplate;
        this.warmupState = warmupState;
        this.jpaUserDetailsService = jpaUserDetailsService;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null && !warmupState.isWarmupCompleted()) {

            long startTime = System.currentTimeMillis();
            warmup();
            long endTime = System.currentTimeMillis();

            log.info("Warmup time : {} ms", endTime - startTime);

            warmupState.setWarmupCompleted(true);
        }
    }

    public void warmup() {

        request("http://localhost:8080/videos");
        request("http://localhost:8080/videos/1");
        request("http://localhost:8080/videos/1/replies");
        request("http://localhost:8080/channels/4");
        request("http://localhost:8080/channels/4/videos");
        request("http://localhost:8080/members");
        request("http://localhost:8080/members/rewards");
        request("http://localhost:8080/members/subscribes");
        request("http://localhost:8080/members/orders");
        request("http://localhost:8080/members/playlists");
        request("http://localhost:8080/members/playlists/channels");
//        request("http://localhost:8080/members/watchs");

    }

    private void request(String url) {

        HttpHeaders headers = getBasicHeader();

        HttpEntity<String> entity = new HttpEntity<>(headers);

        for (int i = 0; i < WARMUP_COUNT; i++) {
            restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
        }
    }

    private HttpHeaders getBasicHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
        headers.setBearerAuth(getAccessToken());
        headers.set("warmup", "true");
        return headers;
    }

    private String getAccessToken() {

        UserDetails userDetails = jpaUserDetailsService.loadUserByUsername("test@gmail.com");
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        return jwtProvider.createAccessToken(authentication, 3600000L);
    }
}
