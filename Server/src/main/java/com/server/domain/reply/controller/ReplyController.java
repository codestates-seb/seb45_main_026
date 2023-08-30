package com.server.domain.reply.controller;

import com.server.domain.member.entity.Member;
import com.server.domain.reply.dto.ReplyDto;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.service.ReplyService;
import com.server.global.annotation.LoginId;
import com.server.global.reponse.ApiPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/videos/{video-Id}/replies")
public class ReplyController {

    private ReplyService replyService;

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }




    @GetMapping("/replies/{video-id}")
    public ResponseEntity<ApiPageResponse<ReplyDto.ReplyResponse>> getReplies(
            @PathVariable("video-id") Long videoId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "createdDate") Sort sort,
            @RequestParam(defaultValue = "0") int star) {

        ApiPageResponse<ReplyDto.ReplyResponse> replies = (ApiPageResponse<ReplyDto.ReplyResponse>) replyService.getReply(page, sort);

        return ResponseEntity.ok(replies); //반환.. 이렇게하면 되는지..
    }


    @PostMapping("/{video-id}/replies")
    public ResponseEntity<Void> createReply(@PathVariable("video-id") Long videoId,
                                            @Positive Long memberId,
                                            @Positive @LoginId  Long loginMemberId,
                                            @RequestBody ReplyDto.ReplyResponse response) {

        Reply createdReply = replyService.createReply(memberId, loginMemberId, videoId, response);

        URI location = URI.create("/videos/" + videoId + "/replies/" + createdReply.getReplyId());

        return ResponseEntity.created(location).build();
    }



    @PatchMapping("/{reply-id}")
    public ResponseEntity<Reply> updateReply(@PathVariable("reply-id") Long replyId,
                                             @LoginId Member loginMemberId,
                                             @RequestBody ReplyDto.ReplyResponse response) {

        Reply reply = replyService.updateReply(replyId, response, loginMemberId);

        return ResponseEntity.ok(reply);
    }



    @DeleteMapping("/{reply-id}")
    public ResponseEntity<Void> deleteReply(@PathVariable("reply-id") Long replyId,
                                             @LoginId Member loginMemberId) {

        replyService.deleteReply(replyId, loginMemberId);

        return ResponseEntity.noContent().build();
    }


}
