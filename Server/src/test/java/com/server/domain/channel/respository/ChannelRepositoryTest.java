package com.server.domain.channel.respository;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.global.testhelper.RepositoryTest;
import org.assertj.core.api.Assertions;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ChannelRepositoryTest extends RepositoryTest {

    @Autowired ChannelRepository channelRepository;

    @Test
    @DisplayName("memberId 를 통해 해당 멤버의 채널을 찾는다. member 가 초기화되어있다.")
    void findByMember() {
        //given
        Member member = createMemberWithChannel();

        //when
        Channel findChannel = channelRepository.findByMember(member.getMemberId()).orElseThrow();

        //then
        assertAll(
                () -> assertThat(findChannel.getMember().getMemberId()).isEqualTo(member.getMemberId()),
                () -> assertThat(Hibernate.isInitialized(findChannel.getMember())).isTrue()
        );
    }
}