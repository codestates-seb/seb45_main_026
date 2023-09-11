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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
        Page<Reply> replyInfoPage = replyRepository.findAllByVideoIdPaging(video.getVideoId(), PageRequest.of(0, 10));

        //then
        assertThat(replyInfoPage.getContent().size()).isEqualTo(2);
        assertThat(replyInfoPage.getContent().get(0).getReplyId()).isEqualTo(reply.getReplyId());
        assertThat(replyInfoPage.getContent().get(1).getReplyId()).isEqualTo(reply2.getReplyId());

    }

    @Test
    @DisplayName("댓글들을 별점순으로 필터링하여 반환한다")
    void findAllByVideoIdAndStarOrStarIsNull() {
        // Given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        Reply reply1 = Reply.builder()
                .content("댓글 내용 1")
                .star(4)
                .member(member)
                .video(video)
                .build();
        replyRepository.save(reply1);

        Reply reply2 = Reply.builder()
                .content("댓글 내용 2")
                .star(5)
                .member(member)
                .video(video)
                .build();
        replyRepository.save(reply2);

        Reply reply3 = Reply.builder()
                .content("댓글 내용 3")
                .star(2)
                .member(member)
                .video(video)
                .build();
        replyRepository.save(reply3);

        em.flush();
        em.clear();

        // When
        Page<Reply> replyInfoPage = replyRepository.findAllByVideoIdAndStarOrStarIsNull(video.getVideoId(), 3, PageRequest.of(0, 10));

        // Then
        assertThat(replyInfoPage.getContent().size()).isEqualTo(2); // 3점 이상의 댓글만 필터링되어야 함
        assertThat(replyInfoPage.getContent().get(0).getStar()).isEqualTo(4); // 4점 댓글
        assertThat(replyInfoPage.getContent().get(1).getStar()).isEqualTo(5); // 5점 댓글


    }

    @Test
    @DisplayName("memberId 와 videoId 로 댓글들을 찾는다")
    void findAllByMemberIdAndVideoId(){
        //given
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);
        Reply reply = createAndSaveReplies(member, video);
        Reply reply1 = createAndSaveReplies(member, video);
        Reply reply2 = createAndSaveReplies(member, video);

        em.flush();
        em.clear();

        //when
        List<Reply> findAllByMemberIdAndVideoId = replyRepository.findAllByMemberIdAndVideoId(member.getMemberId(), video.getVideoId());

        //then
        assertThat(findAllByMemberIdAndVideoId.size()).isEqualTo(3);
        assertThat(findAllByMemberIdAndVideoId.get(2).getReplyId()).isEqualTo(reply2.getReplyId());
    }
}