package com.server.domain.reply.controller;

import com.server.domain.reply.dto.ReplyDto;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.service.ReplyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<Reply>> getReply(@PathVariable Long videoId,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "createdAt") String sort,
                                                @RequestParam(defaultValue = "0") int star) {
        List<Reply> replies = replyService.getReply(videoId, page, sort, star);
        return ResponseEntity.ok(replies);
    }

    @PostMapping
    public ResponseEntity<Void> createReply(@PathVariable Long videoId, @RequestBody ReplyDto replyDto) {
        Reply createdReply = replyService.createReply(videoId, replyDto);
        URI location = URI.create("/videos/" + videoId + "/replies/" + createdReply.getReplyId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{replyId}")
    public ResponseEntity<Reply> updateReply(@PathVariable Long videoId,
                                             @PathVariable Long replyId,
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
