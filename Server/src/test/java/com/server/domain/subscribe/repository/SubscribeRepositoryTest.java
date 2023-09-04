package com.server.domain.subscribe.repository;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.subscribe.entity.Subscribe;
import com.server.global.testhelper.RepositoryTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class SubscribeRepositoryTest extends RepositoryTest {

    @Autowired SubscribeRepository subscribeRepository;

    @Test
    @DisplayName("member 와 channel 을 통해 subscribe 를 찾는다.")
    void findByMemberAndChannel() {
        //given
        Member owner = createAndSaveMember();
        Channel channel = createAndSaveChannel(owner);

        Member member = createAndSaveMember();

        createAndSaveSubscribe(member, channel);

        em.flush();
        em.clear();

        //when
        Subscribe subscribe = subscribeRepository.findByMemberAndChannel(member, channel).orElseThrow();

        //then
        assertThat(subscribe.getMember().getMemberId()).isEqualTo(member.getMemberId());
        assertThat(subscribe.getChannel().getChannelId()).isEqualTo(channel.getChannelId());
    }
}