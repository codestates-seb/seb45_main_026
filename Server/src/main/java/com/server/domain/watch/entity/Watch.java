package com.server.domain.watch.entity;

import com.server.domain.member.entity.Member;
import com.server.domain.video.entity.Video;
import com.server.global.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Watch extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long watchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    public static Watch createWatch(Member member, Video video) {
        Watch watch = new Watch();
        watch.member = member;
        watch.video = video;
        return watch;
    }

    public void setLastWatchedTime(LocalDateTime lastWatchedTime) {
        this.modifiedDate = lastWatchedTime;
    }


}
