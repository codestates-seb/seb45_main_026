package com.server.domain.reply.controller;

import com.server.domain.reply.dto.ReplyInfo;
import com.server.domain.reply.dto.ReplyUpdateControllerApi;
import com.server.domain.reply.service.ReplyService;
import com.server.global.annotation.LoginId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/replies")
public class ReplyController {

    private final ReplyService replyService;

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }



    @PatchMapping("/replies/{reply-id}")
    public ResponseEntity<Void> updateReply(@PathVariable("reply-id")
                                            @Positive Long replyId,
                                            @RequestBody @Valid ReplyUpdateControllerApi request,
                                            @LoginId Long loginMemberId) {

        replyService.updateReply(loginMemberId, replyId, request.toService());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{reply-id}")
    public ResponseEntity<ReplyInfo> getReply(@PathVariable ("reply-id")
                                              @Positive Long replyId,
                                              @LoginId Long loginMemberId) {

        ReplyInfo reply = replyService.getReply(replyId, loginMemberId);

        return ResponseEntity.ok(reply);
    }

    @DeleteMapping("/replies/{reply-id}")
    public ResponseEntity<Void> deleteReply(@PathVariable("reply-id")
                                            @Positive Long replyId,
                                            @LoginId Long loginMemberId) {

        replyService.deleteReply(replyId, loginMemberId);

        return ResponseEntity.noContent().build();
    }
}
