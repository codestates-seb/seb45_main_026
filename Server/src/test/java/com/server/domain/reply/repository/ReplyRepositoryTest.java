package com.server.domain.reply.repository;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.reply.dto.ReplyInfo;
import com.server.domain.reply.entity.Reply;
import com.server.domain.video.entity.Video;
import com.server.global.testhelper.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReplyRepositoryTest extends RepositoryTest {

    @Autowired ReplyRepository replyRepository;

    @Test
    @DisplayName("videoId 로 모든 reply 를 찾는다.")
    void findAllByVideoIdPaging() {
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);

        Video video = createAndSaveVideo(channel);

        Reply reply = createAndSaveReplies(member, video);
        Reply reply2 = createAndSaveReplies(member, video);

        em.flush();
        em.clear();

        //when
        Page<ReplyInfo> replyInfoPage = replyRepository.findAllByVideoIdPaging(video.getVideoId(), PageRequest.of(0, 10));

        //then
        assertThat(replyInfoPage.getContent().size()).isEqualTo(2);
        assertThat(replyInfoPage.getContent().get(0).getReplyId()).isEqualTo(reply.getReplyId());
        assertThat(replyInfoPage.getContent().get(1).getReplyId()).isEqualTo(reply2.getReplyId());

    }





}