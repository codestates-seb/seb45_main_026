package com.server.domain.reply.service;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.repository.ReplyRepository;
import com.server.domain.video.entity.Video;
import com.server.global.exception.businessexception.replyException.ReplyNotFoundException;
import com.server.global.testhelper.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReplyServiceTest extends ServiceTest {

    @Autowired ReplyService replyService;
    @Autowired ReplyRepository replyRepository;


    @Test
    @DisplayName("수강평 목록을 페이징하여 별점순으로 반환한다(10개 단위)")
    void getReplies() {
        Member member = createAndSaveMember();
        memberRepository.save(member);

        Channel channel = createAndSaveChannel(member);
        channelRepository.save(channel);

        Video video = createAndSaveVideo(channel);
        videoRepository.save(video);

        for(int i = 0; i < 10; i++) {
            Reply reply1 = createAndSaveReply(member, video);

            replyRepository.save(reply1);
        }

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "star"));
        Page<Reply> pagedReplies = replyRepository.findAll(pageable);
        assertThat(pagedReplies.getContent().size()).isEqualTo(10);
    }

    @Test
    @DisplayName("수강평을 작성한다")
    void createReply() {
        Member member = createAndSaveMember();
        memberRepository.save(member);

        Channel channel = createAndSaveChannel(member);
        channelRepository.save(channel);

        Video video = createAndSaveVideo(channel);
        videoRepository.save(video);

        Reply reply = Reply.builder()
                .member(member)
                .content("test")
                .star(5)
                .build();

        Reply savedReply = replyRepository.save(reply);

        assertThat(savedReply.getMember()).isEqualTo(member);
        assertThat(savedReply.getContent()).isEqualTo("test");
        assertThat(savedReply.getStar()).isEqualTo(5);
    }

    @Test
    @DisplayName("수강평을 수정한다")
    void updateReply() {
        Member member = createAndSaveMember();
        memberRepository.save(member);

        Channel channel = createAndSaveChannel(member);
        channelRepository.save(channel);

        Video video = createAndSaveVideo(channel);
        videoRepository.save(video);

        Reply reply = Reply.builder()
                .member(member)
                .content("test")
                .star(5)
                .build();

        Reply savedReply = replyRepository.save(reply);

        savedReply.updateReply("update", 3);

        assertThat(savedReply.getContent()).isEqualTo("update");
        assertThat(savedReply.getStar()).isEqualTo(3);
    }

    @Test
    @DisplayName("수강평 존재유무를 확인한다.")
    void existReply() {
        Member member = createAndSaveMember();
        memberRepository.save(member);

        Channel channel = createAndSaveChannel(member);
        channelRepository.save(channel);

        Video video = createAndSaveVideo(channel);
        videoRepository.save(video);

        Reply reply = Reply.builder()
                .member(member)
                .content("test")
                .star(5)
                .build();

        Reply savedReply = replyRepository.save(reply);

        Reply existReply = replyService.existReply(savedReply.getReplyId());

        assertThat(existReply).isEqualTo(savedReply);
    }

    @Test
    @DisplayName("수강평을 삭제한다.")
    void deleteReply() {
        Member member = createAndSaveMember();
        memberRepository.save(member);

        Channel channel = createAndSaveChannel(member);
        channelRepository.save(channel);

        Video video = createAndSaveVideo(channel);
        videoRepository.save(video);

        Reply reply = Reply.builder()
                .member(member)
                .content("test")
                .star(5)
                .build();
        replyRepository.save(reply);

        replyService.deleteReply(reply.getReplyId(), member.getMemberId());

        assertThat(replyRepository.findAll().size()).isEqualTo(0);

    }

    @Test
    @DisplayName("존재하지 않는 수강평을 삭제하면 ReplyNotFoundException이 발생한다.")
    void deleteNonExistReply() {
        Member member = createAndSaveMember();
        memberRepository.save(member);

        Video video = createAndSaveVideo(member.getChannel());
        videoRepository.save(video);

        Reply reply = createAndSaveReply(member, video);
        replyRepository.save(reply);

        Long replyId = 99L;

        assertThrows(ReplyNotFoundException.class, () -> replyService.deleteReply(replyId, member.getMemberId()));
    }







}