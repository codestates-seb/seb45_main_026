package com.server.domain.channel.entity;

import com.server.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ChannelTest {

    @TestFactory
    @DisplayName("memberNickname으로 채널을 생성한다.")
    void createChannel() {
        //given
        Member member = Member.createMember("email", "password", "nickName");
        String memberNickname = "nickName";

        //when
        Channel channel = Channel.createChannel(memberNickname);

        //then
        assertThat(member.getNickname()).isEqualTo(memberNickname);
    }

    @TestFactory
    @DisplayName("channelName과 description을 수정한다.")
    Collection<DynamicTest> updateChannel() {
        //given
        String memberNickname = "nickName";
        Channel channel = Channel.createChannel(memberNickname);

        String channelName = "channelName";
        String description = "description";

        return List.of(
                DynamicTest.dynamicTest("channelName과 description을 수정한다.", () -> {
                    //when
                    channel.updateChannel(channelName, description);

                    //then
                    assertAll(
                            () -> assertThat(channel.getChannelName()).isEqualTo(channelName),
                            () -> assertThat(channel.getDescription()).isEqualTo(description)
                    );
                })
        );
    }

    @Test
    @DisplayName("member를 설정한다.")
    void setMember() {
        //given
        String memberNickname = "nickName";
        Channel channel = Channel.createChannel(memberNickname);
        Member member = Member.createMember("email", "password", "nickName");

        //when
        channel.setMember(member);

        //then
        assertThat(channel.getMember()).isEqualTo(member);
    }

    @TestFactory
    @DisplayName("구독자 수를 증가시킨다.")
    void addSubscriber() {
        //given
        String memberNickname = "nickName";
        Channel channel = Channel.createChannel(memberNickname);

        //when
        channel.addSubscriber();

        //then
        assertThat(channel.getSubscribers()).isEqualTo(1);
    }

    @Test
    @DisplayName("구독자 수를 감소시킨다.")
    void decreaseSubscribers() {
        //given
        String memberNickname = "nickName";
        Channel channel = Channel.createChannel(memberNickname);
        channel.addSubscriber();

        //when
        channel.decreaseSubscribers();

        //then
        assertThat(channel.getSubscribers()).isEqualTo(0);
    }
}