package com.server.global.initailizer.warmup;

import com.server.auth.jwt.service.JpaUserDetailsService;
import com.server.auth.jwt.service.JwtProvider;
import com.server.domain.channel.controller.ChannelController;
import com.server.domain.member.controller.MemberController;
import com.server.domain.member.controller.dto.PlaylistsSort;
import com.server.domain.reply.controller.convert.ReplySort;
import com.server.domain.video.controller.VideoController;
import com.server.domain.video.controller.dto.request.VideoSort;
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
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Profile("prod")
@Slf4j
public class WarmupApi implements ApplicationListener<ContextRefreshedEvent> {

    private final MemberController memberController;
    private final VideoController videoController;
    private final ChannelController channelController;

    private final RestTemplate restTemplate;
    private final WarmupState warmupState;
    private final JpaUserDetailsService jpaUserDetailsService;
    private final JwtProvider jwtProvider;
    private static final int WARMUP_COUNT = 100;

    public WarmupApi(MemberController memberController, VideoController videoController,
                     ChannelController channelController,
                     RestTemplate restTemplate, WarmupState warmupState,
                     JpaUserDetailsService jpaUserDetailsService, JwtProvider jwtProvider) {
        this.memberController = memberController;
        this.videoController = videoController;
        this.channelController = channelController;
        this.restTemplate = restTemplate;
        this.warmupState = warmupState;
        this.jpaUserDetailsService = jpaUserDetailsService;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (event.getApplicationContext().getParent() == null && !warmupState.isWarmupCompleted()) {

            long startTime = System.currentTimeMillis();
            request("http://localhost:8080/warmup");
            methodWarmup();
            long endTime = System.currentTimeMillis();

            log.info("Warmup time : {} ms", endTime - startTime);

            warmupState.setWarmupCompleted(true);
        }
    }

    private void methodWarmup() {

        videoMethodWarmup();
        channelMethodWarmup();
        memberMethodWarmup();
    }

    private void videoMethodWarmup() {

        for (int i = 0; i < WARMUP_COUNT; i++) {

            videoController.getVideos(1, 10, VideoSort.CREATED_DATE, "aws", true, null, true, 1L);
            videoController.getVideo(1L, 1L);
            videoController.getReplies(1L, 1, 10, ReplySort.CREATED_DATE, 3);

        }
    }

    private void channelMethodWarmup() {

        for (int i = 0; i < WARMUP_COUNT; i++) {

            channelController.getChannel(4L, 1L);
            channelController.getChannelVideos(4L, 1, 10, VideoSort.CREATED_DATE, "aws", true, false, 1L);

        }
    }

    private void memberMethodWarmup() {

        for (int i = 0; i < WARMUP_COUNT; i++) {

            memberController.getRewards(1, 10, 1L);
            memberController.getSubscribes(1, 10, 1L);
            memberController.getOrders(1L, 1, 10, 3);
            memberController.getPlaylists(1L, 1, 10, PlaylistsSort.NAME);
            memberController.getPlaylistChannels(1L, 1, 10);
            memberController.getPlaylistChannelDetails(1L, 1, 10, 4L);

        }
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
