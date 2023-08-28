package com.server.domain.video.aop;

import com.server.domain.answer.entity.AnswerStatus;
import com.server.domain.question.service.dto.response.QuestionResponse;
import com.server.domain.video.controller.dto.request.AnswersCreateApiRequest;
import com.server.domain.video.controller.dto.request.QuestionCreateApiRequest;
import com.server.domain.video.controller.dto.request.VideoCreateUrlApiRequest;
import com.server.domain.video.service.dto.response.*;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import com.server.module.s3.service.AwsService;
import com.server.module.s3.service.dto.ImageType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.http.HttpStatus.CREATED;

@Aspect
@Component
public class VideoStubAop {

    private final AwsService awsService;

    public VideoStubAop(AwsService awsService) {
        this.awsService = awsService;
    }

    @Around("execution(* com.server.domain.video.controller.VideoController.getQuestions(..))")
    public Object getQuestions(ProceedingJoinPoint joinPoint) {

        List<QuestionResponse> questions = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            QuestionResponse questionResponse = QuestionResponse.builder()
                    .questionId((long) i)
                    .position(i)
                    .content("content" + i)
                    .myAnswer("2")
                    .questionAnswer(String.valueOf(i))
                    .answerStatus(AnswerStatus.WRONG)
                    .description("description")
                    .selections(List.of("selection1", "selection2", "selection3"))
                    .solvedDate(LocalDateTime.now())
                    .build();

            questions.add(questionResponse);
        }

