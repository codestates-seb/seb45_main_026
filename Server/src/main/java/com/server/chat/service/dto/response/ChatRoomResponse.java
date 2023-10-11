package com.server.chat.service.dto.response;

import com.server.chat.entity.ChatRoom;
import com.server.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class ChatRoomResponse {

    private String roomId;
    private Long memberId;
    private String nickname;
    private LocalDateTime inquireDate;

    public static ChatRoomResponse of(ChatRoom chatRoom, Member member) {

        if(member == null) {
            return ChatRoomResponse.builder()
                    .roomId(chatRoom.getRoomId())
                    .memberId(null)
                    .nickname("탈퇴한 회원입니다.")
                    .inquireDate(chatRoom.getInquireDate())
                    .build();
        }
        return ChatRoomResponse.builder()
                .roomId(chatRoom.getRoomId())
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .inquireDate(chatRoom.getInquireDate())
                .build();
    }
}
