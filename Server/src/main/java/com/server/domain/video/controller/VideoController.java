package com.server.domain.video.controller;

import com.server.domain.question.service.QuestionService;
import com.server.domain.question.service.dto.response.QuestionResponse;
import com.server.domain.video.controller.dto.request.*;
import com.server.domain.video.service.VideoService;
import com.server.domain.video.service.dto.response.VideoCreateUrlResponse;
import com.server.domain.video.service.dto.response.VideoDetailResponse;
import com.server.domain.video.service.dto.response.VideoPageResponse;
import com.server.global.annotation.LoginId;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequestMapping("/videos")
@RestController
@Validated
public class VideoController {

    private final QuestionService questionService;
    private final VideoService videoService;

    public VideoController(QuestionService questionService, VideoService videoService) {
        this.questionService = questionService;
        this.videoService = videoService;
    }

    @GetMapping("/{video-id}/questions")
    public ResponseEntity<ApiSingleResponse<List<QuestionResponse>>> getQuestions(
                             @PathVariable("video-id") Long videoId,
                             @LoginId Long loginMemberId) {

        List<QuestionResponse> questions = questionService.getQuestions(loginMemberId, videoId);

        return ResponseEntity.ok(ApiSingleResponse.ok(questions, "질문 목록 조회 성공"));
    }

    @PostMapping("/{video-id}/answers")
    public ResponseEntity<ApiSingleResponse<List<Boolean>>> solveQuestions(
            @PathVariable("video-id") Long videoId,
            @RequestBody @Valid AnswersCreateApiRequest request,
            @LoginId Long loginMemberId) {

        List<Boolean> answers = questionService.solveQuestions(loginMemberId, videoId, request.getMyAnswers());

        return ResponseEntity.ok(ApiSingleResponse.ok(answers, "문제 제출 성공"));
    }

    @PostMapping("/{video-id}/questions")
    public ResponseEntity<ApiSingleResponse<List<Long>>> createQuestions(
            @PathVariable("video-id") Long videoId,
            @RequestBody @Valid List<QuestionCreateApiRequest> requests,
            @LoginId Long loginMemberId) {


        List<Long> questionIds = questionService.createQuestions(
                loginMemberId,
                videoId,
                QuestionCreateApiRequest.toServiceRequests(requests));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/videos/" + videoId + "/questions");

        return new ResponseEntity<>(
                ApiSingleResponse.of(questionIds, CREATED, "질문 생성 성공"),
                headers,
                CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiPageResponse<VideoPageResponse>> getVideos(@RequestParam(value = "page", defaultValue = "1") int page,
                                                                        @RequestParam(value = "size", defaultValue = "12") int size,
                                                                        @RequestParam(value = "sort", defaultValue = "created-date") VideoSort sort,
                                                                        @RequestParam(value = "category", defaultValue = "") String category,
                                                                        @RequestParam(value = "subscribe", defaultValue = "false") boolean subscribe,
                                                                        @LoginId Long loginMemberId) {

        Page<VideoPageResponse> videos = videoService.getVideos(loginMemberId, page - 1, size, sort.getSort(), category, subscribe);

        return ResponseEntity.ok(ApiPageResponse.ok(videos, "비디오 목록 조회 성공"));
    }

    @GetMapping("/{video-id}")
    public ResponseEntity<ApiSingleResponse<VideoDetailResponse>> getVideo(
                                          @PathVariable("video-id") Long videoId,
                                          @LoginId Long loginMemberId) {

        VideoDetailResponse video = videoService.getVideo(loginMemberId, videoId);

        return ResponseEntity.ok(ApiSingleResponse.ok(video, "비디오 조회 성공"));
    }

    @PostMapping("/presigned-url")
    public ResponseEntity<ApiSingleResponse<VideoCreateUrlResponse>> getVideoCreateUrl(
            @RequestBody @Valid VideoCreateUrlApiRequest request,
            @LoginId Long loginMemberId) {

        VideoCreateUrlResponse videoCreateUrl = videoService.getVideoCreateUrl(loginMemberId, request.toServiceRequest());

        return ResponseEntity.ok(ApiSingleResponse.ok(videoCreateUrl, "put url 생성 성공"));
    }

    @PostMapping
    public ResponseEntity<Void> createVideo(@RequestBody @Valid VideoCreateApiRequest request,
                                            @LoginId Long loginMemberId) {

        Long videoId = videoService.createVideo(loginMemberId, request.toServiceRequest());

        URI uri = URI.create("/videos/" + videoId);

        return ResponseEntity.created(uri).build();
    }

    @PatchMapping("/{video-id}")
    public ResponseEntity<Void> updateVideo(@RequestBody @Valid VideoUpdateApiRequest request,
                                            @PathVariable("video-id") Long videoId,
                                            @LoginId Long loginMemberId) {

        videoService.updateVideo(loginMemberId, request.toServiceRequest(videoId));

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{video-id}/carts")
    public ResponseEntity<ApiSingleResponse<Boolean>> changeCart(@PathVariable("video-id") Long videoId,
                                               @LoginId Long loginMemberId) {

        Boolean isInCart = videoService.changeCart(loginMemberId, videoId);

        String message = isInCart ? "장바구니 담기 성공" : "장바구니 취소 성공";
        return ResponseEntity.ok(ApiSingleResponse.ok(isInCart, message));
    }

    @DeleteMapping("/{video-id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable("video-id") Long videoId,
                                            @LoginId Long loginMemberId) {

        videoService.deleteVideo(loginMemberId, videoId);

        return ResponseEntity.noContent().build();
    }
}
