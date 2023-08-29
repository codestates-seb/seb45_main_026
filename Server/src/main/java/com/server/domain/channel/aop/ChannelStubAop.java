package com.server.domain.channel.aop;

import com.server.domain.announcement.service.dto.response.AnnouncementResponse;
import com.server.domain.order.service.dto.response.OrderResponse;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Aspect
@Component
public class ChannelStubAop {

    @Around("execution(* com.server.domain.channel.controller.ChannelController.createAnnouncement(..))")
    public Object createAnnouncement(ProceedingJoinPoint joinPoint) {

        URI uri = URI.create("/announcements/1");

        return ResponseEntity.created(uri).build();
    }

    @Around("execution(* com.server.domain.channel.controller.ChannelController.getAnnouncements(..))")
    public Object getAnnouncements(ProceedingJoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();
        int page = (int) args[1];
        int size = (int) args[2];

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        AnnouncementResponse response1 = AnnouncementResponse.builder()
                .announcementId(3L)
                .content("안녕하세요 여러분\n" +
                        "\n" +
                        "오늘 8월 21일 오후 6시 전장 시즌5 사전 체험이 있습니다!\n" +
                        "\n" +
                        "https://www.twitch.tv/yusin2796 채널에서 시청 가능하시고요\n" +
                        "\n" +
                        "와서 시청하시기만 해도 추첨을 통해 전장 시즌 패스 드리니깐 \n" +
                        "\n" +
                        "많이 놀러와 주세요!\n" +
                        "\n" +
                        "추후에 유튜브에도 올라옵니다!\n ")
                .createdDate(LocalDateTime.now().minusDays(1))
                .build();

        AnnouncementResponse response2 = AnnouncementResponse.builder()
                .announcementId(2L)
                .content("최근 녹화가 소리없이 녹화가 되어버렸네요\n" +
                        "시청에 불편드려 죄송하다랄까\n" +
                        "다시 다운받아서 재업하겠다\n" +
                        "감사하다")
                .createdDate(LocalDateTime.now().minusDays(2))
                .build();

        AnnouncementResponse response3 = AnnouncementResponse.builder()
                .announcementId(1L)
                .content("오늘 경기 응원해주신 모든 분들 감사드립니다!\n" +
                        "직접 와주신 분들도 모두 감사드립니다!\n" +
                        "(와주신 작순이분은 X500 감사드립니다)\n" +
                        "대회는 2등으로 마무리했습니다\n" +
                        "고마워요 다들~")
                .createdDate(LocalDateTime.now().minusDays(3))
                .build();

        Page<AnnouncementResponse> response = new PageImpl<>(List.of(response1, response2, response3), pageRequest, 100);

        return ResponseEntity.ok(ApiPageResponse.ok(response, "공지사항 목록 조회 성공"));
    }
}
