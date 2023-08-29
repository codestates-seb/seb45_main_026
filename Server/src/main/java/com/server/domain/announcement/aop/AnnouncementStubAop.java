package com.server.domain.announcement.aop;

import com.server.domain.announcement.service.dto.response.AnnouncementResponse;
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

@Aspect
@Component
public class AnnouncementStubAop {

    @Around("execution(* com.server.domain.announcement.controller.AnnouncementController.getAnnouncement(..))")
    public Object getAnnouncement(ProceedingJoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();
        Long announcementId = (Long) args[0];

        AnnouncementResponse response = AnnouncementResponse.builder()
                .announcementId(announcementId)
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

        return ResponseEntity.ok(ApiSingleResponse.ok(response, "공지사항 조회 성공"));
    }

    @Around("execution(* com.server.domain.announcement.controller.AnnouncementController.updateAnnouncement(..))")
    public Object updateAnnouncement(ProceedingJoinPoint joinPoint) {

        return ResponseEntity.noContent().build();
    }

    @Around("execution(* com.server.domain.announcement.controller.AnnouncementController.deleteAnnouncement(..))")
    public Object deleteAnnouncement(ProceedingJoinPoint joinPoint) {

        return ResponseEntity.noContent().build();
    }
}