        return ResponseEntity.ok(ApiSingleResponse.ok(questions, "질문 목록 조회 성공"));
    }

    @Around("execution(* com.server.domain.video.controller.VideoController.solveQuestions(..))")
    public Object solveQuestions(ProceedingJoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();
        AnswersCreateApiRequest request = (AnswersCreateApiRequest) args[1];

        List<Boolean> answers = new ArrayList<>();

        for (int i = 0; i < request.getMyAnswers().size(); i++) {
            if(i % 2 == 0) {
                answers.add(true);
            } else {
                answers.add(false);
            }
        }

        return ResponseEntity.ok(ApiSingleResponse.ok(answers, "문제 제출 성공"));
    }

    @Around("execution(* com.server.domain.video.controller.VideoController.createQuestions(..))")
    public Object createQuestions(ProceedingJoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();
        Long videoId = (Long) args[0];
        List<QuestionCreateApiRequest> requests = (List<QuestionCreateApiRequest>) args[1];

        List<Long> questionIds = IntStream.range(1, requests.size() + 1)
                .mapToObj(Long::valueOf)
                .collect(Collectors.toList());



        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/videos/" + videoId + "/questions");

        return new ResponseEntity<>(
                ApiSingleResponse.of(questionIds, CREATED, "질문 생성 성공"),
                headers,
                CREATED);
    }

    @Around("execution(* com.server.domain.video.controller.VideoController.getVideos(..))")
    public Object getVideos(ProceedingJoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();
        int page = (int) args[0];

        PageRequest pageRequest = PageRequest.of(page - 1, 10);

        awsService.getVideoUrl(9999L, "test");

        List<VideoCategoryResponse> categories = List.of(
                VideoCategoryResponse.builder()
                        .categoryId(1L)
                        .categoryName("java")
                        .build(),
                VideoCategoryResponse.builder()
                        .categoryId(2L)
                        .categoryName("react")
                        .build()
        );

        VideoChannelResponse channel1 = VideoChannelResponse.builder()
                .memberId(1L)
                .channelName("hobeen's vlog")
                .subscribes(100000)
                .imageUrl(awsService.getImageUrl("test"))
                .isSubscribed(true)
                .build();

        VideoChannelResponse channel2 = VideoChannelResponse.builder()
                .memberId(2L)
                .channelName("hobeen's cooking")
                .subscribes(50000)
                .imageUrl(awsService.getImageUrl("test22"))
                .isSubscribed(false)
                .build();

        List<VideoPageResponse> videos = new ArrayList<>();

        String videoThumbnailUrl1 = awsService.getThumbnailUrl(9999L, "test");
        String videoThumbnailUrl2 = awsService.getThumbnailUrl(9999L, "test22");

        for(int i = 1; i <= 10; i++) {

            VideoChannelResponse channel = i % 2 == 0 ? channel1 : channel2;
            String videoThumbnailUrl = i % 2 == 0 ? videoThumbnailUrl1 : videoThumbnailUrl2;
            boolean isPurchased = i % 2 == 0;

            VideoPageResponse video = VideoPageResponse.builder()
                    .videoId((long) i)
                    .videoName("title" + i)
                    .thumbnailUrl(videoThumbnailUrl)
                    .views(i * 100)
                    .price(i * 1000)
                    .star(4.5F)
                    .isPurchased(isPurchased)
                    .categories(categories)
                    .channel(channel)
                    .createdDate(LocalDateTime.now())
                    .build();

            videos.add(video);
        }

        Page<VideoPageResponse> pageVideos = new PageImpl<>(videos, pageRequest, 100);

        return ResponseEntity.ok(ApiPageResponse.ok(pageVideos, "비디오 목록 조회 성공"));
    }

    @Around("execution(* com.server.domain.video.controller.VideoController.getVideo(..))")
    public Object getVideo(ProceedingJoinPoint joinPoint) {

        VideoChannelResponse channel = VideoChannelResponse.builder()
                .memberId(1L)
                .channelName("hobeen's vlog")
                .subscribes(100000)
                .imageUrl(awsService.getImageUrl("test"))
                .isSubscribed(true)
                .build();

        String videoThumbnailUrl = awsService.getThumbnailUrl(9999L, "test");

        List<VideoCategoryResponse> categories = List.of(
                VideoCategoryResponse.builder()
                        .categoryId(1L)
                        .categoryName("java")
                        .build(),
                VideoCategoryResponse.builder()
                        .categoryId(2L)
                        .categoryName("react")
                        .build()
        );

        VideoDetailResponse video = VideoDetailResponse.builder()
                .videoId(1L)
                .videoName("title")
                .description("description")
                .thumbnailUrl(videoThumbnailUrl)
                .videoUrl(awsService.getVideoUrl(9999L, "test"))
                .views(100)
                .star(4.5F)
                .price(1000)
                .reward(10)
                .isReplied(false)
                .isPurchased(false)
                .categories(categories)
                .channel(channel)
                .createdDate(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(ApiSingleResponse.ok(video, "비디오 조회 성공"));
    }

    @Around("execution(* com.server.domain.video.controller.VideoController.getVideoCreateUrl(..))")
    public Object getVideoCreateUrl(ProceedingJoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();
        VideoCreateUrlApiRequest request = (VideoCreateUrlApiRequest) args[0];

        String videoUrl = awsService.getUploadVideoUrl(3L, request.getFileName());
        String thumbnailUrl = awsService.getUploadThumbnailUrl(3L, request.getFileName(), request.getImageType());

        VideoCreateUrlResponse response = VideoCreateUrlResponse.builder()
                .videoUrl(videoUrl)
                .thumbnailUrl(thumbnailUrl)
                .build();

        return ResponseEntity.ok(ApiSingleResponse.ok(response, "put url 생성 성공"));
    }

    @Around("execution(* com.server.domain.video.controller.VideoController.createVideo(..))")
    public Object createVideo(ProceedingJoinPoint joinPoint) {

        URI uri = URI.create("/videos/" + 1);

        return ResponseEntity.created(uri).build();
    }

    @Around("execution(* com.server.domain.video.controller.VideoController.updateVideo(..))")
    public Object updateVideo(ProceedingJoinPoint joinPoint) {

        return ResponseEntity.noContent().build();
    }

    @Around("execution(* com.server.domain.video.controller.VideoController.changeCart(..))")
    public Object changeCart(ProceedingJoinPoint joinPoint) {

        return ResponseEntity.ok(ApiSingleResponse.ok(true, "장바구니 담기 성공"));
    }

    @Around("execution(* com.server.domain.video.controller.VideoController.deleteVideo(..))")
    public Object deleteVideo(ProceedingJoinPoint joinPoint) {

        return ResponseEntity.noContent().build();
    }
}
