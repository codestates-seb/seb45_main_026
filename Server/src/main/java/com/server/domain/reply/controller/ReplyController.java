package com.server.domain.reply.controller;

import com.server.domain.reply.controller.convert.ReplySort;
import com.server.domain.reply.dto.ReplyUpdate;
import com.server.domain.reply.dto.ReplyResponse;
import com.server.domain.reply.service.ReplyService;
import com.server.global.annotation.LoginId;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/videos")
public class ReplyController {

    private final ReplyService replyService;

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }

    @GetMapping("/replies/{reply-id}")
    public ResponseEntity<ApiPageResponse<ReplyResponse>> getReplies(@PathVariable("reply-id") Long replyId,
                                                                     @RequestParam(defaultValue = "1") int page,
                                                                     @RequestParam(defaultValue = "10") int size,
                                                                     @RequestParam(defaultValue = "created-date") ReplySort sort) {

        Page<ReplyResponse> replies = replyService.getReplies(replyId, page -1, size, sort.getSort());

        return ResponseEntity.ok(ApiPageResponse.ok(replies, "댓글 조회 성공"));
    }

    @PostMapping("{video-id}/replies")
    public ResponseEntity<ApiSingleResponse<ReplyUpdate>> createReply(@PathVariable("video-id") Long videoId,
                                                                      @LoginId Long loginMemberId,
                                                                      @RequestBody ReplyUpdate request) {

        ReplyUpdate createdReply = ReplyUpdate.of(replyService.createReply(loginMemberId, request));


        return ResponseEntity.ok(ApiSingleResponse.ok(createdReply, "댓글 생성 성공"));
    }

    @PatchMapping("/replies/{reply-id}")
    public ResponseEntity<ReplyUpdate> updateReply(@PathVariable("reply-id") Long replyId,
                                                   @LoginId Long loginMemberId,
                                                   @RequestBody ReplyUpdate replyUpdate) {

        replyService.updateReply(loginMemberId, replyId, replyUpdate);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/replies/{reply-id}")
    public ResponseEntity<Void> deleteReply(@PathVariable("reply-id") Long replyId,
                                            @LoginId Long loginMemberId) {

        replyService.deleteReply(replyId, loginMemberId);

        return ResponseEntity.noContent().build();
    }
}
