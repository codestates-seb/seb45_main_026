package com.server.chat.controller;

import com.server.auth.util.SecurityUtil;
import com.server.chat.entity.ChatMessage;
import com.server.chat.entity.ChatRoom;
import com.server.chat.service.ChatService;
import com.server.chat.service.dto.response.ChatRoomResponse;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.ApiSingleResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/chats")
public class AdminChatController {

    private final ChatService chatService;

    public AdminChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    //모든 미할당 채팅방 목록 조회
    @GetMapping
    public ResponseEntity<ApiSingleResponse<List<ChatRoomResponse>>> rooms() {

        List<ChatRoomResponse> notAssignedChatRooms = chatService.getChatRooms();

        return ResponseEntity.ok(ApiSingleResponse.ok(notAssignedChatRooms, "미할당 채팅방 목록 조회 성공"));
    }

    //자신이 참여한 채팅방 목록 조회
    @GetMapping("/my-rooms")
    public ResponseEntity<ApiSingleResponse<List<ChatRoomResponse>>> myRooms() {

        String email = SecurityUtil.getEmail();

        List<ChatRoomResponse> myAdminRooms = chatService.getMyAdminRooms(email);

        return ResponseEntity.ok(ApiSingleResponse.ok(myAdminRooms, "자신이 참여한 채팅방 목록 조회 성공"));
    }

    //채팅방 이전 대화 조회
    @GetMapping("/{room-id}")
    public ResponseEntity<ApiPageResponse<ChatMessage>> getMessages(
            @PathVariable("room-id") String roomId,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {

        String email = SecurityUtil.getEmail();
        Page<ChatMessage> chatRecord = chatService.getChatRecord(email, roomId, page - 1);

        return ResponseEntity.ok(ApiPageResponse.ok(chatRecord, "채팅 메시지 조회 성공"));
    }

    @PatchMapping("/{room-id}")
    public ResponseEntity<Void> completeChat(@PathVariable("room-id") String roomId) {

        String email = SecurityUtil.getEmail();

        chatService.completeChat(email, roomId);

        return ResponseEntity.noContent().build();
    }
}
