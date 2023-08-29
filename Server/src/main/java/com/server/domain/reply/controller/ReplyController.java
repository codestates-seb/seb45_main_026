package com.server.domain.reply.controller;

import com.server.domain.reply.dto.ReplyDto;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.service.ReplyService;
import com.server.global.annotation.LoginId;
import com.server.global.reponse.ApiPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/videos/{videoId}/replies")
public class ReplyController {

    private ReplyService replyService;

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }

    @GetMapping
    public ApiPageResponse<Reply> getReply(@PathVariable Long videoId,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "createdDate") Sort sort,
                                           @RequestParam(defaultValue = "0") int star) {
        ApiPageResponse<Reply> replies = replyService.getReply(page, sort);
        return ResponseEntity.ok(replies).getBody();
    }

    @PostMapping
    public ResponseEntity<Void> createReply(@PathVariable Long videoId,
                                            @Positive Long memberId,
                                            @Positive @LoginId  Long loginMemberId,
                                            @RequestBody ReplyDto replyDto) {

        Reply createdReply = replyService.createReply(memberId, loginMemberId, replyDto);

        URI location = URI.create("/videos/" + videoId + "/replies/" + createdReply.getReplyId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{replyId}")
    public ResponseEntity<Reply> updateReply(@PathVariable Long replyId,
                                             @RequestBody ReplyDto replyDto) {
        Reply reply = replyService.updateReply(replyId, replyDto);
        return ResponseEntity.ok(reply);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long replyId) {
        replyService.deleteReply(replyId);
        return ResponseEntity.noContent().build();
    }


}
