package com.server.domain.reply.controller;

import com.server.domain.reply.dto.ReplyInfo;
import com.server.domain.reply.dto.ReplyUpdateControllerApi;
import com.server.domain.reply.service.ReplyService;
import com.server.global.annotation.LoginId;
import com.server.global.reponse.ApiSingleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/replies")
@Validated
public class ReplyController {

    private final ReplyService replyService;

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }



    @PatchMapping("/{reply-id}")
    public ResponseEntity<Void> updateReply(
            @PathVariable("reply-id") @Positive(message = "{validation.positive}") Long replyId,
            @RequestBody @Valid ReplyUpdateControllerApi request,
            @LoginId Long loginMemberId) {

        replyService.updateReply(loginMemberId, replyId, request.toService());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{reply-id}")
    public ResponseEntity<ApiSingleResponse<ReplyInfo>> getReply(
            @PathVariable("reply-id") @Positive(message = "{validation.positive}") Long replyId) {

        ReplyInfo reply = replyService.getReply(replyId);

        return ResponseEntity.ok(ApiSingleResponse.ok(reply, "댓글 단건 조회 성공"));
    }

    @DeleteMapping("/{reply-id}")
    public ResponseEntity<Void> deleteReply(
            @PathVariable("reply-id") @Positive(message = "{validation.positive}") Long replyId,
            @LoginId Long loginMemberId) {

        replyService.deleteReply(replyId, loginMemberId);

        return ResponseEntity.noContent().build();
    }
}
