package com.server.chat.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class ChatRoom implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    private String roomId;
    private boolean assigned;
    private String adminEmail;
    private boolean isCompleted;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime inquireDate;

    public static ChatRoom create(String email) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = email;
        chatRoom.assigned = false;
        chatRoom.adminEmail = null;
        chatRoom.inquireDate = LocalDateTime.now();
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
