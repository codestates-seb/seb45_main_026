package com.server.chat.service;

import com.server.auth.util.SecurityUtil;
import com.server.chat.entity.ChatMessage;
import com.server.chat.entity.ChatRoom;
import com.server.chat.entity.MessageType;
import com.server.chat.repository.ChatRoomRepository;
import com.server.global.exception.businessexception.chatexception.ChatAlreadyAssignedException;
import com.server.global.exception.businessexception.chatexception.ChatNotValidException;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final ChatRoomRepository chatRoomRepository;

    public ChatService(ChannelTopic channelTopic,
                       RedisTemplate redisTemplate, ChatRoomRepository chatRoomRepository) {
        this.channelTopic = channelTopic;
        this.redisTemplate = redisTemplate;
        this.chatRoomRepository = chatRoomRepository;
    }

    public void sendChatMessage(ChatMessage chatMessage) {

        chatRoomRepository.addChatRecord(chatMessage.getRoomId(), chatMessage);
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);

    }

    public List<ChatRoom> getChatRooms() {

        return chatRoomRepository.findNotAssignedRoom();
    }

    public List<String> getMyAdminRooms(String email) {

        HashSet<String> userEnterRoomId = chatRoomRepository.getUserEnterRoomId(email);

        return new ArrayList<>(userEnterRoomId);

    }

    public void assignAdmin(String adminEmail, String roomId) {

        ChatRoom chatRoom = chatRoomRepository.findRoomById(roomId).orElseThrow(ChatNotValidException::new);
        if(chatRoom.isAssigned()) {
            if(!chatRoom.getAdminEmail().equals(adminEmail)) {
                throw new ChatAlreadyAssignedException();
            }
        } else {
            chatRoom.setAdmin(adminEmail);
            chatRoomRepository.assignRoom(adminEmail, chatRoom);
        }
    }

    public void createChatRoom(String roomId) {

        chatRoomRepository.findRoomById(roomId).orElseGet(() -> chatRoomRepository.createChatRoom(roomId));
    }

    public ChatRoom getChatRoom(String roomId) {

        return chatRoomRepository.findRoomById(roomId).orElseThrow(ChatNotValidException::new);
    }

    public Page<ChatMessage> getChatRecord(String email, String roomId, int page) {

        ChatRoom chatRoom = chatRoomRepository.findRoomById(roomId).orElseThrow(ChatNotValidException::new);

        if(!chatRoom.getRoomId().equals(email)) {
            if(chatRoom.isAssigned()) {
                if(!chatRoom.getAdminEmail().equals(email)) {
                    throw new ChatNotValidException();
                }
            }else {
                throw new ChatNotValidException();
            }
        }

        return chatRoomRepository.getChatRecord(roomId, page);
    }

    public void completeChat(String email, String roomId) {

            ChatRoom chatRoom = chatRoomRepository.findRoomById(roomId).orElseThrow(ChatNotValidException::new);

            if(!chatRoom.getRoomId().equals(email)) {
                if(!chatRoom.getAdminEmail().equals(email)) {
                    throw new ChatNotValidException();
                }
            }

            chatRoom.complete();
            chatRoomRepository.saveChatRoom(chatRoom);
            chatRoomRepository.removeAdminChatRoom(chatRoom.getAdminEmail(), roomId);
    }

    public void removeChatRoom(String roomId) {
        chatRoomRepository.removeChatRoom(roomId);
    }
}
