package com.server.domain.reply.controller;

import com.server.domain.reply.dto.ReplyRequest;
import com.server.domain.reply.dto.ReplyRequestApi;
import com.server.domain.reply.service.ReplyService;
import com.server.global.annotation.LoginId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/replies")
public class ReplyController {

    private final ReplyService replyService;

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }



    @PatchMapping("/replies/{reply-id}")
    public ResponseEntity<Void> updateReply(@PathVariable("reply-id") Long replyId,
                                            @RequestBody @Valid ReplyRequest response,
                                             @LoginId Long loginMemberId) {

        replyService.updateReply(loginMemberId, replyId, response);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{reply-id}")
    public ResponseEntity<ReplyRequestApi> getReply(@PathVariable("reply-id") Long replyId,
                                                    @LoginId Long loginMemberId) {

        ReplyRequestApi reply = replyService.getReply(replyId, loginMemberId);

        return ResponseEntity.ok(reply);
    }

    @DeleteMapping("/replies/{reply-id}")
    public ResponseEntity<Void> deleteReply(@PathVariable("reply-id") Long replyId,
                                            @LoginId Long loginMemberId) {

        replyService.deleteReply(replyId, loginMemberId);

        return ResponseEntity.noContent().build();
    }
}
