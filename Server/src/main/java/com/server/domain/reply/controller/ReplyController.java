package com.server.domain.reply.controller;

import com.server.domain.reply.controller.convert.ReplySort;
import com.server.domain.reply.dto.ReplyInfo;
import com.server.domain.reply.dto.ReplyResponse;
import com.server.domain.reply.service.ReplyService;
import com.server.global.annotation.LoginId;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/replies")
public class ReplyController {

    private final ReplyService replyService;

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }



    @PatchMapping("/replies/{reply-id}")
    public ResponseEntity<ReplyResponse> updateReply(@PathVariable("reply-id") Long replyId,
                                             @LoginId Long loginMemberId,
                                             @RequestBody ReplyResponse replyUpdate) {

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
