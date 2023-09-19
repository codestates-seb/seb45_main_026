package com.server.domain.reply.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReplyTest {

    @Test
    @DisplayName("Reply를 생성한다.")
    void newReply() {
        //given
        String content = "content";

        //when
        Reply reply = createReply(content);

        //then
        assertEquals(reply.getContent(), content);
    }

    @Test
    @DisplayName("Reply를 수정한다.")
    void updateReply() {
        //given
        String content = "content";
        Reply reply = createReply(content);
        String updateContent = "updateContent";
        Integer star = 9;

        //when
        reply.updateReply(updateContent, star);

        //then
        assertEquals(reply.getContent(), updateContent);
    }

    @Test
    @DisplayName("reward를 획득한다.")
    void getRewardPoint() {
        //given
        Reply reply = createReply("content");

        //when
        int rewardPoint = reply.getRewardPoint();

        //then
        assertEquals(rewardPoint, 10);
    }

    private Reply createReply(String content) {
        return Reply.builder()
                .content(content)
                .build();
    }
}