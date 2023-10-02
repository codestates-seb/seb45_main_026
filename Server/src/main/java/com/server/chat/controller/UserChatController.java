package com.server.chat.controller;

import com.server.auth.util.SecurityUtil;
import com.server.chat.entity.ChatMessage;
import com.server.chat.service.ChatService;
import com.server.global.reponse.ApiPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/chats")
public class UserChatController {

    private final ChatService chatService;

    public UserChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    //자신이 참여한 채팅방 조회 (페이징)
    @GetMapping("/my-rooms")
    public ResponseEntity<ApiPageResponse<ChatMessage>> getMessages(
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {

        String email = SecurityUtil.getEmail();
        Page<ChatMessage> chatRecord = chatService.getChatRecord(email, email, page - 1);

        return ResponseEntity.ok(ApiPageResponse.ok(chatRecord, "채팅 메시지 조회 성공"));
    }

    @DeleteMapping
    public ResponseEntity<Void> exitChat() {

        String email = SecurityUtil.getEmail();

        chatService.removeChatRoom(email);

        return ResponseEntity.noContent().build();
    }
}
