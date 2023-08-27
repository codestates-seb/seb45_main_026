package com.server.global.testhelper;

import com.server.domain.channel.entity.Channel;
import com.server.domain.member.entity.Authority;
import com.server.domain.member.entity.Member;
import com.server.domain.order.entity.Order;
import com.server.domain.video.entity.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.List;

@DataJpaTest
public abstract class RepositoryTest {

    @Autowired protected EntityManager em;

    protected Member createAndSaveMember() {
        Member member = Member.builder()
                .email("test@gmail.com")
                .password("1q2w3e4r!")
                .nickname("test")
                .authority(Authority.ROLE_USER)
                .reward(1000)
                .imageFile("imageFile")
                .build();

        em.persist(member);

        return member;
    }

    protected Channel createAndSaveChannel(Member member) {
        Channel channel = Channel.createChannel("channelName");
        channel.setMember(member);
        em.persist(channel);

        return channel;
    }

    protected Video createAndSaveVideo(Channel channel) {
        Video video = Video.builder()
                .videoName("title")
                .description("description")
                .thumbnailFile("thumbnailFile")
                .videoFile("videoFile")
                .channel(channel)
                .view(0)
                .star(0)
                .price(1000)
                .build();

        em.persist(video);

        return video;
    }

    protected Order createAndSaveOrder(Member member, List<Video> video) {

        Order order = Order.createOrder(member, video, 500);

        em.persist(order);

        return order;
    }
}
