package com.server.domain.video.controller;

import com.server.domain.question.service.QuestionService;
import com.server.domain.question.service.dto.response.QuestionResponse;
import com.server.domain.reply.controller.convert.ReplySort;
import com.server.domain.reply.dto.ReplyCreateControllerApi;
import com.server.domain.reply.dto.ReplyGetRequest;
import com.server.domain.reply.dto.ReplyInfo;
import com.server.domain.reply.service.ReplyService;
import com.server.domain.video.controller.dto.request.*;
import com.server.domain.video.service.VideoService;
import com.server.domain.video.service.dto.request.VideoGetServiceRequest;
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
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequestMapping("/videos")
@RestController
@Validated
public class VideoController {

    private final QuestionService questionService;
    private final VideoService videoService;
    private final ReplyService replyService;
    public VideoController(QuestionService questionService, VideoService videoService, ReplyService replyService) {
        this.questionService = questionService;
        this.videoService = videoService;
        this.replyService = replyService;
    }

    @GetMapping("/{video-id}/questions")
    public ResponseEntity<ApiSingleResponse<List<QuestionResponse>>> getQuestions(
                             @PathVariable("video-id") @Positive(message = "{validation.positive}") Long videoId,
                             @LoginId Long loginMemberId) {

        List<QuestionResponse> questions = questionService.getQuestions(loginMemberId, videoId);

        return ResponseEntity.ok(ApiSingleResponse.ok(questions, "문제 목록 조회 성공"));
    }

    @PostMapping("/{video-id}/answers")
    public ResponseEntity<ApiSingleResponse<List<Boolean>>> solveQuestions(
            @PathVariable("video-id") @Positive(message = "{validation.positive}") Long videoId,
            @RequestBody @Valid AnswersCreateApiRequest request,
            @LoginId Long loginMemberId) {

        List<Boolean> answers = questionService.solveQuestions(loginMemberId, videoId, request.getMyAnswers());

        return ResponseEntity.ok(ApiSingleResponse.ok(answers, "문제 제출 성공"));
    }

    @PostMapping("/{video-id}/questions")
    public ResponseEntity<ApiSingleResponse<List<Long>>> createQuestions(
            @PathVariable("video-id") @Positive(message = "{validation.positive}") Long videoId,
            @RequestBody @Valid List<QuestionCreateApiRequest> requests,
            @LoginId Long loginMemberId) {


        List<Long> questionIds = questionService.createQuestions(
                loginMemberId,
                videoId,
                QuestionCreateApiRequest.toServiceRequests(requests));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/videos/" + videoId + "/questions");

        return new ResponseEntity<>(
                ApiSingleResponse.of(questionIds, CREATED, "문제 생성 성공"),
                headers,
                CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiPageResponse<VideoPageResponse>> getVideos(
            @RequestParam(value = "page", defaultValue = "1") @Positive(message = "{validation.positive}") int page,
            @RequestParam(value = "size", defaultValue = "16") @Positive(message = "{validation.positive}") int size,
            @RequestParam(value = "sort", defaultValue = "created-date") VideoSort sort,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "subscribe", defaultValue = "false") boolean subscribe,
            @RequestParam(value = "free", required = false) Boolean free,
            @LoginId Long loginMemberId) {

        VideoGetServiceRequest request = VideoGetServiceRequest.builder()
                .loginMemberId(loginMemberId)
                .page(page - 1)
                .size(size)
                .categoryName(category)
                .sort(sort.getSort())
                .subscribe(subscribe)
                .free(free)
                .build();

        Page<VideoPageResponse> videos = videoService.getVideos(loginMemberId, request);

        return ResponseEntity.ok(ApiPageResponse.ok(videos, "비디오 목록 조회 성공"));
    }

    @GetMapping("/{video-id}")
    public ResponseEntity<ApiSingleResponse<VideoDetailResponse>> getVideo(
                                          @PathVariable("video-id") @Positive(message = "{validation.positive}") Long videoId,
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
    public ResponseEntity<Void> updateVideo(
            @RequestBody @Valid VideoUpdateApiRequest request,
            @PathVariable("video-id") @Positive(message = "{validation.positive}") Long videoId,
            @LoginId Long loginMemberId) {

        videoService.updateVideo(loginMemberId, request.toServiceRequest(videoId));

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{video-id}/carts")
    public ResponseEntity<ApiSingleResponse<Boolean>> changeCart(
            @PathVariable("video-id") @Positive(message = "{validation.positive}") Long videoId,
           @LoginId Long loginMemberId) {

        Boolean isInCart = videoService.changeCart(loginMemberId, videoId);

        String message = isInCart ? "장바구니 담기 성공" : "장바구니 취소 성공";
        return ResponseEntity.ok(ApiSingleResponse.ok(isInCart, message));
    }

    @DeleteMapping("/{video-id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable("video-id") @Positive(message = "{validation.positive}") Long videoId,
                                            @LoginId Long loginMemberId) {

        videoService.deleteVideo(loginMemberId, videoId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{video-id}/replies")
    public ResponseEntity<ApiPageResponse<ReplyInfo>> getReplies(
            @PathVariable("video-id") @Positive(message = "{validation.positive}") Long videoId,
            @RequestParam(defaultValue = "1") @Positive(message = "{validation.positive}") int page,
            @RequestParam(defaultValue = "10") @Positive(message = "{validation.positive}") int size,
            @RequestParam(defaultValue = "created-date") ReplySort sort,
            @RequestParam(required = false) @Positive(message = "{validation.positive}") Integer star) {

        Page<ReplyInfo> replies = videoService.getReplies(videoId, page, size, sort);

        return ResponseEntity.ok(ApiPageResponse.ok(replies, "댓글 조회 성공"));
    }

    @PostMapping("/{video-id}/replies")
    public ResponseEntity<ApiSingleResponse<Void>> createReply(
            @PathVariable("video-id") @Positive(message = "{validation.positive}") Long videoId,
            @RequestBody @Valid ReplyCreateControllerApi request,
            @LoginId Long loginMemberId) {

        Long replyId = videoService.createReply(loginMemberId, videoId, request.toService());

        URI uri = URI.create("/replies/" + replyId);

        return ResponseEntity.created(uri).build();
    }
}
