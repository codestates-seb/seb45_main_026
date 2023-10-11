package com.server.chat.service;

import com.server.chat.entity.ChatMessage;
import com.server.chat.entity.ChatRoom;
import com.server.chat.repository.ChatRoomRepository;
import com.server.chat.service.dto.response.ChatRoomResponse;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.global.exception.businessexception.chatexception.ChatAlreadyAssignedException;
import com.server.global.exception.businessexception.chatexception.ChatNotValidException;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    public ChatService(ChannelTopic channelTopic,
                       RedisTemplate redisTemplate, ChatRoomRepository chatRoomRepository,
                       MemberRepository memberRepository) {
        this.channelTopic = channelTopic;
        this.redisTemplate = redisTemplate;
        this.chatRoomRepository = chatRoomRepository;
        this.memberRepository = memberRepository;
    }

    public void sendChatMessage(ChatMessage chatMessage) {

        chatRoomRepository.addChatRecord(chatMessage.getRoomId(), chatMessage);
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);

    }

    public List<ChatRoomResponse> getChatRooms() {

        List<ChatRoom> notAssignedRoom = chatRoomRepository.findNotAssignedRoom();

        HashSet<String> notAssignedRoomIds = notAssignedRoom.stream()
                .map(ChatRoom::getRoomId)
                .collect(Collectors.toCollection(HashSet::new));

        List<Member> members = memberRepository.findAllByEmails(notAssignedRoomIds);

        List<ChatRoomResponse> responses = new ArrayList<>();

        for (ChatRoom chatRoom : notAssignedRoom) {
            Member member = members.stream()
                    .filter(m -> m.getEmail().equals(chatRoom.getRoomId()))
                    .findFirst()
                    .orElse(null);

            responses.add(ChatRoomResponse.of(chatRoom, member));
        }


        return responses;
    }

    public List<ChatRoomResponse> getMyAdminRooms(String email) {

        HashSet<String> adminEnterRoomIds = chatRoomRepository.getAdminEnterRoomId(email);

        List<Member> members = memberRepository.findAllByEmails(adminEnterRoomIds);

        List<ChatRoomResponse> responses = new ArrayList<>();

        for (String roomId : adminEnterRoomIds) {
            ChatRoom chatRoom = getValidRoom(roomId);

            Member member = members.stream()
                    .filter(m -> m.getEmail().equals(chatRoom.getRoomId()))
                    .findFirst()
                    .orElse(null);

            responses.add(ChatRoomResponse.of(chatRoom, member));

        }

        return responses;

    }

    public void assignAdmin(String adminEmail, String roomId) {

        ChatRoom chatRoom = getValidRoom(roomId);
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

        return getValidRoom(roomId);
    }

    public Page<ChatMessage> getChatRecord(String email, String roomId, int page) {

        ChatRoom chatRoom = getValidRoom(roomId);

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

    private ChatRoom getValidRoom(String roomId) {
        return chatRoomRepository.findRoomById(roomId).orElseThrow(ChatNotValidException::new);
    }

    public void completeChat(String email, String roomId) {

            ChatRoom chatRoom = getValidRoom(roomId);

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
