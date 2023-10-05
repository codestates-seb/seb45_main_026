package com.server.chat.repository;

import com.server.chat.entity.ChatMessage;
import com.server.chat.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {
    // Redis CacheKeys
    private static final String CHAT_ROOMS = "CHAT_ROOM"; // 채팅룸 저장
    private static final String ADMIN_ASSIGN = "ADMIN_INFO";

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, ChatRoom> hashOpsChatRoom;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, HashSet<String>> hashOpsAdminInfo;
    @Resource(name = "redisTemplateChatMessage")
    private ZSetOperations<String, ChatMessage> zSetOpsChatRecord;

    private final StringRedisTemplate stringRedisTemplate;

    // 모든 채팅방 조회
    public List<ChatRoom> findNotAssignedRoom() {
        List<ChatRoom> allRooms = hashOpsChatRoom.values(CHAT_ROOMS);

        List<ChatRoom> notAssignedRooms = new ArrayList<>();

        for(ChatRoom chatRoom : allRooms) {
            if(!chatRoom.isAssigned()) {
                notAssignedRooms.add(chatRoom);
            }
        }

        return notAssignedRooms;
    }

    // 특정 채팅방 조회
    public Optional<ChatRoom> findRoomById(String roomId) {
        return Optional.ofNullable(hashOpsChatRoom.get(CHAT_ROOMS, roomId));
    }

    //채팅방 대화 내용 조회
    public Page<ChatMessage> getChatRecord(String roomId, int page) {
        Pageable pageable = PageRequest.of(page, 20);
        Long size = zSetOpsChatRecord.size(roomId);
        if(size == null || size == 0) {
            return Page.empty();
        }

        long start = Math.max(0, size - (page + 1) * 20L);
        long end = Math.max(0, size - page * 20L - 1);

        Set<ChatMessage> messages = zSetOpsChatRecord.range(roomId, start, end);
        List<ChatMessage> listMessages = new ArrayList<>(messages);

        return new PageImpl<>(listMessages, pageable, size);
    }


    //채팅방 대화
    public void addChatRecord(String roomId, ChatMessage chatMessage) {

        LocalDateTime now = LocalDateTime.now();
        chatMessage.setSendDate(now);
        double timestamp = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        zSetOpsChatRecord.add(roomId, chatMessage, timestamp);
    }


    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        hashOpsChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    public void assignRoom(String adminEmail, ChatRoom chatRoom) {

        if(!hashOpsAdminInfo.hasKey(ADMIN_ASSIGN, adminEmail)) {
            HashSet<String> hashSet = new HashSet<>();
            hashSet.add(chatRoom.getRoomId());
            hashOpsAdminInfo.put(ADMIN_ASSIGN, adminEmail, hashSet);
        }else {
            hashOpsAdminInfo.get(ADMIN_ASSIGN, adminEmail).add(chatRoom.getRoomId());
        }
        hashOpsChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
    }

    public void saveChatRoom(ChatRoom chatRoom) {
        hashOpsChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
    }

    public void removeAdminChatRoom(String adminEmail, String roomId) {
        if(!hashOpsAdminInfo.hasKey(ADMIN_ASSIGN, adminEmail)) {
            return;
        }
        hashOpsAdminInfo.get(ADMIN_ASSIGN, adminEmail).remove(roomId);
    }

    public void removeChatRoom(String roomId) {

        ChatRoom chatRoom = hashOpsChatRoom.get(CHAT_ROOMS, roomId);

        if(chatRoom.getAdminEmail() != null) {
            removeAdminChatRoom(chatRoom.getAdminEmail(), roomId);
        }

        hashOpsChatRoom.delete(CHAT_ROOMS, roomId);
        zSetOpsChatRecord.removeRange(roomId, 0, -1);
    }

    public HashSet<String> getUserEnterRoomId(String email) {

        if(!hashOpsAdminInfo.hasKey(ADMIN_ASSIGN, email)) {
            return new HashSet<>();
        }

        return hashOpsAdminInfo.get(ADMIN_ASSIGN, email);
    }

    public void setSessionId(String sessionId, String email) {

        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set(sessionId, email);
    }

    public String getEmailFrom(String sessionId) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(sessionId);
    }
}
