package com.server.domain.reply.service;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.reply.controller.convert.ReplySort;
import com.server.domain.reply.dto.ReplyCreateServiceApi;
import com.server.domain.reply.dto.ReplyInfo;
import com.server.domain.reply.dto.ReplyUpdateServiceApi;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.repository.ReplyRepository;
import com.server.domain.video.entity.Video;
import com.server.domain.video.service.VideoService;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.replyException.ReplyDuplicateException;
import com.server.global.exception.businessexception.replyException.ReplyNotFoundException;
import com.server.global.exception.businessexception.replyException.ReplyNotValidException;
import com.server.global.testhelper.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ReplyServiceTest extends ServiceTest {

    @Autowired
    private ReplyService replyService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReplyRepository replyRepository;

    @Test
    @DisplayName("댓글과 별점을 수정한다.")
    void updateReply() {
        Member loginMember = createAndSaveMember();
        Channel channel = createAndSaveChannel(loginMember);
        Video video = createAndSaveVideo(channel);

        Long loginMemberId = loginMember.getMemberId();
        Long replyId = createAndSaveReply(loginMember, video).getReplyId();

        ReplyUpdateServiceApi updateInfo = ReplyUpdateServiceApi.builder()
                .content("updateContent")
                .star(5)
                .build();

        replyService.updateReply(loginMemberId, replyId, updateInfo);

        Reply reply = replyRepository.findById(replyId).orElse(null);
        assertThat(reply).isNotNull();
        assertThat(reply.getContent()).isEqualTo("updateContent");
        assertThat(reply.getStar()).isEqualTo(5);
    }

    @Test
    @DisplayName("댓글 한 건을 조회한다.")
    void getReply() {
        Member loginMember = createAndSaveMember();

        Channel channel = createAndSaveChannel(loginMember);
        Video video = createAndSaveVideo(channel);

        Long loginMemberId = loginMember.getMemberId();
        Long replyId = createAndSaveReply(loginMember, video).getReplyId();

        ReplyInfo reply = replyService.getReply(replyId, loginMemberId);

        assertThat(reply.getContent()).isEqualTo("content");
        assertThat(reply.getStar()).isEqualTo(1);

        // repository에서도 조회된 값을 가져와서 비교
        Reply reply2 = replyRepository.findById(replyId).orElse(null);
        assertThat(reply2).isNotNull();
        assertThat(reply2.getContent()).isEqualTo("content");
        assertThat(reply2.getStar()).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글을 삭제한다.")
    public void testDeleteReply() {
        Member member = createAndSaveMember();
        Reply reply = createAndSaveReply(member, createAndSaveVideo(createAndSaveChannel(member)));

        Long replyId = reply.getReplyId();
        Long loginMemberId = member.getMemberId();

        replyService.deleteReply(replyId, loginMemberId);

        Reply deletedReply = replyRepository.findById(replyId).orElse(null);

        assertThat(deletedReply).isNull();
    }


    @Test
    @DisplayName("댓글 목록을 페이징으로 찾아서 조회한다.")
    void getReplies() {
        Member member = createAndSaveMember();

        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        for (int i = 0; i < 100; i++) {
            Reply reply = Reply.builder()
                    .content("content" + i)
                    .star(i)
                    .member(member)
                    .video(video)
                    .build();
            replyRepository.save(reply);
        }

        Page<ReplyInfo> replies = replyService.getReplies(video.getVideoId(), 8, 10, ReplySort.CREATED_DATE, null);

        Page<ReplyInfo> replies2 = replyRepository.findAllByVideoIdPaging(video.getVideoId(), PageRequest.of(8, 10, Sort.by(Sort.Order.asc("createdDate"))));

        assertThat(replies.getTotalElements()).isEqualTo(replies2.getTotalElements());
        assertThat(replies.getNumber()).isEqualTo(replies2.getNumber());
        assertThat(replies.getSize()).isEqualTo(replies2.getSize());
    }

    @Test
    @DisplayName("댓글을 작성한다.")
    void createReply(){
        Member member = createAndSaveMember();

        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        Reply reply = Reply.builder()
                .content("description")
                .star(0)
                .member(member)
                .video(video)
                .build();

        replyRepository.save(reply);

        assertEquals(replyRepository.findById(reply.getReplyId()).get().getContent(), "description");
    }

    @Test
    @DisplayName("별점이 8인 댓글을 조회한다")
    void getRepliesWithHighStarFilter() {
        Member member = createAndSaveMember();
        memberRepository.save(member);

        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        List<Reply> replies = new ArrayList<>();

        // 별점이 8점 이상인 댓글들
        for (int i = 8; i <= 10; i++) {
            Reply reply = Reply.builder()
                    .content("content" + i)
                    .star(i)
                    .member(member)
                    .video(video)
                    .build();
            replies.add(replyRepository.save(reply));
        }

        for (int i = 1; i <= 7; i++) {
            Reply reply = Reply.builder()
                    .content("content" + i)
                    .star(i)
                    .member(member)
                    .video(video)
                    .build();
            replies.add(replyRepository.save(reply));
        }

        Page<ReplyInfo> repliesPage = replyService.getReplies(video.getVideoId(), 1, 10, ReplySort.STAR, 8);

        // 별점이 8인 댓글만 조회되었는지 확인하는 코드
        List<ReplyInfo> replyInfoList = repliesPage.getContent();
        for (ReplyInfo replyInfo : replyInfoList) {
            assertTrue(replyInfo.getStar() == 8);
        }

        // 8,9,10 - 댓글 세 개
        assertThat(repliesPage.getTotalElements()).isEqualTo(3);
        assertThat(repliesPage.getNumber()).isEqualTo(1);
        assertThat(repliesPage.getSize()).isEqualTo(10);

    }


    @Test
    @DisplayName("댓글을 최신순으로 조회한다")
    void getRepliesByCreatedDate() {
        Member member = createAndSaveMember();

        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        for (int i = 0; i < 10; i++) {
            Reply reply = Reply.builder()
                    .content("content" + i)
                    .star(i)
                    .member(member)
                    .video(video)
                    .build();
            replyRepository.save(reply);
        }

        Page<ReplyInfo> replies = replyService.getReplies(video.getVideoId(), 1, 10, ReplySort.CREATED_DATE, null);

        assertThat(replies.getTotalElements()).isEqualTo(10);
        assertThat(replies.getNumber()).isEqualTo(1);
        assertThat(replies.getSize()).isEqualTo(10);

        List<ReplyInfo> replyInfoList = replies.getContent();
        for (int i = 0; i < replyInfoList.size() - 1; i++) { //마지막까지
            assertThat(replyInfoList.get(i).getCreatedDate()).isEqualTo(replyInfoList.get(i + 1).getCreatedDate());
        }
    }

    @Test
    @DisplayName("로그인한 회원만 댓글을 작성할 수 있다.")
    void createRepliesLoginUser() {
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        Long loginId = member.getMemberId();

        Reply reply = createAndSaveReply(member, video);
        Long replyId = reply.getMember().getMemberId();

        assertThat(replyId).isEqualTo(loginId);

    }

    @Test
    @DisplayName("로그인한 회원이 아니면 자신의 댓글을 삭제할 수 없다.")
    void deleteRepliesOnlyLoginUser() {
        Member member = createAndSaveMember();
        memberRepository.save(member);

        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);
        Reply reply = createAndSaveReply(member, video);

        Long replyId = reply.getReplyId();
        Long loginMemberId = member.getMemberId();

        if (reply.getMember().getMemberId() != loginMemberId) {
            assertThrows(MemberAccessDeniedException.class, () -> replyService.deleteReply(replyId, loginMemberId));
        } else {
            assertDoesNotThrow(() -> replyService.deleteReply(replyId, loginMemberId));
        }
    }

    @Test
    @DisplayName("별점을 초과해서 부여할 수 없다")
    void notExceedStar(){
        Member member = createAndSaveMember();
        Long loginMemberId = member.getMemberId();

        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);
        Long videoId = video.getVideoId();

        ReplyCreateServiceApi request = ReplyCreateServiceApi.builder()
                .content("content")
                .star(11)
                .build();

        ReplyNotValidException exception = assertThrows(ReplyNotValidException.class, () -> {
            replyService.createReply(loginMemberId, videoId, request);
        });

        assertThat(exception).isInstanceOf(ReplyNotValidException.class);
    }

    @Test
    @DisplayName("댓글을 중복하여 작성할 수 없다")
    void cannotDuplicateReplies(){
        Member member = createAndSaveMember();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        Long loginId = member.getMemberId();
        Long videoId = video.getVideoId();

        ReplyCreateServiceApi request = ReplyCreateServiceApi.builder()
                .content("content")
                .star(10)
                .build();

        Long reply = replyService.createReply(loginId, videoId, request);

        Throwable throwable = assertThrows(ReplyDuplicateException.class, () -> {
            replyService.createReply(loginId, videoId, request);
        });

        assertThat(throwable).isInstanceOf(ReplyDuplicateException.class);
    }

    @Test
    @DisplayName("로그인한 사용자만 댓글을 수정할 수 있다.")
    public void onlyLoginUserModifyReplies() {
        Member member = createAndSaveMember();
        Long loginMemberId = member.getMemberId();
        Channel channel = createAndSaveChannel(member);
        Video video = createAndSaveVideo(channel);

        Reply reply = Reply.builder()
                .content("content")
                .star(0)
                .member(member)
                .video(video)
                .build();
        replyRepository.save(reply);

        if(reply.getMember().getMemberId() == loginMemberId){
            assertDoesNotThrow(() -> replyService.updateReply(loginMemberId, reply.getReplyId(), new ReplyUpdateServiceApi("modifyContent", 3)));
        }else{
            assertThrows(MemberAccessDeniedException.class, () -> replyService.updateReply(loginMemberId, reply.getReplyId(), new ReplyUpdateServiceApi("modifyContent", 3)));
        }
    }

}


