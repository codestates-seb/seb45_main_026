package com.server.domain.reply.service;

import com.server.domain.member.entity.Member;
import com.server.domain.member.repository.MemberRepository;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reply.dto.ReplyDto;
import com.server.domain.reply.repository.ReplyRepository;
import com.server.domain.video.entity.Video;
import com.server.domain.video.repository.VideoRepository;
import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberNotFoundException;
import com.server.global.exception.businessexception.replyException.ReplyDuplicateException;
import com.server.global.exception.businessexception.replyException.ReplyNotFoundException;
import com.server.global.exception.businessexception.videoexception.VideoNotFoundException;
import com.server.global.reponse.ApiPageResponse;
import com.server.global.reponse.PageInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;
    private final VideoRepository videoRepository;

    public ReplyService(ReplyRepository replyRepository,
                        MemberRepository memberRepository,
                        VideoRepository videoRepository) {

        this.replyRepository = replyRepository;
        this.memberRepository = memberRepository;
        this.videoRepository = videoRepository;
    }




    public List<ReplyDto.ReplyResponse> getReply(int page, Sort sort) {
        int replyList = 10;

        Pageable pageable = PageRequest.of(page, replyList, sort);
        Page<Reply> replyPage = replyRepository.findAll(pageable);

        List<ReplyDto.ReplyResponse> replyResponses = new ArrayList<>();

        for (Reply reply : replyPage.getContent()) {
            ReplyDto.ReplyResponse replyResponse = new ReplyDto.ReplyResponse(
                    reply.getReplyId(),
                    reply.getContent(),
                    reply.getMember().getNickname(),
                    reply.getStar(),
                    reply.getMember(),
                    reply.getCreatedDate()
            );
            replyResponses.add(replyResponse);
        }

        return replyResponses; // 이렇게 반환해도 되는지..? 아닌가..?
    }
    //반환타입 이거는 컨트롤러에서. replyResponse 생성해서 반환하기





    //videoId 받기, 로그인한 사용자만 댓글 남길 수 잇음, memberRepository, reply 중복 확인, 비디오 존재여부 확인
    public Reply createReply(Long memberId, Long loginMemberId, Long videoId, ReplyDto.ReplyResponse replyResponse) {
        if (memberId.equals(loginMemberId)) {

            Member member = memberRepository.findById(loginMemberId)
                    .orElseThrow(MemberNotFoundException::new);

            Video video = videoRepository.findById(videoId)
                    .orElseThrow(VideoNotFoundException::new);

            // 댓글 중복 검증 로직
            boolean isAlreadyExist = replyRepository.findByMemberAndVideo(member, video);

            if (isAlreadyExist) {
                throw new ReplyDuplicateException(); // 중복 댓글이면 예외 발생
            }

            Reply reply = Reply.builder()
                    .content(replyResponse.getContent())
                    .star(replyResponse.getStar()) // 별점... 이게 맞나..
                    .member(member)
                    .video(video)
                    .build();

            return replyRepository.save(reply);
        } else {
            throw new MemberAccessDeniedException();
        }
    }


    public Reply updateReply(Long replyId, ReplyDto.ReplyResponse response, Member loggedInMember) {


        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(ReplyNotFoundException::new);

        reply.checkPermission(loggedInMember); //수정 권한 확인

        reply.setContent(response.getContent());
        reply.setStar(response.getStar());

        return replyRepository.save(reply);
    }



    public void deleteReply(Long replyId, Member loggedInMember) {

        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(ReplyNotFoundException::new);

        if (!reply.getMember().equals(loggedInMember)) {
            throw new MemberAccessDeniedException(); //삭제 권한 확인
        }

        replyRepository.delete(reply);
    }
}
