package com.server.domain.reply.dto;

import com.server.domain.member.entity.Member;
import com.server.domain.reply.entity.Reply;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;


public class ReplyDto {

    @Builder
    @Getter
    public static class ReplyResponse { ////엔티티에 정의되어잇지 않다고 안된다고 하는데
        private Long replyId;
        //private Long memberId;
        private String nickName;
        //private String imageUrl;
        private String content;
        private int star;
        private Member member;
        private LocalDateTime createdDate;


        public ReplyResponse(Long replyId,
                         String content,
                         String nickName,
                         int star,
                         Member member,
                         LocalDateTime createdDate) {

            //Long memberId,
            //String imageUrl,

            this.replyId = replyId;
            //this.memberId = memberId;
           // this.imageUrl = imageUrl;
            this.content = content;
            this.nickName = nickName;
            this.star = star;
            this.member = member;
            this.createdDate = createdDate;
        }
    }




}

