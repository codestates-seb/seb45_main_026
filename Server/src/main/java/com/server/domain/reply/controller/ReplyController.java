package com.server.domain.reply.controller;

import com.server.domain.reply.dto.ReplyDto;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.service.ReplyService;
import com.server.global.annotation.LoginId;
import com.server.global.reponse.ApiPageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/videos/{video-id}/replies")
public class ReplyController {

    private ReplyService replyService;

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }

    @GetMapping("/replies/{reply-id}")
    public ResponseEntity<ApiPageResponse<ReplyDto.ReplyResponse>> getReplies(@PathVariable("reply-id") Long replyId,
                                                           @RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "createdAt") String sort,
                                                           @RequestParam(defaultValue = "0") int star) {

        List<ReplyDto.ReplyResponse> replies = replyService.getReplies(replyId, page, sort, star);

        return null;
                //ResponseEntity.ok(ApiPageResponse.ok(replies, "댓글 조회 성공"));
    }

    @PostMapping("/replies")
    public ResponseEntity<Void> createReply(@Positive Long memberId,
                                            @LoginId Long loginMemberId,
                                            @Positive Long videoId,
                                            @RequestBody ReplyDto.ReplyResponse replyDto) {

        Reply createdReply = replyService.createReply(loginMemberId, replyDto);

        URI location = URI.create("/videos/" + videoId + "/replies/" + createdReply.getReplyId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{reply-id}")
    public ResponseEntity<Reply> updateReply(@PathVariable("reply-id") Long replyId,
                                             @LoginId Long loginMemberId,
                                             @RequestBody ReplyDto.ReplyResponse replyDto) {

        Reply reply = replyService.updateReply(replyId, replyDto);

        return ResponseEntity.ok(reply);
    }

    @DeleteMapping("/{reply-id}")
    public ResponseEntity<Void> deleteReply(@PathVariable("reply-id") Long replyId,
                                            @LoginId Long loginMemberId) {
        replyService.deleteReply(replyId, loginMemberId);
        return ResponseEntity.noContent().build();
    }
}
