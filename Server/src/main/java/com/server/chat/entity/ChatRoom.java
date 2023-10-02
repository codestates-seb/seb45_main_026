package com.server.chat.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class ChatRoom implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    private String roomId;
    private boolean assigned;
    private String adminEmail;
    private boolean isCompleted;

    public static ChatRoom create(String email) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = email;
        chatRoom.assigned = false;
        chatRoom.adminEmail = null;
        return chatRoom;
    }

    public void setAdmin(String email) {
        this.assigned = true;
        this.adminEmail = email;
    }

    public void complete() {
        this.isCompleted = true;
    }
}
